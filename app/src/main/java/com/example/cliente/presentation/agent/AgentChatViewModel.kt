package com.example.cliente.presentation.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.remote.AgentResponse
import com.example.cliente.data.repository.AgentRepository
import com.example.cliente.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val toolResult: String? = null,
    val metadata: String? = null
)

data class AgentChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para el chat con el Agente de IA.
 *
 * El agente puede ejecutar herramientas:
 * - add_task: Añadir tarea
 * - get_tasks: Obtener tareas
 * - update_task_status: Actualizar estado de tarea
 * - get_daily_recommendations: Obtener recomendaciones del día
 */
@HiltViewModel
class AgentChatViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _chatState = MutableStateFlow(AgentChatState())
    val chatState: StateFlow<AgentChatState> = _chatState.asStateFlow()

    init {
        // Mensaje de bienvenida del agente
        addMessage(
            ChatMessage(
                text = "¡Hola! Soy tu asistente de aprendizaje. Puedo ayudarte con:\n\n" +
                        "📝 Gestionar tus tareas\n" +
                        "💡 Darte recomendaciones diarias\n" +
                        "📚 Sugerirte qué estudiar\n\n" +
                        "¿En qué puedo ayudarte hoy?",
                isUser = false
            )
        )
    }

    /**
     * Envía un mensaje al agente de IA.
     * POST /agent
     */
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Agregar mensaje del usuario
        addMessage(ChatMessage(text = userMessage, isUser = true))

        // Indicar que está procesando
        _chatState.value = _chatState.value.copy(isLoading = true)

        agentRepository.sendPrompt(userMessage).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val response = result.data
                    if (response != null) {
                        val agentMessage = formatAgentResponse(response)
                        addMessage(agentMessage)
                    }
                    _chatState.value = _chatState.value.copy(isLoading = false, error = null)
                }
                is Resource.Error -> {
                    addMessage(
                        ChatMessage(
                            text = "❌ ${result.message ?: "Error al comunicarse con el agente"}",
                            isUser = false
                        )
                    )
                    _chatState.value = _chatState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _chatState.value = _chatState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Formatea la respuesta del agente según si es texto o resultado de herramienta.
     */
    private fun formatAgentResponse(response: AgentResponse): ChatMessage {
        return when {
            response.text != null -> {
                ChatMessage(
                    text = response.text,
                    isUser = false
                )
            }
            response.toolResult != null -> {
                val friendlyText = response.toolResult.prettyToolResult()
                ChatMessage(
                    text = friendlyText,
                    isUser = false,
                    metadata = "tool"
                )
            }
            else -> {
                ChatMessage(
                    text = "⚠️ Respuesta inesperada del agente",
                    isUser = false
                )
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _chatState.value = _chatState.value.copy(
            messages = _chatState.value.messages + message
        )
    }

    fun clearError() {
        _chatState.value = _chatState.value.copy(error = null)
    }

    fun clearChat() {
        _chatState.value = AgentChatState()
        // Volver a agregar mensaje de bienvenida
        addMessage(
            ChatMessage(
                text = "Chat reiniciado. ¿En qué puedo ayudarte?",
                isUser = false
            )
        )
    }
}

private fun JsonElement.prettyToolResult(): String {
    return when (this) {
        is JsonPrimitive -> this.contentOrNull?.ifBlank { null } ?: "La herramienta no devolvió detalles."
        is JsonObject -> renderToolObject(this)
        is JsonArray -> renderToolArray(this)
        else -> toString()
    }
}

private fun renderToolArray(array: JsonArray): String {
    if (array.isEmpty()) return "No hay elementos para mostrar."
    val builder = StringBuilder()
    array.forEachIndexed { index, element ->
        builder.appendLine("• ${index + 1}. ${element.prettyToolResult()}")
    }
    return builder.toString().trim()
}

private fun renderToolObject(obj: JsonObject): String {
    val type = obj["type"]?.jsonPrimitive?.contentOrNull
    return when (type) {
        "tasks" -> formatTaskPayload(obj)
        "mood_log" -> formatMoodLogPayload(obj)
        "preferences" -> formatPreferencesPayload(obj)
        else -> defaultObjectDescription(obj)
    }
}

private fun formatTaskPayload(obj: JsonObject): String {
    val items = obj["items"]?.jsonArray ?: return "No hay tareas registradas."
    if (items.isEmpty()) {
        return "No tienes tareas pendientes. ¿Quieres agregar alguna actividad para hoy?"
    }
    val builder = StringBuilder("Estas son tus tareas:")
    items.forEachIndexed { index, element ->
        val task = element.jsonObject
        val title = task["title"]?.jsonPrimitive?.contentOrNull ?: "Tarea sin título"
        val status = task["status"]?.jsonPrimitive?.contentOrNull ?: "pendiente"
        val due = task["dueDate"]?.jsonPrimitive?.contentOrNull
        builder.appendLine()
        builder.append("${index + 1}. $title (${status.lowercase()}")
        due?.let { builder.append(", vence $it") }
        builder.append(")")
    }
    builder.appendLine()
    builder.append("¿Deseas que agende algo más o marque una tarea como completa?")
    return builder.toString().trim()
}

private fun formatMoodLogPayload(obj: JsonObject): String {
    val mood = obj["mood"]?.jsonPrimitive?.contentOrNull ?: "sin especificar"
    val energy = obj["energy"]?.jsonPrimitive?.contentOrNull
    val stress = obj["stress"]?.jsonPrimitive?.contentOrNull
    val note = obj["note"]?.jsonPrimitive?.contentOrNull
    val builder = StringBuilder("Se registró tu estado de ánimo: $mood")
    energy?.let { builder.append(", energía $it/10") }
    stress?.let { builder.append(", estrés $it/10") }
    note?.takeIf { it.isNotBlank() }?.let { builder.append(". Nota: $it") }
    builder.append(". ¡Sigue escuchando a tu cuerpo!")
    return builder.toString()
}

private fun formatPreferencesPayload(obj: JsonObject): String {
    val likes = obj["likes"]?.jsonArray
    val dislikes = obj["dislikes"]?.jsonArray
    val builder = StringBuilder("Actualicé tus preferencias.")
    likes?.takeIf { it.isNotEmpty() }?.let {
        builder.appendLine()
        builder.append("Te gustan: ${it.joinToString { item -> item.jsonPrimitive.contentOrNull ?: "" }}")
    }
    dislikes?.takeIf { it.isNotEmpty() }?.let {
        builder.appendLine()
        builder.append("Prefieres evitar: ${it.joinToString { item -> item.jsonPrimitive.contentOrNull ?: "" }}")
    }
    builder.append(". Gracias por compartirlo, así puedo sugerirte mejores actividades.")
    return builder.toString().trim()
}

private fun defaultObjectDescription(obj: JsonObject): String {
    return buildString {
        appendLine("Tengo nuevos datos para ti:")
        obj.entries.forEach { (key, value) ->
            appendLine("- ${key.replaceFirstChar { it.uppercase() }}: ${value.prettyToolResult()}")
        }
    }.trim()
}
