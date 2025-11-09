package com.example.cliente.presentation.studypath

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.StudyPathDto
import com.example.cliente.data.repository.StudyPathRepository
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

data class StudyPathListState(
    val isLoading: Boolean = false,
    val studyPaths: List<StudyPathDto> = emptyList(),
    val error: String? = null
)

data class CreateStudyPathState(
    val isLoading: Boolean = false,
    val studyPath: StudyPathDto? = null,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class StudyPathViewModel @Inject constructor(
    private val repository: StudyPathRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _studyPathListState = MutableStateFlow(StudyPathListState())
    val studyPathListState: StateFlow<StudyPathListState> = _studyPathListState.asStateFlow()

    private val _createStudyPathState = MutableStateFlow(CreateStudyPathState())
    val createStudyPathState: StateFlow<CreateStudyPathState> = _createStudyPathState.asStateFlow()

    private val _selectedStudyPath = MutableStateFlow<StudyPathDto?>(null)
    val selectedStudyPath: StateFlow<StudyPathDto?> = _selectedStudyPath.asStateFlow()

    private var currentUserId: String? = null

    init {
        // Load userId from preferences and fetch study paths automatically
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                userId?.let {
                    currentUserId = it
                    getUserStudyPaths(it)
                }
            }
        }
    }

    fun getUserStudyPaths(userId: String? = null) {
        val id = userId ?: currentUserId ?: return

        repository.getUserStudyPaths(id).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _studyPathListState.value = StudyPathListState(
                        studyPaths = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _studyPathListState.value = StudyPathListState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _studyPathListState.value = StudyPathListState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createStudyPath(topic: String, level: String) {
        val userId = currentUserId?.toIntOrNull() ?: return

        // Primera fase: encolar la generación
        repository.createStudyPath(topic, userId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    // Obtuvimos el requestId, ahora monitorear
                    val requestId = result.data?.requestId ?: return@onEach
                    pollStudyPathGeneration(requestId)
                }
                is Resource.Error -> {
                    _createStudyPathState.value = CreateStudyPathState(
                        error = result.message ?: "Error creating study path"
                    )
                }
                is Resource.Loading -> {
                    _createStudyPathState.value = CreateStudyPathState(
                        isLoading = true,
                        message = "Encolando generación de ruta..."
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun pollStudyPathGeneration(requestId: String) {
        repository.pollStudyPathRequest(requestId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _createStudyPathState.value = CreateStudyPathState(
                        studyPath = result.data
                    )
                }
                is Resource.Error -> {
                    _createStudyPathState.value = CreateStudyPathState(
                        error = result.message ?: "Error generating study path"
                    )
                }
                is Resource.Loading -> {
                    _createStudyPathState.value = CreateStudyPathState(
                        isLoading = true,
                        message = "Generando ruta con IA..."
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getStudyPath(pathId: String) {
        repository.getStudyPath(pathId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _selectedStudyPath.value = result.data
                }
                is Resource.Error -> {
                    // Handle error
                }
                is Resource.Loading -> {
                    // Handle loading
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateModuleProgress(pathId: String, moduleId: String, isCompleted: Boolean) {
        repository.updateModuleProgress(pathId, moduleId, isCompleted).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _selectedStudyPath.value = result.data
                }
                is Resource.Error -> {
                    // Handle error
                }
                is Resource.Loading -> {
                    // Handle loading
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearCreateState() {
        _createStudyPathState.value = CreateStudyPathState()
    }
}

