package com.example.tourbuddy.data.remote

import com.squareup.moshi.Json

data class PlacesResponse(val results: List<PlaceResult>)

data class PlaceResult(
    @Json(name = "place_id") val placeId: String,
    val name: String,
    val geometry: Geometry,
    val vicinity: String?
)
data class Geometry(val location: Location)
data class Location(val lat: Double, val lng: Double)