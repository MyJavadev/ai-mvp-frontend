package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizDto(
    val id: Int,
    val userId: Int,
    val moduleId: Int,
    val questions: List<QuestionDto>,
    val score: Int? = null,
    val totalQuestions: Int,
    val status: String, // "pending", "in_progress", "completed"
    val createdAt: String? = null,
    val completedAt: String? = null
)

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

