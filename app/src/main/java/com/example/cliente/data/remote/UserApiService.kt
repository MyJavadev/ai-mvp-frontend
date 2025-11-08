package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import com.example.cliente.data.model.CreateUserRequest
import com.example.cliente.data.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): ApiResponse<UserDto>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): ApiResponse<UserDto>

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: UserDto
    ): ApiResponse<UserDto>
}

