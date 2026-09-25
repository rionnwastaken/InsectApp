package com.example.randominsect.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.io.File

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val insect by viewModel.generatedInsect.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaved by viewModel.isCurrentInsectSaved.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val uriHandler = LocalUriHandler.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(top = 40.dp, bottom = 16.dp), // Adjust for status bar space
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Main Menu",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), thickness = 1.dp)

        // Generate Button Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { viewModel.generateRandomInsect() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = if (isLoading) "Generating..." else "Generate",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), thickness = 1.dp)

        // Center Content
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (insect == null) {
                Text(
                    text = "No Animal Has Been Generated",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // Display image if image_path exists
                    insect?.image_path?.let { path ->
                        val imageFile = File(path)
                        if (imageFile.exists()) {
                            AsyncImage(
                                model = imageFile,
                                contentDescription = insect?.commonName ?: "Insect Image",
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Text(
                        text = insect?.commonName ?: insect?.scientificName ?: "Unknown Insect",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (insect?.commonName != null && insect?.scientificName != null) {
                        Text(
                            text = insect?.scientificName ?: "",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    //Fila de acciones (Botón de Favoritos + Botón de Blacklist)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AddToFavoritesButton(
                            isSaved = isSaved,
                            isSaving = isSaving,
                            onClick = { viewModel.saveCurrentInsectToFavorites() }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        //Botón para agregar a la lista negra
                        Button(
                            onClick = { viewModel.addToBlacklist() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Blacklist",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Blacklist",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    val wikiUrl = insect?.wikipedia_url
                    if (!wikiUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                try {
                                    uriHandler.openUri(wikiUrl)
                                } catch (e: Exception) {
                                    android.util.Log.e("MainScreen", "Error opening Wikipedia URL: $wikiUrl", e)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Link",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Error message (generation or saving failures)
        error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Navigation Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onNavigateToFavorites,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Favorites", color = MaterialTheme.colorScheme.onSecondary)
            }
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Settings", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

/**
 * Botón para guardar el insecto actual.
 *  - Normal:   "Add to Favorites"      (corazón vacío, habilitado)
 *  - Guardando: "Saving..."            (deshabilitado)
 *  - Guardado:  "Saved to Favorites"   (corazón lleno, deshabilitado)
 */
@Composable
private fun AddToFavoritesButton(
    isSaved: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Button(
        onClick = onClick,
        enabled = !isSaved && !isSaving,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            disabledContainerColor = if (isSaved) colors.primaryContainer else colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = if (isSaved) colors.onPrimaryContainer else colors.onSurface.copy(alpha = 0.38f)
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
                isSaved -> "Saved to Favorites"
                isSaving -> "Saving..."
                else -> "Add to Favorites"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
