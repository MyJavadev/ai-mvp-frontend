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
     */
    fun searchTypesense(query: String): Flow<Resource<List<ModuleDto>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.searchTypesense(query)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error en búsqueda por keyword"))
        }
    }
}

