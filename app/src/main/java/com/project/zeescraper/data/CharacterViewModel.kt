package com.project.zeescraper.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterViewModel : ViewModel() {
    private val repository = CharacterRepository()

    private val _characters = MutableStateFlow<List<CharacterList>>(emptyList())
    val characters: StateFlow<List<CharacterList>> = _characters.asStateFlow()

    private val _character = MutableStateFlow<CharacterDetail?>(null)
    val character: StateFlow<CharacterDetail?> = _character.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ✅ Load all characters
    private val _isInitialLoad = MutableStateFlow(true)
    val isInitialLoad: StateFlow<Boolean> = _isInitialLoad.asStateFlow()

    fun loadCharacters(forceRefresh: Boolean = false) {
        if (_characters.value.isNotEmpty() && !forceRefresh) return

        viewModelScope.launch {
            _isInitialLoad.value = _characters.value.isEmpty()
            _isLoading.value = true
            _error.value = null

            repository.getAllCharacters()
                .onSuccess { _characters.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
            _isInitialLoad.value = false
        }
    }



    // ✅ Load character by ID (dengan forceRefresh)
    fun loadCharacterById(id: Int, forceRefresh: Boolean = false) {
        _character.value = null
        if (_character.value != null && !forceRefresh) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getCharacterById(id)
                result
                    .onSuccess { characterDetail ->
                        _character.value = characterDetail
                    }
                    .onFailure { exception ->
                        _error.value = exception.message
                    }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
