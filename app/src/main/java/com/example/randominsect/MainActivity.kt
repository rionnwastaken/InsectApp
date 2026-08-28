package com.example.randominsect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.randominsect.navigation.NavGraph
import com.example.randominsect.ui.theme.RandomInsectTheme
import com.example.randominsect.ui.viewmodel.InsectViewModel

class MainActivity : ComponentActivity() {
    private val insectViewModel: InsectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RandomInsectTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    viewModel = insectViewModel
                )
            }
        }
    }
}
