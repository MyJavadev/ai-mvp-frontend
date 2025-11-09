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
     * Response: [{ resultados de Typesense }]
     */
    @GET("search/typesense")
    suspend fun searchTypesense(
        @Query("q") query: String
    ): List<ModuleDto>
}

@Serializable
data class SearchResultModule(
    val id: Int,
    val study_path_id: Int,
    val title: String,
    val description: String?,
    val content: String,
    val distance: Float? = null // Para búsqueda semántica
)

