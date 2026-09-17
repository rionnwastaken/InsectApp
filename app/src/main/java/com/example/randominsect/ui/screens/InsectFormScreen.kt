package com.example.randominsect.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.randominsect.data.model.Insect
import com.example.randominsect.ui.viewmodel.InsectViewModel

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.compose.ui.platform.LocalContext

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "insect_${UUID.randomUUID()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath // Returns local file path
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsectFormScreen(
    viewModel: InsectViewModel,
    onNavigateBack: () -> Unit
) {
    var nombreCientifico by remember { mutableStateOf("") }
    var nombreComun by remember { mutableStateOf("") }
    var orden by remember { mutableStateOf("") }
    var habitat by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var nombreCientificoTouched by remember { mutableStateOf(false) }
    var nombreComunTouched by remember { mutableStateOf(false) }

    val isNombreCientificoValid = nombreCientifico.isNotBlank()
    val isNombreComunValid = nombreComun.isNotBlank()
    val isFormValid = isNombreCientificoValid && isNombreComunValid

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Registrar Insecto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombreCientifico,
                onValueChange = {
                    nombreCientifico = it
                    nombreCientificoTouched = true
                },
                label = { Text("Nombre Científico *") },
                isError = nombreCientificoTouched && !isNombreCientificoValid,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreCientificoTouched && !isNombreCientificoValid) {
                Text(
                    text = "El nombre científico es obligatorio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = nombreComun,
                onValueChange = {
                    nombreComun = it
                    nombreComunTouched = true
                },
                label = { Text("Nombre Común *") },
                isError = nombreComunTouched && !isNombreComunValid,
                modifier = Modifier.fillMaxWidth()
            )
            if (nombreComunTouched && !isNombreComunValid) {
                Text(
                    text = "El nombre común es obligatorio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = orden,
                onValueChange = { orden = it },
                label = { Text("Orden") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = habitat,
                onValueChange = { habitat = it },
                label = { Text("Hábitat") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { photoPickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar Imagen")
            }

            selectedImageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Vista previa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

	  val context = LocalContext.current
            Button(
                onClick = {
                    if (isFormValid) {
		      val savedImagePath = selectedImageUri?.let { uri ->
				      saveImageToInternalStorage(context, uri)
				  }
                        val newInsect = Insect(
                            nombreCientifico = nombreCientifico.trim(),
                            nombreComun = nombreComun.trim(),
                            orden = orden.trim(),
                            habitat = habitat.trim(),
                            imageUri = savedImagePath
                        )
                        viewModel.addInsect(newInsect)
                        onNavigateBack()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
