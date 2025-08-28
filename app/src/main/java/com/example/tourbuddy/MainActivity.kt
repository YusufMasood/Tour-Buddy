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
import com.example.tourbuddy.ui.screens.HomeScreen
import com.example.tourbuddy.ui.screens.LoginScreen
import com.example.tourbuddy.ui.screens.ParentScreen
import com.example.tourbuddy.ui.screens.WelcomeScreen
import com.example.tourbuddy.ui.theme.TourBuddyTheme
import com.example.tourbuddy.viewmodel.LoginViewModel
import com.example.tourbuddy.viewmodel.LoginViewModelFactory
import com.example.tourbuddy.viewmodel.LocationViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the UserDao from our Application class
        val application = application as TourBuddyApplication
        val userDao = application.database.userDao()

        setContent {
            TourBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create the ViewModel using our custom factory
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(userDao)
                    )

                    val locationViewModel: LocationViewModel = viewModel()


                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {
                        composable("welcome") { WelcomeScreen(navController) }
                        // Pass the ViewModel to the LoginScreen
                        composable("login") { LoginScreen(viewModel = loginViewModel, navController = navController) }

                        composable("home") {   ParentScreen() }


                    }

                }
            }
        }
    }
}