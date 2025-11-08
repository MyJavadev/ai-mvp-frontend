package com.example.cliente.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.cliente.ui.theme.Dimens
import com.example.cliente.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToStudyPaths: () -> Unit,
    onNavigateToCreatePath: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.APP_NAME) },
                actions = {
                    IconButton(onClick = { /* Navigate to search */ }) {
                        Icon(Icons.Default.Search, contentDescription = Strings.NAV_SEARCH)
                    }
                    IconButton(onClick = { /* Navigate to profile */ }) {
                        Icon(Icons.Default.Person, contentDescription = Strings.NAV_PROFILE)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePath) {
                Icon(Icons.Default.Add, contentDescription = Strings.CREATE_STUDY_PATH)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            Text(
                text = Strings.WELCOME_BACK,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToStudyPaths
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.PaddingLarge),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.MY_STUDY_PATHS,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = Strings.CONTINUE_LEARNING,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeXLarge),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = Strings.QUICK_STATS,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
            ) {
                StatCard(
                    title = Strings.ACTIVE_PATHS,
                    value = "3",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = Strings.COMPLETED,
                    value = "12",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(Dimens.PaddingMedium)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

