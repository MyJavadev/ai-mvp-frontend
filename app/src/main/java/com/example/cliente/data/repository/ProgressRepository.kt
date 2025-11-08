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

