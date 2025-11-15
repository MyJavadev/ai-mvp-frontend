package com.example.cliente.presentation.wellness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cliente.data.model.JournalEntryDto
import com.example.cliente.data.model.MoodDistributionItem
import com.example.cliente.data.model.MoodSnapshotDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessScreen(
    onNavigateBack: () -> Unit,
    viewModel: WellnessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mi Bienestar", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Observa tu energía y emociones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MoodSummaryCard(uiState.moodSummary)
            }

            item {
                MoodCheckInCard(
                    isLoading = uiState.isSavingMood,
                    onSubmit = { mood, energy, stress, note, tags ->
                        viewModel.logMood(mood, energy, stress, note, tags)
                    }
                )
            }

            if (uiState.moodHistory.isNotEmpty()) {
                item {
                    SectionTitle("Historial de ánimo")
                }
                items(uiState.moodHistory) { snapshot ->
                    MoodHistoryItem(snapshot)
                }
            }

            item {
                JournalEntryCard(
                    isSaving = uiState.isSavingJournal,
                    onSubmit = { title, summary, raw, tags ->
                        viewModel.createJournalEntry(title, summary, raw, tags)
                    }
                )
            }

            if (uiState.journalEntries.isNotEmpty()) {
                item {
                    SectionTitle("Entradas recientes")
                }
                items(uiState.journalEntries) { entry ->
                    JournalPreviewItem(entry)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun MoodSummaryCard(summary: com.example.cliente.data.model.MoodSummaryDto?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resumen de ánimo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (summary == null) {
                Text("Aún no tenemos datos. Registra tu estado de ánimo para comenzar.")
            } else {
                Text(
                    text = "Último registro: ${summary.latestEntry?.mood ?: "-"}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatChip(label = "Energía prom.", value = summary.averageEnergy?.formatOneDecimal() ?: "-")
                    StatChip(label = "Estrés prom.", value = summary.averageStress?.formatOneDecimal() ?: "-")
                    StatChip(label = "Registros", value = summary.sampleSize.toString())
                }
                if (summary.moodDistribution.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Frecuencia de estados", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    summary.moodDistribution.take(4).forEach { item ->
                        DistributionRow(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DistributionRow(item: MoodDistributionItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(item.mood.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Medium)
        Text("${item.percentage.formatOneDecimal()}%", color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun MoodCheckInCard(
    isLoading: Boolean,
    onSubmit: (String, Int?, Int?, String?, List<String>) -> Unit
) {
    val moods = listOf("enfocado", "calmo", "motivado", "agotado", "estresado")
    val selectedMood = remember { mutableStateOf(moods.first()) }
    val energy = remember { mutableStateOf(7) }
    val stress = remember { mutableStateOf(3) }
    val note = remember { mutableStateOf("") }
    val tags = remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Check-in rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text("¿Cómo te sientes hoy?", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moods.forEach { mood ->
                    val isSelected = selectedMood.value == mood
                    MoodChip(
                        text = mood.replaceFirstChar { it.uppercase() },
                        selected = isSelected,
                        onClick = { selectedMood.value = mood }
                    )
                }
            }
            NumericSlider(label = "Energía", value = energy.value, onValueChange = { energy.value = it })
            NumericSlider(label = "Estrés", value = stress.value, onValueChange = { stress.value = it })
            TextField(
                value = note.value,
                onValueChange = { note.value = it },
                label = { Text("Notas opcionales") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            TextField(
                value = tags.value,
                onValueChange = { tags.value = it },
                label = { Text("Tags (coma separadas)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            Button(
                onClick = {
                    onSubmit(
                        selectedMood.value,
                        energy.value,
                        stress.value,
                        note.value.ifBlank { null },
                        tags.value.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLoading) "Guardando..." else "Registrar estado")
            }
        }
    }
}

@Composable
private fun NumericSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column {
        Text("$label: $value", fontWeight = FontWeight.Medium)
        androidx.compose.material3.Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..10f
        )
    }
}

@Composable
private fun MoodHistoryItem(snapshot: MoodSnapshotDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(snapshot.mood.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Energía: ${snapshot.energyLevel ?: "-"} • Estrés: ${snapshot.stressLevel ?: "-"}")
            snapshot.note?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            snapshot.tags?.takeIf { it.isNotEmpty() }?.let { tags ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JournalEntryCard(
    isSaving: Boolean,
    onSubmit: (String?, String, String?, List<String>) -> Unit
) {
    val title = remember { mutableStateOf("") }
    val summary = remember { mutableStateOf("") }
    val raw = remember { mutableStateOf("") }
    val tags = remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Diario personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            TextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Título opcional") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            TextField(
                value = summary.value,
                onValueChange = { summary.value = it },
                label = { Text("Resumen (requerido)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            TextField(
                value = raw.value,
                onValueChange = { raw.value = it },
                label = { Text("Texto completo") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            TextField(
                value = tags.value,
                onValueChange = { tags.value = it },
                label = { Text("Tags (coma separadas)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors()
            )
            Button(
                onClick = {
                    val summaryText = summary.value.trim()
                    if (summaryText.isNotEmpty()) {
                        onSubmit(
                            title.value.trim().ifBlank { null },
                            summaryText,
                            raw.value.trim().ifBlank { null },
                            tags.value.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                        )
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(),
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar entrada")
            }
        }
    }
}

@Composable
private fun JournalPreviewItem(entry: JournalEntryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(entry.title ?: "Entrada sin título", fontWeight = FontWeight.Bold)
            Text(entry.summary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                entry.entryDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            entry.metadata?.tags?.takeIf { it.isNotEmpty() }?.let { tags ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        Text("#$tag", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
}

private fun Double.formatOneDecimal(): String = "%.1f".format(this)

@Composable
private fun MoodChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text)
    }
}
