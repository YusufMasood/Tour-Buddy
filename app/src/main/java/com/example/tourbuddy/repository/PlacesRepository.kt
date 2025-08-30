package com.example.tourbuddy.repository

import com.example.tourbuddy.data.remote.PlacesApiService
//import com.example.tourbuddy.data.remote.RetrofitClient

class PlacesRepository(private val apiService: PlacesApiService) {
    suspend fun getNearbyPlaces(
        location: String,
        type: String?,
        keyword: String?,
        apiKey: String
    ) = apiService.getNearbyPlaces(location, type = type, keyword = keyword, apiKey = apiKey)
}