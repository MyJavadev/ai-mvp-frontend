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
    var selectedLevel by remember { mutableStateOf("beginner") }
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
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Topic") },
                placeholder = { Text("e.g., Machine Learning Basics") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Text(
                text = "Select Level",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LevelOption(
                    title = "Beginner",
                    description = "New to the topic",
                    selected = selectedLevel == "beginner",
                    onClick = { selectedLevel = "beginner" },
                    enabled = !state.isLoading
                )
                LevelOption(
                    title = "Intermediate",
                    description = "Some experience",
                    selected = selectedLevel == "intermediate",
                    onClick = { selectedLevel = "intermediate" },
                    enabled = !state.isLoading
                )
                LevelOption(
                    title = "Advanced",
                    description = "Expert level",
                    selected = selectedLevel == "advanced",
                    onClick = { selectedLevel = "advanced" },
                    enabled = !state.isLoading
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (state.message != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.message ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (topic.isNotBlank()) {
                        viewModel.createStudyPath(topic, selectedLevel)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = topic.isNotBlank() && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Study Path")
                }
            }
        }
    }
}

@Composable
fun LevelOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                enabled = enabled
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

