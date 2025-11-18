package com.example.cliente.data.remote

import com.example.cliente.data.model.ModuleDto
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Service para Search según documentación del backend.
 *
 * Endpoints disponibles:
 * - GET /search?q=texto - Búsqueda semántica mediante pgvector
 * - GET /search/typesense?q=texto - Búsqueda por keyword usando Typesense
 */
interface SearchApiService {

    /**
     * Búsqueda semántica mediante pgvector.
     *
     * GET /search?q=texto
     * Response: [{ módulos con su distancia }]
     */
    @GET("search")
    suspend fun searchSemantic(
        @Query("q") query: String
    ): List<SearchResultModule>

    /**
     * Búsqueda por keyword usando Typesense.
     *
     * GET /search/typesense?q=texto
     * Response: { facet_counts: [], found: 6, hits: [...] }
     */
    @GET("search/typesense")
    suspend fun searchTypesense(
        @Query("q") query: String
    ): TypesenseResponse
}

@Serializable
data class SearchResultModule(
    val id: Int,
    val study_path_id: Int? = null,
    val title: String,
    val description: String? = null,
    val content: String? = null,
    val distance: Float? = null, // Para búsqueda semántica
    val subtopics: List<String>? = null,
    val image_url: String? = null
)

/**
 * Respuesta completa de Typesense.
 */
@Serializable
data class TypesenseResponse(
    val facet_counts: List<String> = emptyList(),
    val found: Int,
    val hits: List<TypesenseHit>
)

@Serializable
data class TypesenseHit(
    val document: TypesenseDocument
)

@Serializable
data class TypesenseDocument(
    val id: String,
    val study_path_id: Int? = null,
    val title: String,
    val description: String? = null,
    val subtopics: List<String>? = null,
    val image_url: String? = null
)

