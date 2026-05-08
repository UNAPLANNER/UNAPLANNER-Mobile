package com.moviles.unaplanner.ui.screens.contact.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.CampusContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContactDetailUiState {
    object Loading : ContactDetailUiState
    data class Success(val contact: CampusContact) : ContactDetailUiState
    data class Error(val message: String) : ContactDetailUiState
}

class CampusContactsDetailViewModel(
    private val repository: CampusContactsRepository = CampusContactsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactDetailUiState>(ContactDetailUiState.Loading)
    val uiState: StateFlow<ContactDetailUiState> = _uiState.asStateFlow()

    fun fetchContactDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = ContactDetailUiState.Loading
            when (val result = repository.getCampusContact(id)) {
                is ApiResult.Success -> {
                    _uiState.value = ContactDetailUiState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _uiState.value = ContactDetailUiState.Error(result.message)
                }
            }
        }
    }
}
