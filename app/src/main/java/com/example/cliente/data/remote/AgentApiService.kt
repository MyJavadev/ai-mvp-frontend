package com.example.cliente.data.remote

import com.example.cliente.data.model.AgentChatRequest
import com.example.cliente.data.model.AgentChatResponse
import com.example.cliente.data.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AgentApiService {

    @POST("agent/chat")
    suspend fun chat(@Body request: AgentChatRequest): ApiResponse<AgentChatResponse>
}

