package com.example.cliente.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.MoodSnapshotDto
import com.example.cliente.data.model.MoodSummaryDto
import com.example.cliente.data.remote.*
import com.example.cliente.data.repository.ProgressRepository
import com.example.cliente.data.repository.WellnessRepository
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
    val wellnessSummary: MoodSummaryDto? = null,
    val wellnessSnapshot: MoodSnapshotDto? = null,
    val isWellnessLoading: Boolean = false,
    val wellnessError: String? = null,
    val error: String? = null,
    val userName: String = "Usuario"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val wellnessRepository: WellnessRepository,
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
                    loadWellness()
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
        loadWellness()
    }

    private fun loadWellness() {
        val userId = currentUserId ?: return

        wellnessRepository.getMoodSummary(userId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val summary = result.data
                    val snapshot = summary.selectSnapshotForHighlight()
                    _homeState.value = _homeState.value.copy(
                        wellnessSummary = summary,
                        wellnessSnapshot = snapshot,
                        isWellnessLoading = false,
                        wellnessError = null
                    )
                }
                is Resource.Error -> {
                    _homeState.value = _homeState.value.copy(
                        isWellnessLoading = false,
                        wellnessError = result.message
                    )
                }
                is Resource.Loading -> {
                    _homeState.value = _homeState.value.copy(isWellnessLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}

private fun MoodSummaryDto?.selectSnapshotForHighlight(): MoodSnapshotDto? {
    if (this == null) return null
    val today = java.time.LocalDate.now().toString()
    val todaySnapshot = (latestEntry?.takeIf { it.isSameDay(today) }
        ?: recentSnapshots.firstOrNull { it.isSameDay(today) })
    return todaySnapshot ?: latestEntry ?: recentSnapshots.firstOrNull()
}

private fun MoodSnapshotDto.isSameDay(targetIsoDate: String): Boolean {
    val snapshotDate = createdAt.takeIf { it.length >= 10 }?.substring(0, 10)
    return snapshotDate == targetIsoDate
}
