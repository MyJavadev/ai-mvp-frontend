package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
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

@Serializable
data class QuestionDto(
    val id: Int,
    val quiz_id: Int,
    val question_text: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val correct_option: String, // "A", "B", "C", "D"
    val explanation: String?
)

@Serializable
data class QuizAnswer(
    val questionId: Int,
    val selectedOptionIndex: Int // 0=A, 1=B, 2=C, 3=D
)

