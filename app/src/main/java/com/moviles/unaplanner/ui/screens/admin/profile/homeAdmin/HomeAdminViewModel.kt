package com.moviles.unaplanner.ui.screens.admin.profile.homeAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.AdminDashboard
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch

data class HomeAdminUiState(
    val dashboard: AdminDashboard? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeAdminViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(HomeAdminUiState(isLoading = true))
        private set

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when (val result = repository.getDashboard()) {
                is ApiResult.Success -> uiState = HomeAdminUiState(dashboard = result.data)
                is ApiResult.Error -> uiState = HomeAdminUiState(error = result.message)
            }
        }
    }

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeAdminViewModel() as T
        }
    }
}
