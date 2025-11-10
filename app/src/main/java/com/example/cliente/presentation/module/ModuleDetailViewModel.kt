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

data class ModuleCompletionState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null,
    val message: String? = null
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

    private val _completionState = MutableStateFlow(ModuleCompletionState())
    val completionState: StateFlow<ModuleCompletionState> = _completionState.asStateFlow()

    // Cache de audios generados por texto (texto -> audioUrl)
    private val audioCache = mutableMapOf<String, String>()

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
     *
     * Implementa caché: si el audio ya fue generado para este texto, lo reutiliza.
     */
    fun generateTTS(moduleId: Int, text: String) {
        val userId = currentUserId ?: return

        // Verificar si ya existe en caché
        val cachedAudioUrl = audioCache[text]
        if (cachedAudioUrl != null) {
            // Reutilizar audio existente
            _ttsState.value = TTSState(
                audioUrl = cachedAudioUrl,
                status = "completed",
                currentJobId = null
            )
            return
        }

        // Si no está en caché, generar nuevo audio
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
                        pollTTSJob(jobId, text)
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
     * Guarda el audio en caché para reutilización.
     */
    private fun pollTTSJob(jobId: String, text: String) {
        ttsRepository.pollTTSJob(jobId, maxAttempts = 20).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val audioUrl = result.data?.audioUrl
                    if (audioUrl != null) {
                        // Guardar en caché
                        audioCache[text] = audioUrl
                    }
                    _ttsState.value = TTSState(
                        audioUrl = audioUrl,
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
     * Body: { "userId": 1, "moduleId": 23 }
     */
    fun completeModule(moduleId: Int) {
        val userId = currentUserId
        if (userId == null) {
            _completionState.value = ModuleCompletionState(
                error = "Usuario no identificado"
            )
            return
        }

        progressRepository.completeModule(userId, moduleId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _completionState.value = ModuleCompletionState(
                        isCompleted = true,
                        message = "¡Módulo completado! Has ganado puntos de progreso."
                    )
                }
                is Resource.Error -> {
                    _completionState.value = ModuleCompletionState(
                        error = result.message ?: "Error al completar módulo"
                    )
                }
                is Resource.Loading -> {
                    _completionState.value = ModuleCompletionState(
                        isLoading = true
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearCompletionState() {
        _completionState.value = ModuleCompletionState()
    }

    fun clearTTSState() {
        _ttsState.value = TTSState()
    }
}

