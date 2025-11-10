package com.example.cliente.data.model

import kotlinx.serialization.Serializable

/**
 * Quiz según backend.
 * Backend devuelve: { id, module_id, title, created_at }
 */
@Serializable
data class QuizDto(
    val id: Int,
    val module_id: Int,
    val title: String,
    val created_at: String
)

/**
 * Este QuestionDto ya no se usa, se usa el de QuizApiService
 */
@Deprecated("Use QuestionDto from QuizApiService")
@Serializable
data class QuestionDto(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String? = null,
    val userAnswer: Int? = null,
    val isCorrect: Boolean? = null
)

@Serializable
data class GenerateQuizRequest(
    val moduleId: String,
    val numberOfQuestions: Int = 5
)

@Serializable
data class SubmitQuizRequest(
    val userId: Int,
    val answers: List<QuizAnswerDto>
)

@Serializable
data class QuizAnswerDto(
    val questionId: Int,
    val selectedOptionIndex: Int
)

@Serializable
data class QuizResultDto(
    val quizId: Int,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val passed: Boolean,
    val answers: List<QuestionDto>
)

