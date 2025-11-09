package com.example.cliente.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class CreateUserRequest(
    val username: String
)

