package com.example.cliente.data.repository

import com.example.cliente.data.model.ProgressDto
import com.example.cliente.data.remote.ProgressApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProgressRepository @Inject constructor(
    private val apiService: ProgressApiService
) {

    fun getUserProgress(userId: String): Flow<Resource<ProgressDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUserProgress(userId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching progress"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun getUserAchievements(userId: String): Flow<Resource<ProgressDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUserAchievements(userId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching achievements"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}
package com.example.cliente.data.repository

import com.example.cliente.data.model.SearchRequest
import com.example.cliente.data.model.SearchResultDto
import com.example.cliente.data.remote.SearchApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val apiService: SearchApiService
) {

    fun search(query: String, page: Int = 1, pageSize: Int = 20): Flow<Resource<SearchResultDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.search(query, page, pageSize)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error searching"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun advancedSearch(request: SearchRequest): Flow<Resource<SearchResultDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.advancedSearch(request)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error searching"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}

