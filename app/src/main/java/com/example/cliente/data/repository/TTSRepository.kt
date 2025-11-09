package com.example.cliente.data.repository

import com.example.cliente.data.remote.CreateTTSRequest
import com.example.cliente.data.remote.TTSApiService
import com.example.cliente.data.remote.TTSJobResponse
import com.example.cliente.data.remote.TTSJobStatusResponse
import com.example.cliente.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TTSRepository @Inject constructor(
    private val apiService: TTSApiService
) {

    /**
     * Crea un trabajo TTS y retorna el jobId.
     */
    fun createTTSJob(text: String, userId: Int, moduleId: Int? = null): Flow<Resource<TTSJobResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.createTTSJob(CreateTTSRequest(text, userId, moduleId))
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error creating TTS job"))
        }
    }

    /**
     * Monitorea un trabajo TTS hasta que se complete.
     * Polling cada 3 segundos, max 20 intentos (1 minuto).
     */
    fun pollTTSJob(jobId: String, maxAttempts: Int = 20): Flow<Resource<TTSJobStatusResponse>> = flow {
        try {
            emit(Resource.Loading())

            var attempts = 0
            while (attempts < maxAttempts) {
                val response = apiService.getTTSJob(jobId)

                when (response.status) {
                    "completed" -> {
                        if (response.audioUrl != null) {
                            emit(Resource.Success(response))
                            return@flow
                        }
                    }
                    "failed" -> {
                        emit(Resource.Error(response.error ?: "TTS generation failed"))
                        return@flow
                    }
                    "pending", "processing" -> {
                        delay(3000) // Wait 3 seconds
                        attempts++
                    }
                }
            }

            emit(Resource.Error("Timeout waiting for TTS generation"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error polling TTS job"))
        }
    }

    /**
     * Obtiene el estado de un trabajo TTS específico.
     */
    fun getTTSJob(jobId: String): Flow<Resource<TTSJobStatusResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getTTSJob(jobId)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error fetching TTS job"))
        }
    }

    /**
     * Lista los trabajos TTS del usuario.
     */
    fun getTTSJobs(
        userId: Int? = null,
        moduleId: Int? = null,
        status: String? = null
    ): Flow<Resource<List<TTSJobStatusResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val jobs = apiService.getTTSJobs(userId, moduleId, status)
            emit(Resource.Success(jobs))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error fetching TTS jobs"))
        }
    }
}

