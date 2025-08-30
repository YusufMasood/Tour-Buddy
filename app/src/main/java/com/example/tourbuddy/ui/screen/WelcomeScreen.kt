package com.example.tourbuddy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tourbuddy.ui.theme.TourBuddyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.tourbuddy.data.EnableLocationSetting
import com.example.tourbuddy.data.isLocationEnabled
import com.google.accompanist.permissions.isGranted


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(navController : NavController) {

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var showLocationPrompt by remember { mutableStateOf(false) }

    // This effect runs once to ask for app permission
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // This effect runs when the app permission status changes
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            // If permission is granted, check if the device's GPS is actually on
            if (!isLocationEnabled(context)) {
                showLocationPrompt = true
            }
        }
    }

    if (showLocationPrompt) {
        // This composable will show the dialog to enable GPS
        EnableLocationSetting(
            onLocationSettingEnabled = { showLocationPrompt = false },
            onLocationSettingCancelled = { /* User cancelled, you might want to show a message */ }
        )
    }

    // 1. Get the color from the theme here.
    val backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 2. Pass the color as a parameter.
        RedFortBackground(color = backgroundColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(10f))

            Text(
                text = "Welcome to Delhi",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                lineHeight = 50.sp
            )
            Text(
                text = "Your personal tour buddy awaits",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.weight(2f))

            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = "Let's Go", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 3. Update the function to accept the color.
@Composable
fun RedFortBackground(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.6f)
            lineTo(size.width * 0.15f, size.height * 0.45f)
            lineTo(size.width * 0.25f, size.height * 0.5f)
            lineTo(size.width * 0.25f, size.height * 0.4f)
            lineTo(size.width * 0.35f, size.height * 0.4f)
            lineTo(size.width * 0.35f, size.height * 0.5f)
            lineTo(size.width * 0.5f, size.height * 0.35f)
            lineTo(size.width * 0.65f, size.height * 0.5f)
            lineTo(size.width * 0.65f, size.height * 0.4f)
            lineTo(size.width * 0.75f, size.height * 0.4f)
            lineTo(size.width * 0.75f, size.height * 0.5f)
            lineTo(size.width * 0.85f, size.height * 0.45f)
            lineTo(size.width * 0.9f, size.height * 0.6f)
            lineTo(size.width * 0.1f, size.height * 0.6f)
            close()
        }
        drawPath(
            path = path,
            color = color, // 4. Use the passed-in color here.
            style = Stroke(width = 8f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    TourBuddyTheme {

        val navController = rememberNavController()
        WelcomeScreen(navController)
    }
}