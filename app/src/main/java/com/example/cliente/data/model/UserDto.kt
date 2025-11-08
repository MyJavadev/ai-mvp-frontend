package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String? = null
)

@Serializable
data class CreateUserRequest(
    val email: String,
    val name: String
)

