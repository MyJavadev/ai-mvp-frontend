package com.example.cliente.presentation.module

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cliente.data.model.ModuleDto

/**
 * Pantalla de detalle de módulo según la guía de Android.
 *
 * Acciones disponibles:
 * - "Escuchar" → ejecutar POST /text-to-speech
 * - "Quiz" → consultar GET /modules/:moduleId/quiz
 * - "Marcar completado" → POST /progress/modules/complete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    module: ModuleDto,
    userId: Int,
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onGenerateTTS: (Int, String) -> Unit = { _, _ -> },
    onCompleteModule: (Int, Int) -> Unit = { _, _ -> }
) {
    var showCompleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Módulo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Module image
            if (module.image_url != null) {
                AsyncImage(
                    model = module.image_url,
                    contentDescription = "Imagen del Módulo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Description
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                // Subtopics
                if (module.subtopics.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Temas cubiertos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        module.subtopics.forEachIndexed { index, subtopic ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${index + 1}. ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = subtopic,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Audio button - usa description como texto para TTS
                    OutlinedButton(
                        onClick = {
                            onGenerateTTS(module.id, module.description)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Escuchar")
                    }

                    // Quiz button
                    Button(
                        onClick = { onNavigateToQuiz(module.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quiz")
                    }
                }


                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }

    // Complete module dialog
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text("Marcar como completado") },
            text = { Text("¿Has terminado de estudiar este módulo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCompleteModule(userId, module.id)
                        showCompleteDialog = false
                    }
                ) {
                    Text("Sí, completar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

