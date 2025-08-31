package com.example.tourbuddy.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourbuddy.BuildConfig
import com.example.tourbuddy.data.getCurrentLocation
import com.example.tourbuddy.viewmodel.*
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.tourbuddy.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(locationViewModel: LocationViewModel, placesViewModel: PlacesViewModel) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val locationState by locationViewModel.locationState.collectAsState()
    val placesState by placesViewModel.uiState.collectAsState()

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            getCurrentLocation(context) { lat, lng, city ->
                locationViewModel.onLocationFetched(lat, lng, city)
            }
        }
    }

    LaunchedEffect(locationState.latitude, placesState.searchQuery, placesState.selectedCategory) {
        locationState.latitude?.let { lat ->
            locationState.longitude?.let { lng ->
                val apiKey = BuildConfig.MAPS_API_KEY
                placesViewModel.fetchPlaces("${lat},${lng}", apiKey)
            }
        }
    }

    Scaffold(
        topBar = { TopBar(placesViewModel = placesViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (locationPermissionState.status.isGranted) {
                if (locationState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Text("Fetching your location...")
                    }
                } else {
                    MainContent(placesState = placesState, locationState = locationState)
                }
            } else {
                PermissionRequestScreen { locationPermissionState.launchPermissionRequest() }
            }
        }
    }
}

@Composable
fun TopBar(placesViewModel: PlacesViewModel) {
    val uiState by placesViewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }
    val filterOptions = listOf("All", "historic_place", "park", "museum", "restaurant", "market")

    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box {
            IconButton(onClick = { showProfileMenu = true }) {
                Icon(Icons.Default.AccountCircle, "Profile")
            }
            DropdownMenu(expanded = showProfileMenu, onDismissRequest = { showProfileMenu = false }) {
                DropdownMenuItem(text = { Text("Logout") }, onClick = { /* Handle logout */ })
            }
        }
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = placesViewModel::onSearchQueryChange,
            placeholder = { Text("Search places...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(32.dp)
        )
        Box {
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(Icons.Default.Menu, "Filter")
            }
            DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                filterOptions.forEach { option ->
                    DropdownMenuItem(text = { Text(option.replaceFirstChar { it.uppercase() }) }, onClick = {
                        placesViewModel.onCategoryChange(option)
                        showFilterMenu = false
                    })
                }
            }
        }
    }
}

@Composable
fun MainContent(placesState: PlacesUiState, locationState: LocationState) {
    val userLocation = LatLng(locationState.latitude ?: 28.6139, locationState.longitude ?: 77.2090)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 14f)
    }
    LaunchedEffect(userLocation) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
                Marker(state = MarkerState(position = userLocation), title = "Your Location")
                placesState.places.forEach { place ->
                    Marker(
                        state = MarkerState(position = LatLng(place.geometry.location.lat, place.geometry.location.lng)),
                        title = place.name,
                        snippet = place.vicinity
                    )
                }
            }
            if (placesState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text("Nearby Places", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(placesState.places) { place ->
                Text(place.name, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(onPermissionRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Location Permission Required", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("This app needs access to your location to show relevant places.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPermissionRequest) {
            Text("Grant Permission")
        }
    }
}