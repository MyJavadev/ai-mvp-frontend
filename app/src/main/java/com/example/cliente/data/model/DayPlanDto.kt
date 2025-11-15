package com.example.cliente.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DayPlanResponse(
    val plan: DayPlanContent,
    val context: DayPlanContext? = null,
    val metadata: DayPlanMetadata? = null
)

@Serializable
data class DayPlanContent(
    val headline: String,
    val summary: String,
    val focusAreas: List<String> = emptyList(),
    val timeline: List<DayPlanTimelineItem> = emptyList(),
    val wellness: DayPlanWellness,
    val motivation: String
)

@Serializable
data class DayPlanTimelineItem(
    val label: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val intent: String,
    val actions: List<String> = emptyList()
)

@Serializable
data class DayPlanWellness(
    val checkIn: String,
    val breaks: List<String> = emptyList(),
    val energyTips: List<String> = emptyList(),
    val gratitudePrompt: String
)

@Serializable
data class DayPlanContext(
    val planDate: String,
    val timeOfDay: String,
    val pendingTasks: List<DayPlanTask> = emptyList(),
    val pendingModules: List<DayPlanModule> = emptyList(),
    val progressSummary: DayPlanProgressSummary,
    val recentMood: DayPlanMoodSnapshot? = null,
    val recentAchievements: List<DayPlanAchievement> = emptyList()
)

@Serializable
data class DayPlanTask(
    val id: Int,
    val task: String,
    val status: String,
    val due_at: String? = null
)

@Serializable
data class DayPlanModule(
    val id: Int,
    val study_path_id: Int,
    val title: String,
    val topic: String,
    val description: String
)

@Serializable
data class DayPlanAchievement(
    val id: Int,
    val name: String,
    val earned_at: String
)

@Serializable
data class DayPlanMoodSnapshot(
    val mood: String,
    val energy_level: Int? = null,
    val stress_level: Int? = null,
    val note: String? = null,
    val created_at: String
)

@Serializable
data class DayPlanProgressSummary(
    val totalModules: Int,
    val completedModules: Int,
    val completionRate: Int
)

@Serializable
data class DayPlanMetadata(
    val planDate: String,
    val source: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
