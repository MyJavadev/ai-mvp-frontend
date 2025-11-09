package com.example.cliente.data.repository

import com.example.cliente.data.model.QuizResultDto
import com.example.cliente.data.model.SubmitQuizRequest
import com.example.cliente.data.remote.QuizApiService
import com.example.cliente.data.remote.QuizWithQuestionsResponse
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val apiService: QuizApiService
) {

    /**
     * Encola la generación de un quiz para el módulo.
     * POST /modules/:moduleId/quiz
     */
    fun generateQuiz(moduleId: Int): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.generateQuiz(moduleId)
            emit(Resource.Success(response.message))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al generar quiz"))
        }
    }

    /**
     * Obtiene el último quiz generado con sus preguntas.
     * GET /modules/:moduleId/quiz
     */
    fun getModuleQuiz(moduleId: Int): Flow<Resource<QuizWithQuestionsResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getModuleQuiz(moduleId)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener quiz"))
        }
    }

    /**
     * Registra respuestas del usuario y devuelve el puntaje.
     * POST /quizzes/:quizId/submit
     */
    fun submitQuiz(quizId: Int, request: SubmitQuizRequest): Flow<Resource<QuizResultDto>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.submitQuiz(quizId, request)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al enviar quiz"))
        }
    }
}

