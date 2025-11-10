package com.example.cliente.data.repository

import com.example.cliente.data.model.ModuleDto
import com.example.cliente.data.remote.SearchApiService
import com.example.cliente.data.remote.SearchResultModule
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val apiService: SearchApiService
) {

    /**
     * Búsqueda semántica mediante pgvector.
     * GET /search?q=texto
     */
    fun searchSemantic(query: String): Flow<Resource<List<SearchResultModule>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.searchSemantic(query)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error en búsqueda semántica"))
        }
    }

    /**
     * Búsqueda por keyword usando Typesense.
     * GET /search/typesense?q=texto
     *
     * Typesense devuelve: { found: N, hits: [{ document: {...} }] }
     */
    fun searchTypesense(query: String): Flow<Resource<List<ModuleDto>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.searchTypesense(query)

            // Extraer documentos de los hits y convertir a ModuleDto
            val modules = response.hits.map { hit ->
                ModuleDto(
                    id = hit.document.id.toIntOrNull() ?: 0,
                    study_path_id = hit.document.study_path_id ?: 0,
                    title = hit.document.title,
                    description = hit.document.description ?: "Sin descripción",
                    subtopics = hit.document.subtopics ?: emptyList(),
                    image_url = hit.document.image_url
                )
            }

            emit(Resource.Success(modules))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error en búsqueda por keyword"))
        }
    }
}

