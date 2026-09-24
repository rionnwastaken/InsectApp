package com.example.randominsect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.randominsect.ui.google.AuthScreen
import com.example.randominsect.ui.favorites.FavoritesScreen
import com.example.randominsect.ui.main.MainScreen
import com.example.randominsect.ui.settings.BlackListScreen
import com.example.randominsect.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

// 1. Define Type-Safe Destinations
@Serializable object AuthRoute
@Serializable object MainRoute
@Serializable object FavoritesRoute
@Serializable object SettingsRoute
@Serializable object BlacklistRoute

// 2. Set Up Navigation Graph
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    isUserSignedIn: Boolean // Pass your auth state here (e.g., from a ViewModel)
) {
    NavHost(
        navController = navController,
        startDestination = if (isUserSignedIn) MainRoute else AuthRoute
    ) {
        // Authentication Screen
        composable<AuthRoute> {
            AuthScreen(
                onSignInSuccess = {
                    navController.navigate(MainRoute) {
                        popUpTo<AuthRoute> { inclusive = true } // Removes AuthScreen from backstack
                    }
                }
            )
        }

        // Main App Screens
        composable<MainRoute> {
            MainScreen(
                onNavigateToFavorites = { navController.navigate(FavoritesRoute) },
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }

        composable<FavoritesRoute> {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBlacklist = { navController.navigate(BlacklistRoute) },
//                onSignOut = {
//                    navController.navigate(AuthRoute) {
//                        popUpTo<MainRoute> { inclusive = true } // Clear app screens on sign-out
//                    }
//                }
            )
        }

        composable<BlacklistRoute> {
            BlackListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
