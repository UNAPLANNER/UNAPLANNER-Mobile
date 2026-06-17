package com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch

data class CareerAdminUiState(
    val careers: List<Career> = emptyList(),
    val filteredCareers: List<Career> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CareerAdminViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CareerAdminUiState())
        private set

    init {
        loadCareers()
    }

    fun loadCareers() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when (val result = repository.getCareers()) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(
                        careers = result.data,
                        filteredCareers = filterCareers(result.data, uiState.searchQuery),
                        isLoading = false
                    )
                }

                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(
            searchQuery = query,
            filteredCareers = filterCareers(uiState.careers, query)
        )
    }

    private fun filterCareers(careers: List<Career>, query: String): List<Career> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return careers

        return careers.filter { career ->
            career.name.lowercase().contains(normalizedQuery) ||
                career.code.lowercase().contains(normalizedQuery) ||
                (career.campusName?.lowercase()?.contains(normalizedQuery) ?: false)
        }
    }

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CareerAdminViewModel() as T
        }
    }
}
