package com.example.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaItem
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<MediaItem>) : SearchUiState
    data class Error(val message: String) : SearchUiState
    data object Empty : SearchUiState
}

class SearchViewModel(private val repository: MediaRepository = MediaRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            _uiState.value = SearchUiState.Loading
            repository.search(query)
                .onSuccess { response ->
                    if (response.success && !response.results.isNullOrEmpty()) {
                        _uiState.value = SearchUiState.Success(response.results)
                    } else if (response.success) {
                        _uiState.value = SearchUiState.Empty
                    } else {
                        _uiState.value = SearchUiState.Error(response.error?.message ?: "Search failed")
                    }
                }
                .onFailure { exception ->
                    _uiState.value = SearchUiState.Error(exception.message ?: "Network error")
                }
        }
    }
}
