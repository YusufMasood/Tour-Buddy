package com.example.tourbuddy

import android.app.Application
import com.example.tourbuddy.data.local.AppDatabase
import com.example.tourbuddy.data.remote.RetrofitClient
import com.example.tourbuddy.repository.PlacesRepository

class TourBuddyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    // Add this to create a single instance of the PlacesRepository
    val placesRepository by lazy {
        PlacesRepository(apiService = RetrofitClient.apiService)
    }
}