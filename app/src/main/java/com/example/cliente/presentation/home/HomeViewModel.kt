 package com.example.cliente.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.remote.*
import com.example.cliente.data.repository.ProgressRepository
import com.example.cliente.util.Resource
import com.example.cliente.util.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val dashboard: DashboardResponse? = null,
    val timeline: TimelineResponse? = null,
    val error: String? = null,
    val userName: String = "Usuario"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    private var currentUserId: Int? = null

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                currentUserId = userId?.toIntOrNull()
                if (currentUserId != null) {
                    loadDashboard()
                    loadTimeline()
                }
            }
        }
    }

    /**
     * Carga el dashboard del usuario.
     * GET /progress/users/:userId/dashboard
     */
    fun loadDashboard() {
        val userId = currentUserId ?: return

        progressRepository.getUserDashboard(userId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _homeState.value = _homeState.value.copy(
                        dashboard = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _homeState.value = _homeState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _homeState.value = _homeState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Carga el timeline del usuario.
     * GET /progress/users/:userId/timeline
     */
    fun loadTimeline() {
        val userId = currentUserId ?: return

        progressRepository.getUserTimeline(userId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _homeState.value = _homeState.value.copy(
                        timeline = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _homeState.value = _homeState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _homeState.value = _homeState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        loadDashboard()
        loadTimeline()
    }
}

