package com.example.cliente.data.model

import kotlinx.serialization.Serializable

/**
 * Study Path según el backend.
 * Response: { "id": 1, "user_id": 1, "topic": "...", "created_at": "..." }
 */
@Serializable
data class StudyPathDto(
    val id: Int,
    val user_id: Int,
    val topic: String,
    val created_at: String,
    val progress: Int = 0, // Calculado del lado del cliente
    val modules: List<ModuleDto> = emptyList()
)

/**
 * Módulo de estudio según la tabla study_path_modules del backend.
 * Campos reales: id, study_path_id, title, description, subtopics (TEXT[]), image_url
 */
@Serializable
data class ModuleDto(
    val id: Int,
    val study_path_id: Int,
    val title: String,
    val description: String,
    val subtopics: List<String>, // Array de strings en PostgreSQL
    val image_url: String? = null
)

