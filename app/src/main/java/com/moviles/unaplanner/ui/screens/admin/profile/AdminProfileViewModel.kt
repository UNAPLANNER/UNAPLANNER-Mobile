package com.moviles.unaplanner.ui.screens.admin.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.ChangePasswordRequest
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UserDto
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch

class AdminProfileViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminProfileViewModel(AppContainer.adminRepository) as T
            }
        }
    }

    var uiState by mutableStateOf(AdminProfileUiState())
        private set

    init {
        // Mostrar usuario de sesión inmediatamente (si existe) mientras se refresca desde API
        AuthSession.currentUser?.let { uiState = uiState.copy(user = it) }
        loadProfile()
    }

    fun loadProfile() {
        val userId = AuthSession.currentUser?.id ?: return
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = adminRepository.getProfile(userId)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(
                        user = result.data,
                        isLoading = false,
                        error = null
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

    fun updateProfile(fullName: String, phone: String, department: String) {
        val userId = AuthSession.currentUser?.id ?: return
        uiState = uiState.copy(isUpdating = true)
        viewModelScope.launch {
            val request = UpdateProfileRequest(fullName, phone, department)
            when (val result = adminRepository.updateProfile(userId, request)) {
                is ApiResult.Success -> {
                    AuthSession.currentUser?.let { current ->
                        AuthSession.setUser(result.data.copy(token = current.token))
                    }
                    uiState = uiState.copy(
                        user = result.data,
                        isUpdating = false,
                        showEditModal = false,
                        successMessage = "Perfil actualizado correctamente"
                    )
                }
                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isUpdating = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun changePassword(current: String, new: String) {
        val userId = AuthSession.currentUser?.id ?: return
        uiState = uiState.copy(isUpdating = true)
        viewModelScope.launch {
            val request = ChangePasswordRequest(current, new)
            when (val result = adminRepository.changePassword(userId, request)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(
                        isUpdating = false,
                        successMessage = "Contraseña cambiada exitosamente"
                    )
                }
                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isUpdating = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun setShowEditModal(show: Boolean) {
        uiState = uiState.copy(showEditModal = show)
    }

    fun clearMessages() {
        uiState = uiState.copy(error = null, successMessage = null)
    }
}

data class AdminProfileUiState(
    val user: UserDto? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showEditModal: Boolean = false
)
