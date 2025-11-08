package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import com.example.cliente.data.model.ProgressDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProgressApiService {

    @GET("progress/{userId}")
    suspend fun getUserProgress(@Path("userId") userId: String): ApiResponse<ProgressDto>

    @GET("progress/{userId}/achievements")
    suspend fun getUserAchievements(@Path("userId") userId: String): ApiResponse<ProgressDto>
}

