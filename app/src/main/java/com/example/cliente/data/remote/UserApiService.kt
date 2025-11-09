package com.example.cliente.data.remote

import com.example.cliente.data.model.CreateUserRequest
import com.example.cliente.data.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * API Service para Users según documentación del backend.
 *
 * Endpoints disponibles:
 * - POST /users - Crea o reutiliza usuario
 * - GET /users/:userId - Obtiene información del usuario
 * - PUT /users/:userId - Actualiza usuario
 *
 * NOTA: El backend devuelve directamente UserDto, NO usa wrapper ApiResponse
 */
interface UserApiService {

    /**
     * POST /users
     * Body: { "username": "ada" }
     * Response: 201 Created { "id": 1, "username": "ada", "created_at": "..." }
     */
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): UserDto

    /**
     * GET /users/:userId
     * Response: 200 OK { "id": 1, "username": "ada", "created_at": "..." }
     */
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserDto

    /**
     * PUT /users/:userId
     * Response: 200 OK { "id": 1, "username": "...", "created_at": "..." }
     */
    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: UserDto
    ): UserDto
}

