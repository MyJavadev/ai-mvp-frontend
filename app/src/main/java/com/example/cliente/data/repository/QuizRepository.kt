package com.example.cliente.data.repository

import com.example.cliente.data.model.GenerateQuizRequest
import com.example.cliente.data.model.QuizAnswerDto
import com.example.cliente.data.model.QuizDto
import com.example.cliente.data.model.QuizResultDto
import com.example.cliente.data.model.SubmitQuizRequest
import com.example.cliente.data.remote.QuizApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val apiService: QuizApiService
) {

    fun generateQuiz(moduleId: String, numberOfQuestions: Int = 5): Flow<Resource<QuizDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.generateQuiz(GenerateQuizRequest(moduleId, numberOfQuestions))
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error generating quiz"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun getQuiz(quizId: String): Flow<Resource<QuizDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getQuiz(quizId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error fetching quiz"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    fun submitQuiz(quizId: String, answers: List<QuizAnswerDto>): Flow<Resource<QuizResultDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.submitQuiz(SubmitQuizRequest(quizId, answers))
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.error ?: "Error submitting quiz"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}

