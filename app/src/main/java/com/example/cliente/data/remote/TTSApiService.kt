package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import kotlinx.serialization.Serializable
import retrofit2.http.*

/**
 * API Service para Text-to-Speech según documentación del backend.
 *
 * Endpoints disponibles:
 * - POST /text-to-speech - Crea trabajo TTS (async)
 * - GET /text-to-speech/:jobId - Consulta estado de un trabajo
 * - GET /text-to-speech - Lista trabajos recientes (filtrable)
 */
interface TTSApiService {

    /**
     * Crea un trabajo de texto a voz.
     *
     * POST /text-to-speech
     * Body: { "text": "...", "userId": 1, "moduleId": 23 }
     * Response: 202 { "jobId": "uuid" }
     */
    @POST("text-to-speech")
    suspend fun createTTSJob(
        @Body request: CreateTTSRequest
    ): TTSJobResponse

    /**
     * Consulta el estado de un trabajo TTS.
     *
     * GET /text-to-speech/:jobId
     * Response: { "status": "pending|processing|completed|failed", "audioUrl": "...", ... }
     */
    @GET("text-to-speech/{jobId}")
    suspend fun getTTSJob(
        @Path("jobId") jobId: String
    ): TTSJobStatusResponse

    /**
     * Lista los trabajos TTS recientes.
     *
     * GET /text-to-speech?userId=X&moduleId=Y&status=completed
     * Response: [{ trabajos ordenados por created_at desc, max 50 }]
     */
    @GET("text-to-speech")
    suspend fun getTTSJobs(
        @Query("userId") userId: Int? = null,
        @Query("moduleId") moduleId: Int? = null,
        @Query("status") status: String? = null
    ): List<TTSJobStatusResponse>
}

/**
 * Request para crear un trabajo TTS.
 */
@Serializable
data class CreateTTSRequest(
    val text: String,
    val userId: Int,
    val moduleId: Int? = null
)

/**
 * Response al crear un trabajo TTS.
 */
@Serializable
data class TTSJobResponse(
    val jobId: String
)

/**
 * Estado de un trabajo TTS.
 */
@Serializable
data class TTSJobStatusResponse(
    val id: String,
    val user_id: Int,
    val module_id: Int?,
    val status: String, // "pending", "processing", "completed", "failed"
    val audioUrl: String? = null,
    val error: String? = null,
    val created_at: String,
    val updated_at: String?
)

