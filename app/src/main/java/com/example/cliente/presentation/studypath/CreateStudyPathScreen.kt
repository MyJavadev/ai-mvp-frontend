package com.example.cliente.presentation.studypath

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cliente.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStudyPathScreen(
    onNavigateBack: () -> Unit,
    onStudyPathCreated: (String) -> Unit,
    viewModel: StudyPathViewModel = hiltViewModel()
) {
    var topic by remember { mutableStateOf("") }
    val state by viewModel.createStudyPathState.collectAsState()

    LaunchedEffect(state.studyPath) {
        state.studyPath?.let { studyPath ->
            onStudyPathCreated(studyPath.id.toString())
            viewModel.clearCreateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.CREATE_STUDY_PATH) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.BACK)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Descripción de la funcionalidad
            Text(
                text = "Crea una ruta de estudio personalizada con IA",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Tema de estudio") },
                placeholder = { Text("Ej: Aprender Kafka desde cero") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                supportingText = {
                    Text("Describe el tema que quieres aprender")
                }
            )

            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (state.message != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = state.message ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (topic.isNotBlank()) {
                        viewModel.createStudyPath(topic)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = topic.isNotBlank() && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generando...")
                } else {
                    Text("Crear Ruta de Estudio")
                }
            }
        }
    }
}


