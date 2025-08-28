package com.example.tourbuddy.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.*
import java.util.Locale

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onCityFetched: (String) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val geocoder = Geocoder(context, Locale.getDefault())

    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            location?.let {
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val cityName = addresses?.firstOrNull()?.locality ?: "Your City"
                onCityFetched(cityName)
            } ?: onCityFetched("Unknown City")
        }
        .addOnFailureListener {
            onCityFetched("Unknown City")
        }
}