package com.example.tourbuddy.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LocationState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val city: String? = null,
    val isLoading: Boolean = true
)

class LocationViewModel : ViewModel() {
    private val _locationState = MutableStateFlow(LocationState())
    val locationState = _locationState.asStateFlow()

    fun onLocationFetched(lat: Double, lng: Double, city: String) {
        _locationState.update {
            it.copy(latitude = lat, longitude = lng, city = city, isLoading = false)
        }
    }
}