package com.example.cliente.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.remote.ProgressApiService
import com.example.cliente.data.remote.QuizApiService
import com.example.cliente.data.remote.UserApiService
import com.example.cliente.data.remote.UserPerformanceResponse
import com.example.cliente.data.remote.UserProgressResponse
import com.example.cliente.data.model.UserDto
import com.example.cliente.util.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userApiService: UserApiService,
    private val progressApiService: ProgressApiService,
    private val quizApiService: QuizApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = _profileState.value.copy(isLoading = true, error = null)

                val userId = userPreferences.userId.firstOrNull()
                if (userId == null) {
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        error = "No hay usuario activo"
                    )
                    return@launch
                }

                // Cargar información del usuario
                val user = try {
                    userApiService.getUser(userId)
                } catch (e: Exception) {
                    null
                }

                // Cargar progreso
                val progress = try {
                    progressApiService.getUserProgress(userId.toInt())
                } catch (e: Exception) {
                    null
                }

                // Cargar rendimiento en quizzes
                val performance = try {
                    quizApiService.getUserPerformance(userId.toInt())
                } catch (e: Exception) {
                    null
                }

                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    user = user,
                    progress = progress,
                    performance = performance,
                    userId = userId
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                userPreferences.clearUser()
                onLogoutComplete()
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    error = "Error al cerrar sesión: ${e.message}"
                )
            }
        }
    }
}

data class ProfileState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val progress: UserProgressResponse? = null,
    val performance: UserPerformanceResponse? = null,
    val userId: String? = null,
    val error: String? = null
)

