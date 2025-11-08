package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import com.example.cliente.data.model.GenerateQuizRequest
import com.example.cliente.data.model.QuizDto
import com.example.cliente.data.model.QuizResultDto
import com.example.cliente.data.model.SubmitQuizRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApiService {

    @POST("quiz/generate")
    suspend fun generateQuiz(@Body request: GenerateQuizRequest): ApiResponse<QuizDto>

    @GET("quiz/{quizId}")
    suspend fun getQuiz(@Path("quizId") quizId: String): ApiResponse<QuizDto>

    @GET("quiz/user/{userId}")
    suspend fun getUserQuizzes(@Path("userId") userId: String): ApiResponse<List<QuizDto>>

    @POST("quiz/submit")
    suspend fun submitQuiz(@Body request: SubmitQuizRequest): ApiResponse<QuizResultDto>

    @GET("quiz/module/{moduleId}")
    suspend fun getModuleQuizzes(@Path("moduleId") moduleId: String): ApiResponse<List<QuizDto>>
}

