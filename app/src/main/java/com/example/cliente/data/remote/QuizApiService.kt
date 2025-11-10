package com.example.cliente.data.remote

import com.example.cliente.data.model.QuizDto
import com.example.cliente.data.model.QuizResultDto
import com.example.cliente.data.model.SubmitQuizRequest
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API Service para Quizzes según documentación del backend.
 *
 * Endpoints disponibles:
 * - POST /modules/:moduleId/quiz - Encola generación de quiz
 * - GET /modules/:moduleId/quiz - Obtiene último quiz generado
 * - POST /quizzes/:quizId/submit - Registra respuestas y devuelve puntaje
 */
interface QuizApiService {

    /**
     * Encola la generación de un quiz para el módulo.
     *
     * POST /modules/:moduleId/quiz
     * Response: 202 Accepted { "message": "..." }
     */
    @POST("modules/{moduleId}/quiz")
    suspend fun generateQuiz(
        @Path("moduleId") moduleId: Int
    ): GenerateQuizResponse

    /**
     * Obtiene el último quiz generado con sus preguntas.
     *
     * GET /modules/:moduleId/quiz
     * Response: { "quiz": {...}, "questions": [...] }
     */
    @GET("modules/{moduleId}/quiz")
    suspend fun getModuleQuiz(
        @Path("moduleId") moduleId: Int
    ): QuizWithQuestionsResponse

    /**
     * Registra respuestas del usuario y devuelve el puntaje.
     *
     * POST /quizzes/:quizId/submit
     * Body: { "userId": 1, "answers": [{ "questionId": 10, "selectedOptionIndex": 2 }] }
     * Response: { "attemptId": 1, "score": 80, ... }
     */
    @POST("quizzes/{quizId}/submit")
    suspend fun submitQuiz(
        @Path("quizId") quizId: Int,
        @Body request: SubmitQuizRequest
    ): QuizResultDto

    /**
     * Obtiene el rendimiento histórico del usuario en quizzes.
     *
     * GET /users/:userId/performance
     * Response: { "total_quizzes_taken": 5, "average_score": 85.5, "attempts": [...] }
     */
    @GET("users/{userId}/performance")
    suspend fun getUserPerformance(
        @Path("userId") userId: Int
    ): UserPerformanceResponse
}

@Serializable
data class GenerateQuizResponse(
    val message: String
)

@Serializable
data class QuizWithQuestionsResponse(
    val quiz: QuizDto,
    val questions: List<QuestionDto>
)

/**
 * Pregunta de quiz según backend.
 * Backend devuelve: { id, question_text, options: ["...", "...", "...", "..."] }
 */
@Serializable
data class QuestionDto(
    val id: Int,
    val question_text: String,
    val options: List<String> // Array de opciones ["A", "B", "C", "D"]
)

@Serializable
data class QuizAnswer(
    val questionId: Int,
    val selectedOptionIndex: Int // 0=A, 1=B, 2=C, 3=D
)

/**
 * Respuesta del rendimiento histórico del usuario.
 */
@Serializable
data class UserPerformanceResponse(
    val total_quizzes_taken: Int,
    val average_score: Double,
    val attempts: List<QuizAttemptHistory>
)

@Serializable
data class QuizAttemptHistory(
    val title: String,
    val score: Int,
    val completed_at: String
)

