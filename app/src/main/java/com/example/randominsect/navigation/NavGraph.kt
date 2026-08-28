package com.example.randominsect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.randominsect.ui.screens.InsectFormScreen
import com.example.randominsect.ui.screens.InsectListScreen
import com.example.randominsect.ui.viewmodel.InsectViewModel

sealed class Screen(val route: String) {
    object ListScreen : Screen("list_screen")
    object FormScreen : Screen("form_screen")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: InsectViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ListScreen.route
    ) {
        composable(Screen.ListScreen.route) {
            InsectListScreen(
                viewModel = viewModel,
                onNavigateToForm = { navController.navigate(Screen.FormScreen.route) }
            )
        }
        composable(Screen.FormScreen.route) {
            InsectFormScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
