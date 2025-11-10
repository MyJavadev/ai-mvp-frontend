package com.example.cliente.presentation.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    moduleId: String,
    onQuizCompleted: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val quizState by viewModel.quizState.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()

    // TODO: Obtener userId real desde UserPreferences
    val mockUserId = 1

    LaunchedEffect(moduleId) {
        // Primero intenta obtener el quiz existente
        val moduleIdInt = moduleId.toIntOrNull()
        if (moduleIdInt != null) {
            viewModel.getModuleQuiz(moduleIdInt)
        }
    }

    LaunchedEffect(viewModel.quizResultState.collectAsState().value.result) {
        if (viewModel.quizResultState.value.result != null) {
            onQuizCompleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                quizState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                quizState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = quizState.error ?: "Error al cargar quiz",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para generar quiz si no existe
                        if (quizState.error?.contains("404") == true || quizState.error?.contains("no") == true) {
                            Button(
                                onClick = {
                                    val moduleIdInt = moduleId.toIntOrNull()
                                    if (moduleIdInt != null) {
                                        viewModel.generateQuiz(moduleIdInt)
                                    }
                                }
                            ) {
                                Text("Generar Quiz")
                            }
                        }
                    }
                }
                quizState.generateMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = quizState.generateMessage ?: "Generando quiz...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Por favor espera mientras se genera el quiz",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                quizState.quiz != null -> {
                    val quizData = quizState.quiz!!
                    val questions = quizData.questions
                    val quiz = quizData.quiz

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(questions) { index, question ->
                                QuestionCard(
                                    questionNumber = index + 1,
                                    question = question.question_text,
                                    options = question.options, // ← Ya viene como lista
                                    selectedAnswer = userAnswers[question.id],
                                    onAnswerSelected = { answer ->
                                        viewModel.setAnswer(question.id, answer)
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.submitQuiz(quiz.id, mockUserId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            enabled = userAnswers.size == questions.size
                        ) {
                            Text("Enviar Respuestas")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    questionNumber: Int,
    question: String,
    options: List<String>,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == index,
                        onClick = { onAnswerSelected(index) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

