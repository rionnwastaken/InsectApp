package com.example.randominsect.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randominsect.data.AppContextProvider
import com.example.randominsect.data.api.InsectApi
import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import com.example.randominsect.data.db.BlackList.BlackListEntity
import com.example.randominsect.data.repository.FavoriteRepository
import com.example.randominsect.data.repository.BlacklistRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(
    private val favoriteRepository: FavoriteRepository = FavoriteRepository.getInstance(),
    private val blacklistRepository: BlacklistRepository = BlacklistRepository.getInstance()
) : ViewModel() {

    private val _generatedInsect = MutableStateFlow<InsectEntity?>(null)
    val generatedInsect: StateFlow<InsectEntity?> = _generatedInsect

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSaving = MutableStateFlow(false)
    /** true mientras se está guardando el insecto actual en favoritos. */
    val isSaving: StateFlow<Boolean> = _isSaving

    /**
     * true si el insecto generado ya está en favoritos.
     *
     * Se recalcula solo: reacciona tanto a un insecto nuevo como a cualquier
     * cambio en la tabla (por ejemplo, si lo eliminas desde la pantalla de favoritos).
     */
    val isCurrentInsectSaved: StateFlow<Boolean> = combine(
        _generatedInsect,
        favoriteRepository.allFavorites
    ) { insect, favorites ->
        insect != null && favorites.any { it.isSameInsectAs(insect) }
    }
        .catch { throwable ->
            Log.e(TAG, "Error observing favorites", throwable)
            emit(false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = false
        )

    fun generateRandomInsect() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val insect = InsectApi.getRandomInsect()
                _generatedInsect.value = insect
            } catch (e: Exception) {
                Log.e(TAG, "Error generating insect", e)
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Agrega el insecto mostrado a la blacklist y genera uno nuevo de inmediato.
     */
    fun addToBlacklist(description: String = "") {
        val insect = _generatedInsect.value ?: return

        viewModelScope.launch {
            try {
                val blacklistEntry = BlackListEntity(
                    taxa = insect.scientificName ?: insect.commonName ?: "Insecto desconocido",
                    description = description,
                    iNaturalistId = insect.taxonID,
                    isBlacklisted = true
                )

                blacklistRepository.insert(blacklistEntry)

                // Carga un nuevo insecto (la API leerá la blacklist actualizada gracias al DAO)
                generateRandomInsect()

            } catch (e: Exception) {
                Log.e(TAG, "Error adding insect to blacklist", e)
                _error.value = "No se pudo agregar el insecto a la lista negra."
            }
        }
    }

    /**
     * Guarda en la base de datos el insecto actualmente generado.
     *
     * - No hace nada si no hay insecto, si ya se está guardando o si ya es favorito.
     * - `FavoriteRepository.insert()` MUEVE la imagen de la caché al almacenamiento
     *   permanente. Para que la foto que se ve en pantalla no desaparezca (y para que
     *   borrar el favorito más tarde no afecte a esta pantalla), se inserta una COPIA
     *   de la imagen y el archivo original de la caché queda intacto.
     */
    fun saveCurrentInsectToFavorites() {
        val insect = _generatedInsect.value ?: return
        if (_isSaving.value) return

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                // Comprobación autoritativa contra la base de datos (evita duplicados).
                val alreadySaved = favoriteRepository.allFavorites.first()
                    .any { it.isSameInsectAs(insect) }
                if (alreadySaved) return@launch

                val insectToSave = withContext(Dispatchers.IO) {
                    insect.withIndependentImageCopy()
                }
                favoriteRepository.insert(insectToSave)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error saving insect to favorites", e)
                _error.value = "Could not save the insect to favorites."
            } finally {
                _isSaving.value = false
            }
        }
    }

    /**
     * Devuelve una copia del insecto cuya imagen apunta a un archivo NUEVO dentro de
     * cacheDir (el repositorio solo mueve a almacenamiento permanente los archivos que
     * están en la caché). Si el archivo original ya no existe, se guarda sin imagen
     * en lugar de dejar una ruta rota en la base de datos.
     */
    private fun InsectEntity.withIndependentImageCopy(): InsectEntity {
        val path = image_path ?: return this

        val source = File(path)
        if (!source.exists()) return copy(image_path = null)

        val extension = source.extension.ifBlank { "jpg" }
        val imageCopy = File(
            AppContextProvider.get().cacheDir,
            "fav_${taxonID}_${System.currentTimeMillis()}.$extension"
        )
        source.copyTo(imageCopy, overwrite = true)

        return copy(image_path = imageCopy.absolutePath)
    }

    private companion object {
        const val TAG = "insectapp"
    }
}

/**
 * Criterio de "mismo insecto": primero por nombre científico (identifica la especie y no
 * depende del id que devuelva la API); si falta alguno de los dos, por taxonID no nulo.
 */
private fun InsectEntity.isSameInsectAs(other: InsectEntity): Boolean {
    val mine = scientificName?.trim()?.lowercase()
    val theirs = other.scientificName?.trim()?.lowercase()
    if (!mine.isNullOrEmpty() && !theirs.isNullOrEmpty()) return mine == theirs
    return taxonID != 0L && taxonID == other.taxonID
}
