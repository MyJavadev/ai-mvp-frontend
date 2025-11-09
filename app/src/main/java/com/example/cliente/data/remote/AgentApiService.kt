package com.example.cliente.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API Service para Agent según documentación del backend.
 *
 * Endpoint disponible:
 * - POST /agent - Envía un prompt a Gemini que puede llamar herramientas para tareas
 */
interface AgentApiService {

    /**
     * Envía un prompt a Gemini.
     *
     * POST /agent
     * Body: { "prompt": "Que deberia hacer hoy?" }
     * Response: { "text": "..." } o { "toolResult": {...} }
     */
    @POST("agent")
    suspend fun chat(
        @Body request: AgentRequest
    ): AgentResponse
}

@Serializable
data class AgentRequest(
    val prompt: String
)

@Serializable
data class AgentResponse(
    val text: String? = null,
    val toolResult: JsonObject? = null
)

