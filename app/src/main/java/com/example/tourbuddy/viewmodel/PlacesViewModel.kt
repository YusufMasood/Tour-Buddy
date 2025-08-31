package com.example.tourbuddy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tourbuddy.data.remote.PlaceResult
import com.example.tourbuddy.repository.PlacesRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlacesUiState(
    val places: List<PlaceResult> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null,
    val suggestions: List<AutocompletePrediction> = emptyList() // <-- ADD THIS
)

class PlacesViewModel(private val repository: PlacesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PlacesUiState())
    val uiState = _uiState.asStateFlow()

    // <-- ADD THIS: Session token for Google Places API billing
    private var sessionToken: AutocompleteSessionToken? = null

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // <-- MODIFIED: This function now also fetches suggestions
        if (query.isNotBlank()) {
            // Start a new session if one doesn't exist
            if (sessionToken == null) {
                sessionToken = AutocompleteSessionToken.newInstance()
            }
            fetchAutocompleteSuggestions(query)
        } else {
            // Clear suggestions when the search query is empty
            _uiState.update { it.copy(suggestions = emptyList()) }
        }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    // <-- ADD THIS: Function to handle selecting a suggestion
    fun onSuggestionSelected(prediction: AutocompletePrediction) {
        _uiState.update {
            it.copy(
                searchQuery = prediction.getPrimaryText(null).toString(),
                suggestions = emptyList() // Clear suggestions after selection
            )
        }
        // A place has been selected, so the session is over.
        sessionToken = null
    }

    // <-- ADD THIS: Function to clear suggestions when the dropdown is dismissed
    fun clearSuggestions() {
        _uiState.update { it.copy(suggestions = emptyList()) }
    }

    private fun fetchAutocompleteSuggestions(query: String) {
        viewModelScope.launch {
            try {
                // Ensure sessionToken is not null before making the request
                sessionToken?.let { token ->
                    val predictions = repository.getAutocompletePredictions(query, token)
                    _uiState.update { it.copy(suggestions = predictions) }
                }
            } catch (e: Exception) {
                Log.e("PlacesViewModel", "Autocomplete fetch failed", e)
                // Optionally update UI with an error message
                _uiState.update { it.copy(error = "Failed to load suggestions.") }
            }
        }
    }

    fun fetchPlaces(location: String, apiKey: String) {
        // ... (This function remains unchanged)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, suggestions = emptyList()) } // Clear suggestions before a new search
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
    // ... (This class remains unchanged)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlacesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}