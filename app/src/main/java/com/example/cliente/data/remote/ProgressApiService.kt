package com.example.cliente.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API Service para Progress según documentación del backend.
 *
 * Endpoints disponibles:
 * - POST /progress/modules/complete - Marca un módulo como completado
 * - GET /progress/users/:userId/progress - Devuelve módulos completados y logros
 * - GET /progress/users/:userId/dashboard - Resumen rápido
 * - GET /progress/users/:userId/timeline - Agrega eventos clave para el usuario
 */
interface ProgressApiService {

    /**
     * Marca un módulo como completado y evalúa logros.
     *
     * POST /progress/modules/complete
     * Body: { "userId": 1, "moduleId": 23 }
     * Response: { "progress": {...}, "achievements": [...] }
     */
    @POST("progress/modules/complete")
    suspend fun completeModule(
        @Body request: CompleteModuleRequest
    ): CompleteModuleResponse

    /**
     * Devuelve módulos completados y logros.
     *
     * GET /progress/users/:userId/progress
     */
    @GET("progress/users/{userId}/progress")
    suspend fun getUserProgress(
        @Path("userId") userId: Int
    ): UserProgressResponse

    /**
     * Resumen rápido (conteos, siguiente módulo sugerido, racha).
     *
     * GET /progress/users/:userId/dashboard
     */
    @GET("progress/users/{userId}/dashboard")
    suspend fun getUserDashboard(
        @Path("userId") userId: Int
    ): DashboardResponse

    /**
     * Agrega los eventos clave para el usuario.
     *
     * GET /progress/users/:userId/timeline
     * Response: { "requests": [...], "studyPaths": [...], "pendingModules": [...], ... }
     */
    @GET("progress/users/{userId}/timeline")
    suspend fun getUserTimeline(
        @Path("userId") userId: Int
    ): TimelineResponse
}

@Serializable
data class CompleteModuleRequest(
    val userId: Int,
    val moduleId: Int
)

@Serializable
data class CompleteModuleResponse(
    val message: String? = null,
    val progress: ModuleProgressDto,
    val newly_awarded_achievements: List<AchievementDto> = emptyList()
)

@Serializable
data class ModuleProgressDto(
    val id: Int,
    val user_id: Int,
    val module_id: Int,
    val completed: Boolean,
    val completed_at: String? = null
)

@Serializable
data class AchievementDto(
    val id: Int,
    val user_id: Int,
    val achievement_type: String,
    val description: String,
    val generated_image_url: String? = null,
    val unlocked_at: String = ""
)

@Serializable
data class UserProgressResponse(
    val completed_modules_count: Int? = null,
    val completed_modules: List<ModuleProgressDto>,
    val achievements_count: Int? = null,
    val achievements: List<AchievementDto>
)

@Serializable
data class DashboardResponse(
    val totalPaths: Int,
    val completedModules: Int,
    val totalModules: Int,
    val nextModule: String?,
    val currentStreak: Int
)

@Serializable
data class TimelineResponse(
    val requests: List<TimelineRequest>,
    val studyPaths: List<TimelineStudyPath>,
    val pendingModules: List<TimelineModule>,
    val quizzes: List<TimelineQuiz>,
    val ttsJobs: List<TimelineTTS>,
    val achievements: List<AchievementDto>,
    val recentProgress: List<ModuleProgressDto>
)

@Serializable
data class TimelineRequest(
    val id: String,
    val topic: String,
    val status: String,
    val created_at: String = ""
)

@Serializable
data class TimelineStudyPath(
    val id: Int,
    val topic: String,
    val created_at: String = ""
)

@Serializable
data class TimelineModule(
    val id: Int,
    val title: String,
    val study_path_id: Int
)

@Serializable
data class TimelineQuiz(
    val id: Int,
    val module_id: Int,
    val created_at: String = ""
)

@Serializable
data class TimelineTTS(
    val id: String,
    val status: String,
    val created_at: String = ""
)

