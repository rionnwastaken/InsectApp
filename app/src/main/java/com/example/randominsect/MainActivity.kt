package com.example.randominsect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.randominsect.data.ThemePreferences
import com.example.randominsect.data.AppContextProvider
import com.example.randominsect.data.api.InsectApi
import com.example.randominsect.data.db.InsectDatabase
import com.example.randominsect.navigation.AppNavigation
import com.example.randominsect.ui.theme.RandomInsectTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val client =
            HttpClient(Android) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true // Prevents crashes when API returns extra fields

                            isLenient = true
                        },
                    )
                }
            }

        AppContextProvider.init(this)
        Log.d("insectapp", "Current Global config is $Config")
        val db = InsectDatabase.getDatabase(this)
        InsectApi.init(this, db.blacklistDao(), client)

        // lifecycleScope.launch {
        //     example()
        // }

        super.onCreate(savedInstanceState)
        setContent {
            val isDarkModePref by ThemePreferences.isDarkMode.collectAsState(initial = null)
            val isDarkTheme = isDarkModePref ?: isSystemInDarkTheme()

            val isUserSignedIn = false

            RandomInsectTheme(darkTheme = isDarkTheme) {
                AppNavigation(isUserSignedIn = isUserSignedIn)
            }
        }
    }
}
