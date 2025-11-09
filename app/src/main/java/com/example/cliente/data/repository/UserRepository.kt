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

    /**
     * Crea o reutiliza un usuario.
     * POST /users
     * Response: 201 Created o 200 OK con UserDto directo
     */
    fun createUser(username: String): Flow<Resource<UserDto>> = flow {
        try {
            emit(Resource.Loading())
            val user = apiService.createUser(CreateUserRequest(username))
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al crear usuario"))
        }
    }

    /**
     * Obtiene información de un usuario.
     * GET /users/:userId
     * Response: 200 OK con UserDto directo
     */
    fun getUser(userId: String): Flow<Resource<UserDto>> = flow {
        try {
            emit(Resource.Loading())
            val user = apiService.getUser(userId)
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener usuario"))
        }
    }

    /**
     * Actualiza un usuario.
     * PUT /users/:userId
     * Response: 200 OK con UserDto directo
     */
    fun updateUser(userId: String, user: UserDto): Flow<Resource<UserDto>> = flow {
        try {
            emit(Resource.Loading())
            val updatedUser = apiService.updateUser(userId, user)
            emit(Resource.Success(updatedUser))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al actualizar usuario"))
        }
    }
}

