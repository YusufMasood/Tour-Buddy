package com.example.tourbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourbuddy.ui.screens.LoginScreen
import com.example.tourbuddy.ui.screens.WelcomeScreen
import com.example.tourbuddy.ui.theme.TourBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TourBuddyTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "Welcome"){
                    composable ("Welcome"){WelcomeScreen(navController)}
                    composable ("Login Screen"){ LoginScreen() }
                }
            }
        }
    }
}



