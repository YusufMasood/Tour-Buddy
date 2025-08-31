package com.example.tourbuddy.repository

import androidx.room.Query
import com.example.tourbuddy.data.remote.PlacesApiService
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//import com.example.tourbuddy.data.remote.RetrofitClient

class PlacesRepository(private val apiService: PlacesApiService,
                       private val placesClient : PlacesClient) {



    // This is the function you were calling from the ViewModel.
    // We are now defining it.
    suspend fun getAutocompletePredictions(
        query: String,
        token: AutocompleteSessionToken
    ): List<AutocompletePrediction> {
        // Use suspendCancellableCoroutine to bridge the callback-based API with coroutines
        return suspendCancellableCoroutine { continuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    // On success, resume the coroutine with the list of predictions
                    continuation.resume(response.autocompletePredictions)
                }
                .addOnFailureListener { exception ->
                    // On failure, resume the coroutine with an exception
                    continuation.resumeWithException(exception)
                }
        }
    }
    suspend fun getNearbyPlaces(
        location: String,
        type: String?,
        keyword: String?,
        apiKey: String
    ) = apiService.getNearbyPlaces(location, type = type, keyword = keyword, apiKey = apiKey)
}