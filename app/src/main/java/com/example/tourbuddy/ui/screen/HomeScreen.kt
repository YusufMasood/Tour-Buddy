package com.example.tourbuddy.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tourbuddy.data.getCurrentLocation
import com.example.tourbuddy.ui.theme.TourBuddyTheme

import com.example.tourbuddy.viewmodel.LocationState
import com.example.tourbuddy.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(locationViewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val locationState by locationViewModel.locationState.collectAsState()

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            getCurrentLocation(context) { city ->
                locationViewModel.onCityFetched(city)
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(locationState = locationState)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (locationPermissionState.status.isGranted) {
                if (locationState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fetching your location...")
                    }
                } else {
                    MainContent(city = locationState.city ?: "Your City")
                }
            } else {
                PermissionRequestScreen(
                    onPermissionRequest = { locationPermissionState.launchPermissionRequest() }
                )
            }
        }
    }
}

@Composable
fun TopBar(locationState: LocationState) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search places...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                IconButton(onClick = { showFilterMenu = true }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Filter")
                }
                DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                    DropdownMenuItem(text = { Text("All") }, onClick = {})
                    DropdownMenuItem(text = { Text("Historic Places") }, onClick = {})
                    DropdownMenuItem(text = { Text("Restaurants") }, onClick = {})
                }
            }
        }
    }
}

@Composable
fun MainContent(city: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        ) {
            val cityLocation = LatLng(28.6139, 77.2090) // Placeholder
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(cityLocation, 12f)
            }
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
                Marker(state = MarkerState(position = cityLocation), title = "Your Location")
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Famous Places in $city", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                val famousPlaces = listOf("Red Fort", "Qutub Minar", "India Gate", "Humayun's Tomb", "Lotus Temple")
                items(famousPlaces) { place ->
                    Text(place, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(onPermissionRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Location Permission Required", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("This app needs access to your location to show relevant places in your city.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPermissionRequest) {
            Text("Grant Permission")
        }

        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TourBuddyTheme {
        PermissionRequestScreen(onPermissionRequest = {})
    }
}