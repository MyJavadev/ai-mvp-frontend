package com.example.cliente.data.repository

import com.example.cliente.data.model.DayPlanResponse
import com.example.cliente.data.remote.CreateDayPlanRequest
import com.example.cliente.data.remote.DayPlanApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DayPlanRepository @Inject constructor(
    private val apiService: DayPlanApiService
) {

    fun getDayPlan(userId: String, date: String? = null): Flow<Resource<DayPlanResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getDayPlan(userId, date)
            emit(Resource.Success(response))
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                404 -> "404: No existe un plan diario para esta fecha"
                400 -> "Solicitud inválida"
                500 -> "Error del servidor"
                else -> "Error HTTP ${e.code()}"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error fetching day plan"))
        }
    }

    fun createDayPlan(userId: String, planDate: String? = null, force: Boolean = false): Flow<Resource<DayPlanResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.createDayPlan(userId, CreateDayPlanRequest(planDate, force))
            emit(Resource.Success(response))
        } catch (e: retrofit2.HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Solicitud inválida"
                500 -> "Error del servidor al generar el plan"
                else -> "Error HTTP ${e.code()}"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error creating day plan"))
        }
    }
}
