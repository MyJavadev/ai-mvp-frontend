package com.example.cliente.presentation.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.ModuleDto
import com.example.cliente.data.repository.ProgressRepository
import com.example.cliente.data.repository.StudyPathRepository
import com.example.cliente.data.repository.TTSRepository
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

data class ModuleDetailState(
    val isLoading: Boolean = false,
    val module: ModuleDto? = null,
    val error: String? = null
)

data class TTSState(
    val isGenerating: Boolean = false,
    val currentJobId: String? = null,
    val audioUrl: String? = null,
    val error: String? = null,
    val status: String? = null
)

@HiltViewModel
class ModuleDetailViewModel @Inject constructor(
    private val studyPathRepository: StudyPathRepository,
    private val ttsRepository: TTSRepository,
    private val progressRepository: ProgressRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _moduleState = MutableStateFlow(ModuleDetailState())
    val moduleState: StateFlow<ModuleDetailState> = _moduleState.asStateFlow()

    private val _ttsState = MutableStateFlow(TTSState())
    val ttsState: StateFlow<TTSState> = _ttsState.asStateFlow()

    private var currentUserId: Int? = null

    init {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                currentUserId = userId?.toIntOrNull()
            }
        }
    }

    /**
     * Carga el módulo completo desde el backend.
     * GET /study-path-modules/:id
     */
    fun loadModule(moduleId: String) {
        studyPathRepository.getModule(moduleId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _moduleState.value = ModuleDetailState(module = result.data)
                }
                is Resource.Error -> {
                    _moduleState.value = ModuleDetailState(
                        error = result.message ?: "Error al cargar módulo"
                    )
                }
                is Resource.Loading -> {
                    _moduleState.value = ModuleDetailState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Genera audio TTS para un texto específico.
     * POST /text-to-speech
     */
    fun generateTTS(moduleId: Int, text: String) {
        val userId = currentUserId ?: return

        ttsRepository.createTTSJob(text, userId, moduleId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val jobId = result.data?.jobId
                    if (jobId != null) {
                        _ttsState.value = TTSState(
                            isGenerating = true,
                            currentJobId = jobId,
                            status = "pending"
                        )
                        // Iniciar polling del trabajo TTS
                        pollTTSJob(jobId)
                    }
                }
                is Resource.Error -> {
                    _ttsState.value = TTSState(
                        error = result.message ?: "Error al generar audio"
                    )
                }
                is Resource.Loading -> {
                    _ttsState.value = TTSState(isGenerating = true, status = "processing")
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Hace polling del trabajo TTS hasta que se complete.
     */
    private fun pollTTSJob(jobId: String) {
        ttsRepository.pollTTSJob(jobId, maxAttempts = 20).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _ttsState.value = TTSState(
                        audioUrl = result.data?.audioUrl,
                        currentJobId = jobId,
                        status = "completed"
                    )
                }
                is Resource.Error -> {
                    _ttsState.value = TTSState(
                        error = result.message ?: "Error al generar audio"
                    )
                }
                is Resource.Loading -> {
                    _ttsState.value = TTSState(
                        isGenerating = true,
                        currentJobId = jobId,
                        status = "processing"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Marca el módulo como completado.
     * POST /progress/modules/complete
     */
    fun completeModule(moduleId: Int) {
        val userId = currentUserId ?: return

        progressRepository.completeModule(userId, moduleId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    // Módulo completado exitosamente
                    // Podrías actualizar el estado o mostrar un mensaje
                }
                is Resource.Error -> {
                    // Manejar error
                }
                is Resource.Loading -> {
                    // Mostrar loading si es necesario
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearTTSState() {
        _ttsState.value = TTSState()
    }
}

