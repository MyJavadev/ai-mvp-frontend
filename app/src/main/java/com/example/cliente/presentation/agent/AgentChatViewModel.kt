package com.example.cliente.presentation.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.remote.AgentResponse
import com.example.cliente.data.repository.AgentRepository
import com.example.cliente.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val toolResult: String? = null
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
                    _chatState.value = _chatState.value.copy(isLoading = false)
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
                // El agente ejecutó una herramienta
                // toolResult puede ser string u objeto JSON
                val toolResultText = when (response.toolResult) {
                    is JsonPrimitive -> {
                        // Es un valor primitivo (string, number, boolean)
                        response.toolResult.contentOrNull ?: response.toolResult.toString()
                    }
                    else -> {
                        // Es un objeto o array complejo
                        response.toolResult.toString()
                    }
                }

                ChatMessage(
                    text = toolResultText,
                    isUser = false,
                    toolResult = null // No mostrar metadata adicional
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

