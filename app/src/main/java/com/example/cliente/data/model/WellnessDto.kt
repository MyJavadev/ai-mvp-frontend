package com.example.cliente.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
 data class MoodSnapshotDto(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    val mood: String,
    @SerialName("energy_level") val energyLevel: Int? = null,
    @SerialName("stress_level") val stressLevel: Int? = null,
    val note: String? = null,
    val tags: List<String>? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
 data class MoodSnapshotRequest(
    val mood: String,
    val energyLevel: Int? = null,
    val stressLevel: Int? = null,
    val note: String? = null,
    val tags: List<String>? = null
)

@Serializable
 data class MoodHistoryResponse(
    val snapshots: List<MoodSnapshotDto> = emptyList()
)

@Serializable
 data class MoodSummaryDto(
    val userId: Int,
    val sampleSize: Int,
    val averageEnergy: Double? = null,
    val averageStress: Double? = null,
    val moodDistribution: List<MoodDistributionItem> = emptyList(),
    val recentSnapshots: List<MoodSnapshotDto> = emptyList(),
    val latestEntry: MoodSnapshotDto? = null
)

@Serializable
 data class MoodDistributionItem(
    val mood: String,
    val percentage: Double
)

@Serializable
 data class JournalEntryDto(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("entry_date") val entryDate: String,
    val title: String? = null,
    val summary: String,
    @SerialName("raw_content") val rawContent: String? = null,
    val metadata: JournalMetadata? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
 data class JournalMetadata(
    val tags: List<String>? = null
)

@Serializable
 data class JournalEntriesResponse(
    val entries: List<JournalEntryDto> = emptyList()
)

@Serializable
 data class JournalEntryRequest(
    val entryDate: String? = null,
    val title: String? = null,
    val summary: String,
    val rawContent: String? = null,
    val metadata: JournalMetadata? = null
)

