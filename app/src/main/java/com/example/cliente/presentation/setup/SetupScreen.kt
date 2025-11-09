package com.example.cliente.presentation.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cliente.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onSetupComplete: (String) -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    // Check if user already exists
    LaunchedEffect(Unit) {
        viewModel.checkExistingUser()
    }

    // Navigate when user is created
    LaunchedEffect(state.isUserCreated) {
        val user = state.user
        if (state.isUserCreated && user != null) {
            onSetupComplete(user.id.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.PaddingMedium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                    Text(
                        text = "Configuración Inicial",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                    Text(
                        text = "Crea tu perfil para comenzar tu viaje de aprendizaje",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                    // Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        placeholder = { Text("tu_username") },
                        enabled = true,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.error ?: "",
                                modifier = Modifier.padding(Dimens.PaddingMedium),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.SpacingLarge))

                    // Primary action: create user with provided username
                    Button(
                        onClick = {
                            if (username.isNotBlank()) {
                                viewModel.createUser(username)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotBlank()
                    ) {
                        Text("Comenzar")
                    }

                    Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                    // Secondary action: skip (creates a user with default username or does nothing)
                    TextButton(
                        onClick = {
                            if (username.isNotBlank()) {
                                viewModel.createUser(username)
                            } else {
                                // If no username provided, create a fallback user using a timestamp
                                val fallback = "user_" + System.currentTimeMillis().toString().takeLast(6)
                                viewModel.createUser(fallback)
                            }
                        },
                        enabled = true
                    ) {
                        Text("Omitir por ahora")
                    }
                }
            }
        }

    }
}
