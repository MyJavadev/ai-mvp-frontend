package com.example.cliente.data.remote

import com.example.cliente.data.model.DayPlanResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service para Mi Día (Day Plan)
 * Endpoints:
 * - POST /users/{userId}/day-plan
 * - GET  /users/{userId}/day-plan?date=YYYY-MM-DD
 */
interface DayPlanApiService {

    @POST("users/{userId}/day-plan")
    suspend fun createDayPlan(
        @Path("userId") userId: String,
        @Body request: CreateDayPlanRequest
    ): DayPlanResponse

    @GET("users/{userId}/day-plan")
    suspend fun getDayPlan(
        @Path("userId") userId: String,
        @Query("date") date: String? = null
    ): DayPlanResponse
}

@Serializable
data class CreateDayPlanRequest(
    val planDate: String? = null,
    val force: Boolean? = false
)
