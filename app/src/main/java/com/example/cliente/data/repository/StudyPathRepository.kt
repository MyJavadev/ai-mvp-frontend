package com.example.cliente.data.repository

import com.example.cliente.data.model.CreateStudyPathRequest
import com.example.cliente.data.model.StudyPathDto
import com.example.cliente.data.model.UpdateModuleProgressRequest
import com.example.cliente.data.remote.StudyPathApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StudyPathRepository @Inject constructor(
    private val apiService: StudyPathApiService
) {

    fun createStudyPath(topic: String, level: String): Flow<Resource<StudyPathDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.createStudyPath(CreateStudyPathRequest(topic, level))
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error creating study path"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun getUserStudyPaths(userId: String): Flow<Resource<List<StudyPathDto>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUserStudyPaths(userId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching study paths"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun getStudyPath(pathId: String): Flow<Resource<StudyPathDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getStudyPath(pathId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching study path"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun updateModuleProgress(pathId: String, moduleId: String, isCompleted: Boolean): Flow<Resource<StudyPathDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.updateModuleProgress(
                pathId,
                moduleId,
                UpdateModuleProgressRequest(moduleId, isCompleted)
            )
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error updating progress"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}

