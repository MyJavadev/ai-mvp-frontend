package com.example.cliente.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Pantalla de búsqueda que consume:
 * - GET /search?q=texto (búsqueda semántica con pgvector)
 * - GET /search/typesense?q=texto (búsqueda por keywords)
 *
 * Permite alternar entre dos tipos de búsqueda:
 * - Semántica: Encuentra módulos por significado/contexto
 * - Keyword: Encuentra módulos por palabras clave exactas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToModule: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Módulos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    when (searchState.searchType) {
                        SearchType.SEMANTIC -> viewModel.searchSemantic(searchQuery)
                        SearchType.KEYWORD -> viewModel.searchKeyword(searchQuery)
                    }
                },
                onClear = {
                    searchQuery = ""
                    viewModel.clearSearch()
                }
            )

            // Selector de tipo de búsqueda
            SearchTypeTabs(
                selectedType = searchState.searchType,
                onTypeSelected = { type ->
                    viewModel.setSearchType(type)
                }
            )

            // Explicación del tipo de búsqueda
            SearchTypeExplanation(searchState.searchType)

            // Resultados
            when {
                searchState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Buscando...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                searchState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = searchState.error ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                searchState.results.isEmpty() && searchState.query.isNotBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No se encontraron resultados",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Intenta con otros términos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                searchState.results.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "${searchState.results.size} resultados encontrados",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(searchState.results) { module ->
                            SearchResultCard(
                                module = module,
                                onClick = { onNavigateToModule(module.id) }
                            )
                        }
                    }
                }
                else -> {
                    EmptySearchState()
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Buscar módulos...") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            )
        )

        // Botón de búsqueda visible
        FloatingActionButton(
            onClick = onSearch,
            modifier = Modifier.size(56.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun SearchTypeTabs(
    selectedType: SearchType,
    onTypeSelected: (SearchType) -> Unit
) {
    TabRow(
        selectedTabIndex = if (selectedType == SearchType.SEMANTIC) 0 else 1,
        modifier = Modifier.fillMaxWidth()
    ) {
        Tab(
            selected = selectedType == SearchType.SEMANTIC,
            onClick = { onTypeSelected(SearchType.SEMANTIC) },
            text = { Text("🧠 Semántica") },
            icon = { Icon(Icons.Default.Psychology, contentDescription = null) }
        )
        Tab(
            selected = selectedType == SearchType.KEYWORD,
            onClick = { onTypeSelected(SearchType.KEYWORD) },
            text = { Text("🔍 Palabras Clave") },
            icon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
    }
}

@Composable
fun SearchTypeExplanation(type: SearchType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (type) {
                    SearchType.SEMANTIC -> "Busca por significado y contexto usando IA"
                    SearchType.KEYWORD -> "Busca por palabras exactas en títulos y descripciones"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SearchResultCard(
    module: SearchResultItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = module.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            if (module.subtopics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Topic,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${module.subtopics.size} temas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.TravelExplore,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Explora el contenido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Busca módulos por tema, concepto o palabra clave",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

