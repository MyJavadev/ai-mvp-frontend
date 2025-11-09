package com.example.cliente.data.repository

import com.example.cliente.data.model.ModuleDto
import com.example.cliente.data.model.StudyPathDto
import com.example.cliente.data.remote.CreateStudyPathRequest
import com.example.cliente.data.remote.StudyPathApiService
import com.example.cliente.data.remote.StudyPathRequestResponse
import com.example.cliente.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StudyPathRepository @Inject constructor(
    private val apiService: StudyPathApiService
) {

    /**
     * Crea una ruta de estudio (async).
     * Retorna el requestId que debe ser monitoreado.
     */
    fun createStudyPath(topic: String, userId: Int): Flow<Resource<StudyPathRequestResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.createStudyPath(CreateStudyPathRequest(topic, userId))
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error creating study path"))
        }
    }

    /**
     * Monitorea el estado de una solicitud hasta que se complete.
     * Hace polling cada 6 segundos (rango recomendado 5-8s) hasta obtener el study_path_id.
     */
    fun pollStudyPathRequest(requestId: String, maxAttempts: Int = 20): Flow<Resource<StudyPathDto>> = flow {
        try {
            emit(Resource.Loading())

            var attempts = 0
            while (attempts < maxAttempts) {
                val response = apiService.getStudyPathRequest(requestId)

                when (response.request.status) {
                    "completed" -> {
                        if (response.request.study_path_id != null && response.modules != null) {
                            // Convertir a StudyPathDto
                            val studyPath = StudyPathDto(
                                id = response.request.study_path_id,
                                user_id = response.request.user_id,
                                topic = response.request.topic,
                                created_at = response.request.created_at,
                                modules = response.modules
                            )
                            emit(Resource.Success(studyPath))
                            return@flow
                        }
                    }
                    "failed" -> {
                        emit(Resource.Error("Failed to generate study path"))
                        return@flow
                    }
                    "pending", "processing" -> {
                        // Continue polling
                        delay(6000) // Wait 6 seconds (5-8s recommended)
                        attempts++
                    }
                }
            }

            emit(Resource.Error("Timeout waiting for study path generation (2 minutes)"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error polling study path"))
        }
    }

    /**
     * Obtiene las rutas de estudio del usuario.
     */
    fun getUserStudyPaths(userId: String): Flow<Resource<List<StudyPathDto>>> = flow {
        try {
            emit(Resource.Loading())
            val studyPaths = apiService.getUserStudyPaths(userId)
            emit(Resource.Success(studyPaths))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error fetching study paths"))
        }
    }

    /**
     * Obtiene los módulos de una ruta específica.
     */
    fun getStudyPath(pathId: String): Flow<Resource<List<ModuleDto>>> = flow {
        try {
            emit(Resource.Loading())
            val modules = apiService.getStudyPath(pathId)
            emit(Resource.Success(modules))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error fetching study path"))
        }
    }

    /**
     * Obtiene un módulo específico.
     */
    fun getModule(moduleId: String): Flow<Resource<ModuleDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getModule(moduleId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching module"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error fetching module"))
        }
    }
}

