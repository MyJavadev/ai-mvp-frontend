package com.example.cliente.presentation.studypath

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cliente.data.model.ModuleDto
import com.example.cliente.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPathDetailScreen(
    pathId: String,
    onNavigateToModule: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: StudyPathViewModel = hiltViewModel()
) {
    val modules by viewModel.studyPathModules.collectAsState()
    val isLoading by viewModel.studyPathModulesLoading.collectAsState()

    LaunchedEffect(pathId) {
        viewModel.getStudyPath(pathId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ruta de Estudio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.BACK)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (modules.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Progreso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val completedModules = modules.count { it.isCompleted }
                            val progress = if (modules.isNotEmpty()) {
                                (completedModules.toFloat() / modules.size.toFloat() * 100).toInt()
                            } else 0

                            LinearProgressIndicator(
                                progress = { progress / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$progress% Completado ($completedModules/${modules.size} módulos)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Módulos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(modules) { module ->
                    ModuleCard(
                        module = module,
                        onClick = { onNavigateToModule(module.id.toString()) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay módulos disponibles",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: ModuleDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (module.isCompleted) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Outlined.Circle
                },
                contentDescription = null,
                tint = if (module.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                val description: String = module.description ?: "Sin descripción"
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                module.estimatedMinutes?.let { minutes ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$minutes min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

