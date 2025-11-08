package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StudyPathDto(
    val id: String,
    val userId: String,
    val topic: String,
    val level: String,
    val status: String,
    val modules: List<ModuleDto> = emptyList(),
    val progress: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class ModuleDto(
    val id: String,
    val title: String,
    val description: String,
    val order: Int,
    val content: String? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val estimatedMinutes: Int? = null,
    val isCompleted: Boolean = false
)

@Serializable
data class CreateStudyPathRequest(
    val topic: String,
    val level: String, // "beginner", "intermediate", "advanced"
    val preferences: Map<String, String>? = null
)

@Serializable
data class UpdateModuleProgressRequest(
    val moduleId: String,
    val isCompleted: Boolean
)

