package com.example.randominsect.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.randominsect.data.database.AppDatabase
import com.example.randominsect.data.model.Insect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InsectViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val insectDao = db.insectDao()

    // Room automatically updates this Flow whenever data in the 'insects' table changes
    val insects: Flow<List<Insect>> = insectDao.getAllInsects()

    fun addInsect(insect: Insect) {
        viewModelScope.launch {
            insectDao.insertInsect(insect)
        }
    }

    fun deleteInsect(insect: Insect) {
        viewModelScope.launch {
            insectDao.deleteInsect(insect)
        }
    }

    fun updateInsect(updatedInsect: Insect) {
        viewModelScope.launch {
            insectDao.updateInsect(updatedInsect)
        }
    }
}
