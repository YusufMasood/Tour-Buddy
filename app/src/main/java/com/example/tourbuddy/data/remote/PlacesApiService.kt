package com.example.tourbuddy.data.remote

import com.android.volley.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String?,
        @Query("keyword") keyword: String?,
        @Query("key") apiKey: String
    ): PlacesResponse
}