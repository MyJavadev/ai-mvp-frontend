package com.example.cliente.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cliente.R

/**
 * Pantalla principal de Ritmo que muestra:
 * - Dashboard con estadísticas del usuario
 * - Timeline con actividades recientes
 * - Accesos rápidos a funcionalidades
 *
 * Consume endpoints:
 * - GET /progress/users/:userId/dashboard
 * - GET /progress/users/:userId/timeline
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToStudyPaths: () -> Unit,
    onNavigateToCreatePath: () -> Unit,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToAgent: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeState by viewModel.homeState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ritmo", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Tu ritmo, tu aprendizaje",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (homeState.isLoading && homeState.dashboard == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mensaje de bienvenida
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icono con fondo circular
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.WavingHand,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column {
                                Text(
                                    text = "¡Bienvenido de vuelta!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Continúa aprendiendo a tu propio ritmo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Dashboard con estadísticas
                homeState.dashboard?.let { dashboard ->
                    item {
                        Text(
                            text = "Tu Progreso",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Rutas",
                                value = dashboard.totalPaths.toString(),
                                icon = Icons.Default.Route,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Completados",
                                value = "${dashboard.completedModules}/${dashboard.totalModules}",
                                icon = Icons.Default.CheckCircle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Racha",
                                value = "${dashboard.currentStreak} días",
                                icon = Icons.Default.LocalFireDepartment,
                                modifier = Modifier.weight(1f)
                            )
                            dashboard.nextModule?.let { nextModule ->
                                StatCard(
                                    title = "Siguiente",
                                    value = nextModule.take(10) + "...",
                                    icon = Icons.Default.PlayArrow,
                                    modifier = Modifier.weight(1f)
                                )
                            } ?: Box(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Acciones rápidas con iconos personalizados
                item {
                    Text(
                        text = "¿Qué quieres hacer hoy?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Grid de botones con iconos personalizados
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Primera fila
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomIconButton(
                                imageRes = R.drawable.mi_dia,
                                onClick = { /* TODO: Implementar Mi Día */ },
                                modifier = Modifier.weight(1f)
                            )
                            CustomIconButton(
                                imageRes = R.drawable.mis_rutas,
                                onClick = onNavigateToStudyPaths,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Segunda fila
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomIconButton(
                                imageRes = R.drawable.crear_ruta,
                                onClick = onNavigateToCreatePath,
                                modifier = Modifier.weight(1f)
                            )
                            CustomIconButton(
                                imageRes = R.drawable.salud_mental,
                                onClick = { /* TODO: Implementar Salud Mental */ },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Tercera fila
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomIconButton(
                                imageRes = R.drawable.busqueda,
                                onClick = onNavigateToSearch,
                                modifier = Modifier.weight(1f)
                            )
                            CustomIconButton(
                                imageRes = R.drawable.asistente_de_ia,
                                onClick = onNavigateToAgent,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Cuarta fila
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomIconButton(
                                imageRes = R.drawable.mi_perfil,
                                onClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f)
                            )
                            CustomIconButton(
                                imageRes = R.drawable.configuracion,
                                onClick = { /* TODO: Implementar Configuración */ },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Timeline de actividades
                homeState.timeline?.let { timeline ->
                    if (timeline.recentProgress.isNotEmpty() ||
                        timeline.achievements.isNotEmpty() ||
                        timeline.pendingModules.isNotEmpty()
                    ) {
                        item {
                            Text(
                                text = "Actividad Reciente",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Logros recientes
                        if (timeline.achievements.isNotEmpty()) {
                            items(timeline.achievements.take(3)) { achievement ->
                                AchievementCard(
                                    title = achievement.achievement_type,
                                    description = achievement.description,
                                    imageUrl = achievement.generated_image_url
                                )
                            }
                        }

                        // Módulos pendientes
                        if (timeline.pendingModules.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Text(
                                            text = "Módulos Pendientes",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        timeline.pendingModules.take(3).forEach { module ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 6.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Surface(
                                                    shape = androidx.compose.foundation.shape.CircleShape,
                                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                                    modifier = Modifier.size(8.dp)
                                                ) {}
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = module.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono con fondo circular suave
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CustomIconButton(
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun AchievementCard(
    title: String,
    description: String,
    imageUrl: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo circular
            Surface(
                modifier = Modifier.size(56.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}


