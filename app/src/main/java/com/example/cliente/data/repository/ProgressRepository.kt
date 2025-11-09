package com.example.cliente.data.repository

import com.example.cliente.data.remote.*
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProgressRepository @Inject constructor(
    private val apiService: ProgressApiService
) {

    /**
     * Marca un módulo como completado.
     * POST /progress/modules/complete
     */
    fun completeModule(userId: Int, moduleId: Int): Flow<Resource<CompleteModuleResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.completeModule(CompleteModuleRequest(userId, moduleId))
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al completar módulo"))
        }
    }

    /**
     * Devuelve módulos completados y logros.
     * GET /progress/users/:userId/progress
     */
    fun getUserProgress(userId: Int): Flow<Resource<UserProgressResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUserProgress(userId)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener progreso"))
        }
    }

    /**
     * Resumen rápido.
     * GET /progress/users/:userId/dashboard
     */
    fun getUserDashboard(userId: Int): Flow<Resource<DashboardResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUserDashboard(userId)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener dashboard"))
        }
    }

    /**
     * Agrega eventos clave para el usuario.
     * GET /progress/users/:userId/timeline
     */
    fun getUserTimeline(userId: Int): Flow<Resource<TimelineResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUserTimeline(userId)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener timeline"))
        }
    }
}

