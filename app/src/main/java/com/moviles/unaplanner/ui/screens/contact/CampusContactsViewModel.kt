package com.moviles.unaplanner.ui.screens.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.CampusContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContactsUiState {
    object Loading : ContactsUiState
    data class Success(val contacts: List<CampusContact>) : ContactsUiState
    data class Error(val message: String) : ContactsUiState
    object Empty : ContactsUiState
}

class CampusContactsViewModel(
    private val repository: CampusContactsRepository = CampusContactsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactsUiState>(ContactsUiState.Loading)
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private var allContacts: List<CampusContact> = emptyList()
    private var currentQuery: String = ""
    private var currentCategory: String = "Todos"

    init {
        fetchContacts()
    }

    fun fetchContacts() {
        viewModelScope.launch {
            _uiState.value = ContactsUiState.Loading
            when (val result = repository.getCampusContacts()) {
                is ApiResult.Success -> {
                    allContacts = result.data
                    applyFilters()
                }
                is ApiResult.Error -> {
                    _uiState.value = ContactsUiState.Error(result.message)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun updateCategory(category: String) {
        currentCategory = category
        applyFilters()
    }

    private fun applyFilters() {
        if (allContacts.isEmpty()) {
            _uiState.value = ContactsUiState.Empty
            return
        }

        val filtered = allContacts.filter { contact ->
            val matchesQuery = if (currentQuery.isBlank()) true else {
                contact.departmentName?.contains(currentQuery, ignoreCase = true) == true ||
                contact.description?.contains(currentQuery, ignoreCase = true) == true
            }

            val matchesCategory = if (currentCategory == "Todos") true else {
                // Filtro inteligente: busca la categoría en el nombre o descripción
                contact.departmentName?.contains(currentCategory, ignoreCase = true) == true ||
                contact.description?.contains(currentCategory, ignoreCase = true) == true
            }

            matchesQuery && matchesCategory
        }

        if (filtered.isEmpty()) {
            _uiState.value = ContactsUiState.Empty
        } else {
            _uiState.value = ContactsUiState.Success(filtered)
        }
    }
}
