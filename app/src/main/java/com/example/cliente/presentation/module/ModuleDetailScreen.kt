package com.example.cliente.presentation.module

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.core.net.toUri

/**
 * Pantalla de detalle de módulo moderna e interactiva.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    moduleId: String,
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ModuleDetailViewModel = hiltViewModel()
) {
    val moduleState by viewModel.moduleState.collectAsState()
    val ttsState by viewModel.ttsState.collectAsState()
    val completionState by viewModel.completionState.collectAsState()
    var showCompleteDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(moduleId) {
        viewModel.loadModule(moduleId)
    }

    // Mostrar Snackbar cuando el módulo se complete o haya error
    LaunchedEffect(completionState.isCompleted, completionState.error) {
        if (completionState.isCompleted && completionState.message != null) {
            snackbarHostState.showSnackbar(
                message = completionState.message!!,
                duration = SnackbarDuration.Long
            )
            viewModel.clearCompletionState()
        } else if (completionState.error != null) {
            snackbarHostState.showSnackbar(
                message = completionState.error!!,
                duration = SnackbarDuration.Long
            )
            viewModel.clearCompletionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(moduleState.module?.title ?: "Módulo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de quiz mejorado
                    IconButton(
                        onClick = {
                            moduleState.module?.let {
                                onNavigateToQuiz(it.id)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Assignment, // Ícono más representativo de quiz/examen
                            contentDescription = "Realizar Quiz",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCompleteDialog = true },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                text = { Text("Completar") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                moduleState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                moduleState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = moduleState.error ?: "Error al cargar módulo",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                moduleState.module != null -> {
                    ModuleContent(
                        module = moduleState.module!!,
                        ttsState = ttsState,
                        onGenerateAudio = { subtopic ->
                            viewModel.generateTTS(
                                moduleState.module!!.id,
                                subtopic
                            )
                        },
                        onClearTTS = { viewModel.clearTTSState() },
                        onNavigateToQuiz = onNavigateToQuiz
                    )
                }
            }
        }
    }

    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!completionState.isLoading) {
                    showCompleteDialog = false
                }
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            title = { Text("Marcar como completado") },
            text = {
                if (completionState.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("Completando módulo...")
                    }
                } else {
                    Text("¿Has terminado de estudiar este módulo?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        moduleState.module?.let {
                            viewModel.completeModule(it.id)
                        }
                    },
                    enabled = !completionState.isLoading
                ) {
                    Text("Sí, completar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCompleteDialog = false },
                    enabled = !completionState.isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Cerrar diálogo cuando se complete exitosamente
    LaunchedEffect(completionState.isCompleted) {
        if (completionState.isCompleted) {
            showCompleteDialog = false
        }
    }
}

@Composable
fun ModuleContent(
    module: com.example.cliente.data.model.ModuleDto,
    ttsState: TTSState,
    onGenerateAudio: (String) -> Unit,
    onClearTTS: () -> Unit,
    onNavigateToQuiz: (Int) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (module.image_url != null) {
            AsyncImage(
                model = module.image_url,
                contentDescription = module.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Imagen generándose...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = ttsState.isGenerating || ttsState.audioUrl != null || ttsState.error != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                TTSStatusCard(
                    ttsState = ttsState,
                    onClear = onClearTTS,
                    onPlayAudio = { audioUrl ->
                        // Abrir el audio en el reproductor predeterminado
                        val intent = Intent(Intent.ACTION_VIEW, audioUrl.toUri())
                        intent.setDataAndType(audioUrl.toUri(), "audio/*")
                        context.startActivity(intent)
                    }
                )
            }

            // Card de Quiz destacado
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNavigateToQuiz(module.id) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Pon a prueba tu conocimiento",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Realiza el quiz de este módulo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (module.subtopics.isNotEmpty()) {
                Text(
                    text = "Temas de Estudio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                module.subtopics.forEachIndexed { index, subtopic ->
                    ExpandableSubtopicCard(
                        index = index + 1,
                        title = subtopic,
                        moduleDescription = module.description, // Pasar la descripción completa
                        onPlayAudio = {
                            // Enviar descripción completa del módulo con el subtema como contexto
                            val textToSpeak = "$subtopic: ${module.description}"
                            onGenerateAudio(textToSpeak)
                        },
                        isAudioGenerating = ttsState.isGenerating
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TTSStatusCard(
    ttsState: TTSState,
    onClear: () -> Unit,
    onPlayAudio: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                ttsState.error != null -> MaterialTheme.colorScheme.errorContainer
                ttsState.audioUrl != null -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        ttsState.isGenerating -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Generando audio...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Esto puede tomar unos segundos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                        ttsState.error != null -> {
                            Icon(Icons.Default.Error, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(ttsState.error)
                        }
                        ttsState.audioUrl != null -> {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Audio listo para reproducir",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            // Botón de reproducción cuando el audio está listo
            if (ttsState.audioUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onPlayAudio(ttsState.audioUrl) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("▶ Reproducir Audio")
                }

                Text(
                    text = "El audio se abrirá en tu reproductor predeterminado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ExpandableSubtopicCard(
    index: Int,
    title: String,
    moduleDescription: String,
    onPlayAudio: () -> Unit,
    isAudioGenerating: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = index.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Contenido del módulo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar la descripción completa del módulo
                    Text(
                        text = moduleDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPlayAudio,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isAudioGenerating
                    ) {
                        Icon(
                            if (isAudioGenerating) Icons.Default.HourglassEmpty else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isAudioGenerating) "Generando..." else "Escuchar este contenido")
                    }
                }
            }
        }
    }
}

