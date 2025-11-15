package com.example.cliente.data.remote

import com.example.cliente.data.model.JournalEntriesResponse
import com.example.cliente.data.model.JournalEntryDto
import com.example.cliente.data.model.JournalEntryRequest
import com.example.cliente.data.model.MoodHistoryResponse
import com.example.cliente.data.model.MoodSnapshotDto
import com.example.cliente.data.model.MoodSnapshotRequest
import com.example.cliente.data.model.MoodSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API de bienestar: estado de ánimo y diario personal.
 */
interface WellnessApiService {

    @POST("users/{userId}/mood")
    suspend fun logMood(
        @Path("userId") userId: Int,
        @Body request: MoodSnapshotRequest
    ): MoodSnapshotDto

    @GET("users/{userId}/mood")
    suspend fun getMoodHistory(
        @Path("userId") userId: Int,
        @Query("limit") limit: Int? = null
    ): MoodHistoryResponse

    @GET("users/{userId}/mood/summary")
    suspend fun getMoodSummary(
        @Path("userId") userId: Int
    ): MoodSummaryDto

    @POST("users/{userId}/journal")
    suspend fun createJournalEntry(
        @Path("userId") userId: Int,
        @Body request: JournalEntryRequest
    ): JournalEntryDto

    @GET("users/{userId}/journal")
    suspend fun getJournalEntries(
        @Path("userId") userId: Int,
        @Query("limit") limit: Int? = null
    ): JournalEntriesResponse

    @GET("users/{userId}/journal/{entryId}")
    suspend fun getJournalEntry(
        @Path("userId") userId: Int,
        @Path("entryId") entryId: Int
    ): JournalEntryDto
}

