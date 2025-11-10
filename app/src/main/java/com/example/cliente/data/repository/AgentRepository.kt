package com.example.cliente.data.repository

import com.example.cliente.data.remote.AgentApiService
import com.example.cliente.data.remote.AgentRequest
import com.example.cliente.data.remote.AgentResponse
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
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
    private val apiService: AgentApiService
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
            val response = apiService.chat(AgentRequest(prompt))
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al comunicarse con el agente"))
        }
    }
}

