package com.example.tourbuddy

import android.app.Application
import com.example.tourbuddy.data.local.AppDatabase
import com.example.tourbuddy.data.remote.RetrofitClient
import com.example.tourbuddy.repository.PlacesRepository
import com.google.android.libraries.places.api.Places
import kotlin.getValue

class TourBuddyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val placesRepository by lazy {
        // First, initialize the Google Places SDK
        if (!Places.isInitialized()) {
            val apiKey = BuildConfig.MAPS_API_KEY
            Places.initialize(applicationContext, apiKey)
        }
        // Then, create the PlacesClient
        val placesClient = Places.createClient(this)

        // Now, provide BOTH dependencies to the repository
        PlacesRepository(
            apiService = RetrofitClient.apiService,
            placesClient = placesClient
        )
    }
}