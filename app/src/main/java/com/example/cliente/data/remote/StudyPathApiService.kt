package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import com.example.cliente.data.model.CreateStudyPathRequest
import com.example.cliente.data.model.StudyPathDto
import com.example.cliente.data.model.UpdateModuleProgressRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyPathApiService {

    @POST("study-paths")
    suspend fun createStudyPath(@Body request: CreateStudyPathRequest): ApiResponse<StudyPathDto>

    @GET("study-paths/user/{userId}")
    suspend fun getUserStudyPaths(@Path("userId") userId: String): ApiResponse<List<StudyPathDto>>

    @GET("study-paths/{pathId}")
    suspend fun getStudyPath(@Path("pathId") pathId: String): ApiResponse<StudyPathDto>

    @PUT("study-paths/{pathId}/modules/{moduleId}/progress")
    suspend fun updateModuleProgress(
        @Path("pathId") pathId: String,
        @Path("moduleId") moduleId: String,
        @Body request: UpdateModuleProgressRequest
    ): ApiResponse<StudyPathDto>

    @POST("study-paths/{pathId}/generate")
    suspend fun generateStudyPath(@Path("pathId") pathId: String): ApiResponse<StudyPathDto>
}

