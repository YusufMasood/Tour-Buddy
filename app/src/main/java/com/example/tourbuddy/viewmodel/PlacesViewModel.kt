package com.example.tourbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourbuddy.data.remote.PlaceResult
import com.example.tourbuddy.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

data class PlacesUiState(
    val places: List<PlaceResult> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
)

class PlacesViewModel(private val repository: PlacesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacesUiState())
    val uiState = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun fetchPlaces(location: String, apiKey: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val type = if (_uiState.value.selectedCategory == "All") null else _uiState.value.selectedCategory.lowercase()
                val keyword = if (_uiState.value.searchQuery.isBlank()) null else _uiState.value.searchQuery

                val response = repository.getNearbyPlaces(location, type, keyword, apiKey)
                _uiState.update { it.copy(places = response.results, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to fetch places.") }
            }
        }
    }
}

class PlacesViewModelFactory(private val repository: PlacesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlacesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}