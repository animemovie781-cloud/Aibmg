package com.example.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MediaDetail
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val detail: MediaDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: MediaRepository = MediaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getDetail(id)
                .onSuccess { response ->
                    if (response.success && response.item != null) {
                        _uiState.value = DetailUiState.Success(response.item)
                    } else {
                        _uiState.value = DetailUiState.Error(response.error?.message ?: "Failed to load details")
                    }
                }
                .onFailure { exception ->
                    _uiState.value = DetailUiState.Error(exception.message ?: "Network error")
                }
        }
    }
}
