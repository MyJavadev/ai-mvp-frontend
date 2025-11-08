package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProgressDto(
    val userId: String,
    val totalStudyPaths: Int,
    val completedStudyPaths: Int,
    val totalModules: Int,
    val completedModules: Int,
    val totalQuizzes: Int,
    val averageScore: Float,
    val studyStreak: Int,
    val achievements: List<AchievementDto> = emptyList(),
    val recentActivity: List<ActivityDto> = emptyList()
)

@Serializable
data class AchievementDto(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String? = null,
    val unlockedAt: String? = null,
    val isUnlocked: Boolean = false
)

@Serializable
data class ActivityDto(
    val id: String,
    val type: String, // "completed_module", "passed_quiz", "created_path"
    val description: String,
    val timestamp: String
)

