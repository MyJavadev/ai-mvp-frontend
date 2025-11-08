package com.example.cliente.data.remote

import com.example.cliente.data.model.ApiResponse
import com.example.cliente.data.model.SearchRequest
import com.example.cliente.data.model.SearchResultDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SearchApiService {

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<SearchResultDto>

    @POST("search/advanced")
    suspend fun advancedSearch(@Body request: SearchRequest): ApiResponse<SearchResultDto>
}

