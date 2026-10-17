package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaItem
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val trending: List<MediaItem>) : HomeUiState
    data class Error(val message: String, val canRetry: Boolean = true) : HomeUiState
}

class HomeViewModel(private val repository: MediaRepository = MediaRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getTrending()
                .onSuccess { response ->
                    if (response.success && response.results != null) {
                        _uiState.value = HomeUiState.Success(response.results)
                    } else {
                        val errorMsg = response.error?.message ?: "Trending data temporarily unavailable."
                        _uiState.value = HomeUiState.Error(errorMsg, canRetry = false)
                    }
                }
                .onFailure { exception ->
                    _uiState.value = HomeUiState.Error(
                        exception.message ?: "An unexpected error occurred. Please check your connection."
                    )
                }
        }
    }
}
