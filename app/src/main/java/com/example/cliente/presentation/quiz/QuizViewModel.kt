package com.example.cliente.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.QuizAnswerDto
import com.example.cliente.data.model.QuizDto
import com.example.cliente.data.model.QuizResultDto
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
    val quiz: QuizDto? = null,
    val error: String? = null
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

    private val _userAnswers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userAnswers: StateFlow<Map<String, Int>> = _userAnswers.asStateFlow()

    fun generateQuiz(moduleId: String, numberOfQuestions: Int = 5) {
        repository.generateQuiz(moduleId, numberOfQuestions).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _quizState.value = QuizState(quiz = result.data)
                    _userAnswers.value = emptyMap()
                }
                is Resource.Error -> {
                    _quizState.value = QuizState(
                        error = result.message ?: "Error generating quiz"
                    )
                }
                is Resource.Loading -> {
                    _quizState.value = QuizState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setAnswer(questionId: String, answer: Int) {
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            put(questionId, answer)
        }
    }

    fun submitQuiz(quizId: String) {
        val answers = _userAnswers.value.map { (questionId, answer) ->
            QuizAnswerDto(questionId, answer)
        }

        repository.submitQuiz(quizId, answers).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _quizResultState.value = QuizResultState(result = result.data)
                }
                is Resource.Error -> {
                    _quizResultState.value = QuizResultState(
                        error = result.message ?: "Error submitting quiz"
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

