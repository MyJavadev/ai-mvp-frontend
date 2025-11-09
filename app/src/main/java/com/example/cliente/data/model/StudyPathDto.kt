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
 * Módulo de estudio.
 * Incluye image_url cuando la imagen fue generada.
 * Campos según documentación: title, description, subtopics, content, image_url, audio_url
 */
@Serializable
data class ModuleDto(
    val id: Int,
    val study_path_id: Int,
    val title: String,
    val description: String? = null,
    val content: String,
    val subtopics: String? = null, // JSON string o texto plano con subtemas
    val order_index: Int,
    val image_url: String? = null,
    val audio_url: String? = null,
    val created_at: String,
    val isCompleted: Boolean = false, // Calculado del lado del cliente
    val estimatedMinutes: Int? = null // Estimación del cliente
)

