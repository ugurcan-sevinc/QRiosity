package com.ugrcaan.qriosity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ugrcaan.qriosity.data.SavedLinkRepository
import com.ugrcaan.qriosity.model.SavedLink
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainVM(private val repository: SavedLinkRepository) : ViewModel() {

    val savedLinks: StateFlow<List<SavedLink>> = repository.savedLinks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun saveLink(name: String, url: String) {
        viewModelScope.launch {
            repository.saveLink(name, url)
        }
    }

    fun deleteLink(savedLink: SavedLink) {
        viewModelScope.launch {
            repository.deleteLink(savedLink.id)
        }
    }

    companion object {
        fun provideFactory(repository: SavedLinkRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainVM::class.java)) {
                        return MainVM(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
