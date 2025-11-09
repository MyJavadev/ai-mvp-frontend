package com.example.cliente.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.UserDto
import com.example.cliente.data.repository.UserRepository
import com.example.cliente.util.Resource
import com.example.cliente.util.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null,
    val isUserCreated: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SetupState())
    val state: StateFlow<SetupState> = _state.asStateFlow()

    fun createUser(username: String) {
        userRepository.createUser(username).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { user ->
                        // Guardar usuario en preferencias
                        userPreferences.saveUser(user.id.toString(), user.username, null)
                        _state.value = SetupState(
                            user = user,
                            isUserCreated = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = SetupState(
                        error = result.message ?: "Error al crear usuario"
                    )
                }
                is Resource.Loading -> {
                    _state.value = SetupState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun checkExistingUser() {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    // Usuario ya existe, cargar sus datos
                    loadUser(userId)
                }
            }
        }
    }

    private fun loadUser(userId: String) {
        userRepository.getUser(userId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = SetupState(
                        user = result.data,
                        isUserCreated = true
                    )
                }
                is Resource.Error -> {
                    _state.value = SetupState(
                        error = result.message ?: "Error al cargar usuario"
                    )
                }
                is Resource.Loading -> {
                    _state.value = SetupState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

