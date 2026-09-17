package com.example.randominsect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.randominsect.ui.components.InsectCard
import com.example.randominsect.ui.viewmodel.InsectViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle // O collectAsState()
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsectListScreen(
    viewModel: InsectViewModel,
    onNavigateToForm: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Insectos") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Insecto")
            }
        }
    ) { paddingValues ->
        val insects by viewModel.insects.collectAsStateWithLifecycle(initialValue = emptyList())

        if (insects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay insectos registrados",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                items(
                    items = insects,
                    key = { insect -> insect.id }
                ) { insect ->
                    InsectCard(
                        insect = insect,
                        onDelete = { viewModel.deleteInsect(it) }
                    )
                }
            }
        }
    }
}
