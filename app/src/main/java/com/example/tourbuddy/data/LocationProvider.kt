package com.example.tourbuddy.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.*
import java.util.Locale

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onLocationFetched: (Double, Double, String) -> Unit
) {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val geocoder = Geocoder(context, Locale.getDefault())

    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            location?.let {
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val cityName = addresses?.firstOrNull()?.locality ?: "Your City"
                onLocationFetched(it.latitude, it.longitude, cityName)
            } ?: onLocationFetched(28.6139, 77.2090, "Delhi") // Default on failure
        }
        .addOnFailureListener {
            onLocationFetched(28.6139, 77.2090, "Delhi") // Default on failure
        }
}