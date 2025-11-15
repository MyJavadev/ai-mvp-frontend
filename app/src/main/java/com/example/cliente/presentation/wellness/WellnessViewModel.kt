package com.example.cliente.presentation.wellness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.JournalEntryDto
import com.example.cliente.data.model.JournalEntryRequest
import com.example.cliente.data.model.MoodSnapshotDto
import com.example.cliente.data.model.MoodSnapshotRequest
import com.example.cliente.data.model.MoodSummaryDto
import com.example.cliente.data.repository.WellnessRepository
import com.example.cliente.util.Resource
import com.example.cliente.util.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val repository: WellnessRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(WellnessUiState())
    val uiState: StateFlow<WellnessUiState> = _uiState.asStateFlow()

    private var currentUserId: Int? = null

    init {
        viewModelScope.launch {
            userPreferences.userId.collectLatest { idString ->
                val userId = idString?.toIntOrNull()
                currentUserId = userId
                if (userId != null) {
                    refresh()
                }
            }
        }
    }

    fun refresh() {
        val userId = currentUserId ?: return
        fetchMoodSummary(userId)
        fetchMoodHistory(userId)
        fetchJournalEntries(userId)
    }

    fun logMood(mood: String, energy: Int?, stress: Int?, note: String?, tags: List<String>) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.logMood(
                userId,
                MoodSnapshotRequest(
                    mood = mood,
                    energyLevel = energy,
                    stressLevel = stress,
                    note = note,
                    tags = tags.takeIf { it.isNotEmpty() }
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isSavingMood = true, error = null)
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSavingMood = false,
                            successMessage = "Estado de ánimo registrado"
                        )
                        refresh()
                    }
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isSavingMood = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun createJournalEntry(title: String?, summary: String, raw: String?, tags: List<String>) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.createJournalEntry(
                userId,
                JournalEntryRequest(
                    title = title?.takeIf { it.isNotBlank() },
                    summary = summary,
                    rawContent = raw?.takeIf { it.isNotBlank() },
                    metadata = if (tags.isNotEmpty()) com.example.cliente.data.model.JournalMetadata(tags = tags) else null
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isSavingJournal = true, error = null)
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSavingJournal = false,
                            successMessage = "Entrada guardada"
                        )
                        refresh()
                    }
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isSavingJournal = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun fetchMoodSummary(userId: Int) {
        viewModelScope.launch {
            repository.getMoodSummary(userId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        moodSummary = result.data,
                        isLoading = false,
                        error = null
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun fetchMoodHistory(userId: Int) {
        viewModelScope.launch {
            repository.getMoodHistory(userId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        moodHistory = result.data?.snapshots.orEmpty(),
                        isLoading = false,
                        error = null
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun fetchJournalEntries(userId: Int) {
        viewModelScope.launch {
            repository.getJournalEntries(userId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        journalEntries = result.data?.entries.orEmpty(),
                        isLoading = false,
                        error = null
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}

data class WellnessUiState(
    val isLoading: Boolean = false,
    val isSavingMood: Boolean = false,
    val isSavingJournal: Boolean = false,
    val moodSummary: MoodSummaryDto? = null,
    val moodHistory: List<MoodSnapshotDto> = emptyList(),
    val journalEntries: List<JournalEntryDto> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null
)

