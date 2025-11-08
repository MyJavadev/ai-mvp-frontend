package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizDto(
    val id: String,
    val userId: String,
    val moduleId: String,
    val questions: List<QuestionDto>,
    val score: Int? = null,
    val totalQuestions: Int,
    val status: String, // "pending", "in_progress", "completed"
    val createdAt: String? = null,
    val completedAt: String? = null
)

@Serializable
data class QuestionDto(
    val id: String,
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
    val quizId: String,
    val answers: List<QuizAnswerDto>
)

@Serializable
data class QuizAnswerDto(
    val questionId: String,
    val answer: Int
)

@Serializable
data class QuizResultDto(
    val quizId: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val passed: Boolean,
    val answers: List<QuestionDto>
)

