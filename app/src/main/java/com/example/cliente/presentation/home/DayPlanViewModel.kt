package com.example.cliente.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.DayPlanResponse
import com.example.cliente.data.repository.DayPlanRepository
import com.example.cliente.data.repository.ProgressRepository
import com.example.cliente.util.Resource
import com.example.cliente.util.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DayPlanState(
    val isLoading: Boolean = false,
    val plan: DayPlanResponse? = null,
    val error: String? = null,
    val completionMessage: String? = null,
    val completionError: String? = null,
    val lastRequestedDate: String? = null
)

@HiltViewModel
class DayPlanViewModel @Inject constructor(
    private val repository: DayPlanRepository,
    private val progressRepository: ProgressRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(DayPlanState())
    val state: StateFlow<DayPlanState> = _state

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            userPreferences.userId.collectLatest { id ->
                currentUserId = id
                id?.let { getPlan(it) }
            }
        }
    }

    fun refresh() {
        currentUserId?.let { getPlan(it, _state.value.lastRequestedDate) }
    }

    fun getPlan(userId: String, date: String? = null) {
        viewModelScope.launch {
            repository.getDayPlan(userId, date).collect { res ->
                when (res) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false,
                        plan = res.data,
                        error = null,
                        lastRequestedDate = date
                    )
                    is Resource.Error -> {
                        // Si recibimos 404, intentar generar el plan automáticamente
                        if (res.message?.contains("404") == true || res.message?.contains("No existe un plan") == true) {
                            generatePlan(date, force = false)
                        } else {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = res.message ?: "Error",
                                lastRequestedDate = date
                            )
                        }
                    }
                }
            }
        }
    }

    fun generatePlan(planDate: String? = null, force: Boolean = false) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.createDayPlan(userId, planDate, force).collect { res ->
                when (res) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false,
                        plan = res.data,
                        error = null,
                        lastRequestedDate = planDate
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = res.message ?: "Error",
                        lastRequestedDate = planDate
                    )
                }
            }
        }
    }

    fun completeModule(moduleId: Int) {
        val uid = currentUserId?.toIntOrNull() ?: return
        viewModelScope.launch {
            progressRepository.completeModule(uid, moduleId).collect { res ->
                when (res) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false,
                        completionMessage = "Módulo marcado como completado"
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        completionError = res.message ?: "Error al completar módulo"
                    )
                }
            }
        }
    }

    fun clearCompletionMessage() {
        _state.value = _state.value.copy(completionMessage = null)
    }

    fun clearCompletionError() {
        _state.value = _state.value.copy(completionError = null)
    }

    fun handleAction(action: String) {
        // Parsear acciones simples: "open_module:23" o "mark_complete:23"
        val parts = action.split(":")
        if (parts.size >= 2) {
            when (parts[0]) {
                "open_module" -> {
                    // TODO: navegar al módulo con ID
                }
                "mark_complete" -> {
                    // handled by UI through completeModule
                }
            }
        }
    }
}
