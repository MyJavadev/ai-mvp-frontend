package com.example.cliente.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.QuizAnswerDto
import com.example.cliente.data.model.QuizResultDto
import com.example.cliente.data.model.SubmitQuizRequest
import com.example.cliente.data.remote.QuizWithQuestionsResponse
import com.example.cliente.data.repository.QuizRepository
import com.example.cliente.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class QuizState(
    val isLoading: Boolean = false,
    val quiz: QuizWithQuestionsResponse? = null,
    val error: String? = null,
    val generateMessage: String? = null
)

data class QuizResultState(
    val isLoading: Boolean = false,
    val result: QuizResultDto? = null,
    val error: String? = null
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _quizResultState = MutableStateFlow(QuizResultState())
    val quizResultState: StateFlow<QuizResultState> = _quizResultState.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    /**
     * Encola la generación de un quiz para el módulo.
     * POST /modules/:moduleId/quiz
     */
    fun generateQuiz(moduleId: Int) {
        repository.generateQuiz(moduleId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _quizState.value = QuizState(
                        generateMessage = result.data
                    )
                }
                is Resource.Error -> {
                    _quizState.value = QuizState(
                        error = result.message ?: "Error al generar quiz"
                    )
                }
                is Resource.Loading -> {
                    _quizState.value = QuizState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Obtiene el quiz generado con sus preguntas.
     * GET /modules/:moduleId/quiz
     */
    fun getModuleQuiz(moduleId: Int) {
        repository.getModuleQuiz(moduleId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _quizState.value = QuizState(quiz = result.data)
                    _userAnswers.value = emptyMap()
                }
                is Resource.Error -> {
                    _quizState.value = QuizState(
                        error = result.message ?: "Error al obtener quiz"
                    )
                }
                is Resource.Loading -> {
                    _quizState.value = QuizState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setAnswer(questionId: Int, answerIndex: Int) {
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            put(questionId, answerIndex)
        }
    }

    /**
     * Envía las respuestas del usuario.
     * POST /quizzes/:quizId/submit
     */
    fun submitQuiz(quizId: Int, userId: Int) {
        val answers = _userAnswers.value.map { (questionId, answerIndex) ->
            QuizAnswerDto(questionId, answerIndex)
        }

        val request = SubmitQuizRequest(userId, answers)

        repository.submitQuiz(quizId, request).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _quizResultState.value = QuizResultState(result = result.data)
                }
                is Resource.Error -> {
                    _quizResultState.value = QuizResultState(
                        error = result.message ?: "Error al enviar quiz"
                    )
                }
                is Resource.Loading -> {
                    _quizResultState.value = QuizResultState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun resetQuiz() {
        _quizState.value = QuizState()
        _quizResultState.value = QuizResultState()
        _userAnswers.value = emptyMap()
    }
}

