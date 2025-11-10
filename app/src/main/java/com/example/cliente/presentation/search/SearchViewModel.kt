package com.example.cliente.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cliente.data.model.ModuleDto
import com.example.cliente.data.remote.SearchResultModule
import com.example.cliente.data.repository.SearchRepository
import com.example.cliente.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchResultItem(
    val id: Int,
    val title: String,
    val description: String, // Siempre tendrá un valor (con fallback)
    val subtopics: List<String>,
    val studyPathId: Int
)

data class SearchState(
    val isLoading: Boolean = false,
    val results: List<SearchResultItem> = emptyList(),
    val error: String? = null,
    val searchType: SearchType = SearchType.SEMANTIC,
    val query: String = ""
)

enum class SearchType {
    SEMANTIC,  // pgvector - búsqueda semántica
    KEYWORD    // Typesense - búsqueda por palabras clave
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    /**
     * Realiza búsqueda semántica usando pgvector.
     * GET /search?q=texto
     */
    fun searchSemantic(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState(searchType = SearchType.SEMANTIC)
            return
        }

        _searchState.value = _searchState.value.copy(
            query = query,
            searchType = SearchType.SEMANTIC
        )

        searchRepository.searchSemantic(query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val items = result.data?.map { it.toSearchResultItem() } ?: emptyList()
                    _searchState.value = _searchState.value.copy(
                        results = items,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _searchState.value = _searchState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _searchState.value = _searchState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Realiza búsqueda por keywords usando Typesense.
     * GET /search/typesense?q=texto
     */
    fun searchKeyword(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState(searchType = SearchType.KEYWORD)
            return
        }

        _searchState.value = _searchState.value.copy(
            query = query,
            searchType = SearchType.KEYWORD
        )

        searchRepository.searchTypesense(query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val items = result.data?.map { it.toSearchResultItem() } ?: emptyList()
                    _searchState.value = _searchState.value.copy(
                        results = items,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _searchState.value = _searchState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _searchState.value = _searchState.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setSearchType(type: SearchType) {
        val currentQuery = _searchState.value.query
        _searchState.value = SearchState(searchType = type, query = currentQuery)

        // Re-ejecutar búsqueda si hay query
        if (currentQuery.isNotBlank()) {
            when (type) {
                SearchType.SEMANTIC -> searchSemantic(currentQuery)
                SearchType.KEYWORD -> searchKeyword(currentQuery)
            }
        }
    }

    fun clearSearch() {
        _searchState.value = SearchState(searchType = _searchState.value.searchType)
    }
}

// Extension functions para convertir a SearchResultItem
private fun SearchResultModule.toSearchResultItem() = SearchResultItem(
    id = this.id,
    title = this.title,
    description = (this.description ?: this.content) ?: "Sin descripción", // Usa content o fallback
    subtopics = this.subtopics ?: emptyList(), // Ahora puede tener subtopics
    studyPathId = this.study_path_id ?: 0 // Usa 0 si es null
)

private fun ModuleDto.toSearchResultItem() = SearchResultItem(
    id = this.id,
    title = this.title,
    description = this.description,
    subtopics = this.subtopics,
    studyPathId = this.study_path_id
)

