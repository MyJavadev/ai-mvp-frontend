package com.example.cliente.data.repository

import com.example.cliente.data.model.JournalEntriesResponse
import com.example.cliente.data.model.JournalEntryDto
import com.example.cliente.data.model.JournalEntryRequest
import com.example.cliente.data.model.MoodHistoryResponse
import com.example.cliente.data.model.MoodSnapshotDto
import com.example.cliente.data.model.MoodSnapshotRequest
import com.example.cliente.data.model.MoodSummaryDto
import com.example.cliente.data.remote.WellnessApiService
import com.example.cliente.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WellnessRepository @Inject constructor(
    private val api: WellnessApiService
) {

    fun logMood(userId: Int, request: MoodSnapshotRequest): Flow<Resource<MoodSnapshotDto>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(api.logMood(userId, request)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al registrar estado de ánimo"))
        }
    }

    fun getMoodHistory(userId: Int, limit: Int? = null): Flow<Resource<MoodHistoryResponse>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(api.getMoodHistory(userId, limit)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener historial"))
        }
    }

    fun getMoodSummary(userId: Int): Flow<Resource<MoodSummaryDto>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(api.getMoodSummary(userId)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener resumen"))
        }
    }

    fun createJournalEntry(userId: Int, request: JournalEntryRequest): Flow<Resource<JournalEntryDto>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(api.createJournalEntry(userId, request)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al guardar entrada"))
        }
    }

    fun getJournalEntries(userId: Int, limit: Int? = null): Flow<Resource<JournalEntriesResponse>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(api.getJournalEntries(userId, limit)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener diario"))
        }
    }

    fun getJournalEntry(userId: Int, entryId: Int): Flow<Resource<JournalEntryDto>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(api.getJournalEntry(userId, entryId)))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error al obtener detalle"))
        }
    }
}

