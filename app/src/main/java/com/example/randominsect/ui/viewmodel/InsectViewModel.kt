package com.example.randominsect.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.randominsect.data.model.Insect

class InsectViewModel : ViewModel() {
    private val _insects = mutableStateListOf<Insect>()
    val insects: SnapshotStateList<Insect> get() = _insects

    fun addInsect(insect: Insect) {
        _insects.add(insect)
    }

    fun deleteInsect(insect: Insect) {
        _insects.remove(insect)

    }


    fun updateInsect(updatedInsect: Insect) {
        val index = _insects.indexOfFirst { it.id == updatedInsect.id }
        if (index != -1) {
            _insects[index] = updatedInsect
        }
    }
}
