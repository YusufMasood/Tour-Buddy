package com.example.tourbuddy.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tourbuddy.TourBuddyApplication
import com.example.tourbuddy.ui.navigation.Screen
import com.example.tourbuddy.viewmodel.LocationViewModel
import com.example.tourbuddy.viewmodel.PlacesViewModel
import com.example.tourbuddy.viewmodel.PlacesViewModelFactory

@Composable
fun ParentScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as TourBuddyApplication

    // Create both ViewModels needed for the home screen
    val locationViewModel: LocationViewModel = viewModel()
    val placesViewModel: PlacesViewModel = viewModel(
        factory = PlacesViewModelFactory(application.placesRepository)
    )

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(Screen.Home, Screen.Places, Screen.Favorites, Screen.Settings)

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pass both ViewModels to the HomeScreen
            composable(Screen.Home.route) {
                HomeScreen(
                    locationViewModel = locationViewModel,
                    placesViewModel = placesViewModel
                )
            }
            composable(Screen.Places.route) { PlacesScreen() }
            composable(Screen.Favorites.route) { FavoritesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}