package com.example.cliente.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val results: List<SearchItemDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class SearchItemDto(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // "module", "topic", "content"
    val relevanceScore: Float? = null,
    val thumbnailUrl: String? = null
)

@Serializable
data class SearchRequest(
    val query: String,
    val filters: Map<String, String>? = null,
    val page: Int = 1,
    val pageSize: Int = 20
)

