package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentMessageDto(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: String? = null
)

@Serializable
data class AgentChatRequest(
    val userId: String,
    val message: String,
    val context: Map<String, String>? = null
)

@Serializable
data class AgentChatResponse(
    val message: String,
    val suggestions: List<String>? = null,
    val actions: List<AgentActionDto>? = null
)

@Serializable
data class AgentActionDto(
    val type: String, // "navigate", "create_path", "take_quiz"
    val params: Map<String, String>
)

