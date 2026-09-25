package com.example.randominsect.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randominsect.data.db.BlackList.BlackListEntity
import com.example.randominsect.data.repository.BlacklistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BlackListViewModel(
    private val repository: BlacklistRepository = BlacklistRepository.getInstance(),
) : ViewModel() {

    val blacklistedInsects: StateFlow<List<BlackListEntity>> = repository.allBlacklistTaxa
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeFromBlacklist(entry: BlackListEntity) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}
