package com.example.cliente.data.repository

import com.example.cliente.data.model.CreateUserRequest
import com.example.cliente.data.model.UserDto
import com.example.cliente.data.remote.UserApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: UserApiService
) {

    fun createUser(email: String, name: String): Flow<Resource<UserDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.createUser(CreateUserRequest(email, name))
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error creating user"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun getUser(userId: String): Flow<Resource<UserDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUser(userId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching user"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun updateUser(userId: String, user: UserDto): Flow<Resource<UserDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.updateUser(userId, user)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error updating user"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}

