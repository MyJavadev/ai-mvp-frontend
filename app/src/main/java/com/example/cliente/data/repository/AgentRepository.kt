package com.example.cliente.data.repository

import com.example.cliente.data.remote.AgentApiService
import com.example.cliente.data.remote.AgentRequest
import com.example.cliente.data.remote.AgentResponse
import com.example.cliente.util.Resource
import com.example.cliente.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repositorio para interactuar con el Agente de IA.
 *
 * Endpoint: POST /agent
 * Permite enviar prompts a Gemini que puede ejecutar herramientas como:
 * - add_task
 * - get_tasks
 * - update_task_status
 * - get_daily_recommendations
 */
class AgentRepository @Inject constructor(
    private val apiService: AgentApiService,
    private val userPreferences: UserPreferences
) {
    /**
     * Envía un prompt al agente de IA.
     * POST /agent
     *
     * @param prompt El mensaje del usuario
     * @return Response con texto directo o resultado de herramienta ejecutada
     */
    fun sendPrompt(prompt: String): Flow<Resource<AgentResponse>> = flow {
        try {
            emit(Resource.Loading())
            val userId = userPreferences.userId.firstOrNull()?.toIntOrNull()
            val response = apiService.chat(
                AgentRequest(
                    prompt = prompt,
                    userId = userId,
                    context = mapOf("client" to "android")
                )
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al comunicarse con el agente"))
        }
    }
}
