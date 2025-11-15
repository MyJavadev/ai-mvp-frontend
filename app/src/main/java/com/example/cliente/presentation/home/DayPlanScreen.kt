package com.example.cliente.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.cliente.data.model.DayPlanContext
import com.example.cliente.data.model.DayPlanResponse
import com.example.cliente.data.model.DayPlanTimelineItem
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlanScreen(
    onNavigateBack: () -> Unit,
    onOpenModule: (Int) -> Unit,
    viewModel: DayPlanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes de completado/error
    LaunchedEffect(state.completionMessage, state.completionError) {
        state.completionMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearCompletionMessage()
        }
        state.completionError?.let { err ->
            snackbarHostState.showSnackbar(err)
            viewModel.clearCompletionError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Día", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
        , snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading && state.plan == null -> LoadingState(padding)
            state.error != null && state.plan == null -> ErrorState(padding, state.error!!, onRetry = { viewModel.refresh() })
            else -> state.plan?.let { plan ->
                DayPlanContent(
                    padding = padding,
                    plan = plan,
                    onGeneratePlan = { force -> viewModel.generatePlan(force = force) },
                    onAction = { action ->
                        when {
                            action.startsWith("open_module") -> {
                                val id = action.split(":").getOrNull(1)?.toIntOrNull()
                                id?.let { onOpenModule(it) }
                            }
                            action.startsWith("mark_complete") -> {
                                val id = action.split(":").getOrNull(1)?.toIntOrNull()
                                id?.let { viewModel.completeModule(it) }
                            }
                            else -> viewModel.handleAction(action)
                        }
                    },
                    isRefreshing = state.isLoading
                )
            } ?: EmptyPlanState(padding, onGenerate = { viewModel.generatePlan(force = true) })
        }
    }
}

@Composable
private fun DayPlanContent(
    padding: PaddingValues,
    plan: DayPlanResponse,
    onGeneratePlan: (Boolean) -> Unit,
    onAction: (String) -> Unit,
    isRefreshing: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeadlineCard(plan)
        }

        item {
            ActionRow(isRefreshing = isRefreshing, onGeneratePlan = onGeneratePlan)
        }

        if (plan.plan.timeline.isNotEmpty()) {
            item { SectionTitle("Plan del día") }
            items(plan.plan.timeline) { block ->
                TimelineCard(block, onAction)
            }
        }

        item { SectionTitle("Cuidado personal") }
        item { WellnessCard(plan) }

        plan.context?.let { context ->
            item { SectionTitle("Contexto de hoy") }
            item { ContextHighlights(context) }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun HeadlineCard(plan: DayPlanResponse) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(plan.plan.headline, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(plan.plan.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            if (plan.plan.focusAreas.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    plan.plan.focusAreas.forEach { area ->
                        AssistChip(onClick = {}, label = { Text(area) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRow(isRefreshing: Boolean, onGeneratePlan: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = { onGeneratePlan(false) },
            enabled = !isRefreshing,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Actualizar")
        }
        Button(
            onClick = { onGeneratePlan(true) },
            enabled = !isRefreshing,
            modifier = Modifier.weight(1f)
        ) {
            Text("Generar nuevo plan")
        }
    }
}

@Composable
private fun TimelineCard(block: DayPlanTimelineItem, onAction: (String) -> Unit) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(block.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(block.intent, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!block.startTime.isNullOrBlank() || !block.endTime.isNullOrBlank()) {
                Text(
                    text = listOfNotNull(block.startTime, block.endTime).joinToString(" — "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (block.actions.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    block.actions.forEach { action ->
                        AssistChip(
                            onClick = { onAction(action) },
                            label = { Text(beautifyAction(action)) },
                            leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WellnessCard(plan: DayPlanResponse) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WellnessRow("Check-in", plan.plan.wellness.checkIn)
            WellnessRow("Pausas recomendadas", plan.plan.wellness.breaks.joinToString(" • "))
            WellnessRow("Tips de energía", plan.plan.wellness.energyTips.joinToString(" • "))
            WellnessRow("Gratitud", plan.plan.wellness.gratitudePrompt)
            Divider()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(plan.plan.motivation, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ContextHighlights(context: DayPlanContext) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ContextCard(title = "Tareas pendientes", value = context.pendingTasks.size.toString())
        ContextCard(title = "Módulos abiertos", value = context.pendingModules.size.toString())
        ContextCard(
            title = "Progreso",
            value = "${context.progressSummary.completedModules}/${context.progressSummary.totalModules} (${context.progressSummary.completionRate}%)"
        )
        context.recentMood?.let { mood ->
            ContextCard(title = "Humor", value = mood.mood)
        }
    }
}

@Composable
private fun ContextCard(title: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
}

@Composable
private fun WellnessRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LoadingState(padding: PaddingValues) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(padding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyPlanState(padding: PaddingValues, onGenerate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Aún no tienes un plan para hoy", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGenerate) {
            Text("Crear Mi Día")
        }
    }
}

@Composable
private fun ErrorState(padding: PaddingValues, message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Ups", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry) { Text("Reintentar") }
    }
}

private fun beautifyAction(action: String): String = when {
    action.startsWith("open_module") -> "Abrir módulo"
    action.startsWith("mark_complete") -> "Marcar completado"
    else -> action.replace('_', ' ')
}
