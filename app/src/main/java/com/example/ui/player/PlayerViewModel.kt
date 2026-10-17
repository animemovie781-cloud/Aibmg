package com.example.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.StreamSource
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Success(val sources: List<StreamSource>) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel(private val repository: MediaRepository = MediaRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _selectedSource = MutableStateFlow<StreamSource?>(null)
    val selectedSource: StateFlow<StreamSource?> = _selectedSource.asStateFlow()

    fun loadStream(id: String) {
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            repository.getStream(id)
                .onSuccess { response ->
                    if (response.success && !response.sources.isNullOrEmpty()) {
                        val sources = response.sources.sortedByDescending { it.quality }
                        _uiState.value = PlayerUiState.Success(sources)
                        // Select the highest quality by default if not already selected
                        if (_selectedSource.value == null) {
                            _selectedSource.value = sources.first()
                        }
                    } else {
                        _uiState.value = PlayerUiState.Error(response.error?.message ?: "No streaming sources available")
                    }
                }
                .onFailure { exception ->
                    _uiState.value = PlayerUiState.Error(exception.message ?: "Network error")
                }
        }
    }

    fun selectSource(source: StreamSource) {
        _selectedSource.value = source
    }
}
