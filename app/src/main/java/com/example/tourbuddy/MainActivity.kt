package com.example.tourbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourbuddy.data.remote.RetrofitClient
import com.example.tourbuddy.repository.PlacesRepository
import com.example.tourbuddy.ui.screens.HomeScreen
import com.example.tourbuddy.ui.screens.LoginScreen
import com.example.tourbuddy.ui.screens.ParentScreen
import com.example.tourbuddy.ui.screens.WelcomeScreen
import com.example.tourbuddy.ui.theme.TourBuddyTheme
import com.example.tourbuddy.viewmodel.*
import com.google.android.libraries.places.api.Places


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val application = application as TourBuddyApplication
        val userDao = application.database.userDao()

        val apiKey = BuildConfig.MAPS_API_KEY

        if(!Places.isInitialized()){
            Places.initialize(applicationContext,apiKey)
        }


        val placesClient = Places.createClient(this)


        val placesApiService = RetrofitClient.apiService

        val placesRepository = PlacesRepository(placesApiService, placesClient)

        setContent {
            TourBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create ViewModels using their factories
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(userDao)
                    )
                    val locationViewModel: LocationViewModel = viewModel()

                    val placesViewModel: PlacesViewModel = viewModel(
                        factory = PlacesViewModelFactory(placesRepository)
                    )

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {
                        composable("welcome") { WelcomeScreen(navController) }
                        composable("login") { LoginScreen(viewModel = loginViewModel, navController = navController) }

                        composable("home") {
                            ParentScreen()

                        }
                    }
                }
            }
        }
    }
}