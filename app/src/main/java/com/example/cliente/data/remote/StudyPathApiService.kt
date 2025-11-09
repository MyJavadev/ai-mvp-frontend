package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import com.example.cliente.data.model.StudyPathDto
import com.example.cliente.data.model.ModuleDto
import kotlinx.serialization.Serializable
import retrofit2.http.*

/**
 * API Service para Study Paths según documentación del backend.
 *
 * Endpoints disponibles:
 * - POST /study-path - Encola generación de ruta (async)
 * - GET /study-path-requests/:requestId - Consulta estado de solicitud
 * - GET /study-paths?userId=X - Lista rutas del usuario
 * - GET /study-path/:id - Obtiene módulos de una ruta
 */
interface StudyPathApiService {

    /**
     * Encola la generación de una ruta de estudio.
     * Retorna un requestId que debe ser monitoreado.
     *
     * POST /study-path
     * Body: { "topic": "...", "userId": 1 }
     * Response: 202 Accepted { "message": "...", "topic": "...", "requestId": "uuid" }
     */
    @POST("study-path")
    suspend fun createStudyPath(
        @Body request: CreateStudyPathRequest
    ): StudyPathRequestResponse

    /**
     * Consulta el estado de una solicitud de ruta de estudio.
     *
     * GET /study-path-requests/:requestId
     * Response: { "request": {...}, "modules": [...] }
     */
    @GET("study-path-requests/{requestId}")
    suspend fun getStudyPathRequest(
        @Path("requestId") requestId: String
    ): StudyPathRequestStatusResponse

    /**
     * Lista las rutas de estudio del usuario.
     *
     * GET /study-paths?userId=X
     * Response: [{ "id": 1, "user_id": 1, "topic": "...", "created_at": "..." }]
     */
    @GET("study-paths")
    suspend fun getUserStudyPaths(
        @Query("userId") userId: String
    ): List<StudyPathDto>

    /**
     * Obtiene los módulos de una ruta específica.
     *
     * GET /study-path/:id
     * Response: [{ módulos con image_url }]
     */
    @GET("study-path/{id}")
    suspend fun getStudyPath(
        @Path("id") pathId: String
    ): List<ModuleDto>

    /**
     * Obtiene un módulo específico.
     *
     * GET /study-path-modules/:id
     */
    @GET("study-path-modules/{id}")
    suspend fun getModule(
        @Path("id") moduleId: String
    ): ApiResponse<ModuleDto>
}

/**
 * Request para crear un study path.
 */
@Serializable
data class CreateStudyPathRequest(
    val topic: String,
    val userId: Int
)

/**
 * Response al encolar una ruta de estudio.
 */
@Serializable
data class StudyPathRequestResponse(
    val message: String,
    val topic: String,
    val requestId: String
)

/**
 * Response del estado de una solicitud.
 */
@Serializable
data class StudyPathRequestStatusResponse(
    val request: StudyPathRequest,
    val modules: List<ModuleDto>?
)

/**
 * Información de la solicitud.
 */
@Serializable
data class StudyPathRequest(
    val id: String,
    val user_id: Int,
    val topic: String,
    val status: String, // "pending", "processing", "completed", "failed"
    val study_path_id: Int? = null,
    val created_at: String = "",
    val updated_at: String? = null
)

