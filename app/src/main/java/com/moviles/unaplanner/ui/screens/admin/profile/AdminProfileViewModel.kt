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
        // Pre-populate the UI with session data while the API returns the full profile.
        AuthSession.currentUser?.let { uiState = uiState.copy(user = it) }
        loadProfile()
    }

    fun loadProfile() {
        val userId = AuthSession.currentUser?.id ?: run {
            uiState = uiState.copy(error = "Sesion no iniciada")
            return
        }

        uiState = uiState.copy(isInitialLoading = true, error = null)
        viewModelScope.launch {
            when (val result = adminRepository.getProfile(userId)) {
                is ApiResult.Success -> {
                    persistProfileInSession(result.data)
                    uiState = uiState.copy(
                        user = result.data,
                        isInitialLoading = false,
                        error = null
                    )
                }

                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isInitialLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun updateProfile(fullName: String, phone: String, department: String) {
        val validationError = validateProfile(fullName, phone, department)
        if (validationError != null) {
            uiState = uiState.copy(error = validationError)
            return
        }

        val userId = AuthSession.currentUser?.id ?: return
        uiState = uiState.copy(isSavingProfile = true, error = null)
        viewModelScope.launch {
            val request = UpdateProfileRequest(
                fullName = fullName.trim(),
                phone = phone.trim(),
                department = department.trim()
            )

            when (val result = adminRepository.updateProfile(userId, request)) {
                is ApiResult.Success -> {
                    persistProfileInSession(result.data)
                    uiState = uiState.copy(
                        user = result.data,
                        isSavingProfile = false,
                        showEditModal = false,
                        successMessage = "Perfil actualizado correctamente"
                    )
                    loadProfile()
                }

                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isSavingProfile = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun changePassword(current: String, new: String) {
        val validationError = validatePasswordChange(current, new)
        if (validationError != null) {
            uiState = uiState.copy(error = validationError)
            return
        }

        val userId = AuthSession.currentUser?.id ?: return
        uiState = uiState.copy(isChangingPassword = true, error = null)
        viewModelScope.launch {
            val request = ChangePasswordRequest(currentPassword = current, newPassword = new)
            when (val result = adminRepository.changePassword(userId, request)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(
                        isChangingPassword = false,
                        passwordChangeSuccessVersion = uiState.passwordChangeSuccessVersion + 1,
                        successMessage = "Contrasena cambiada exitosamente"
                    )
                }

                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        isChangingPassword = false,
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

    private fun persistProfileInSession(profile: UserDto) {
        AuthSession.currentUser?.let { current ->
            AuthSession.setUser(profile.copy(token = current.token))
        }
    }

    private fun validateProfile(fullName: String, phone: String, department: String): String? {
        return when {
            fullName.isBlank() -> "Ingresa el nombre completo."
            department.isBlank() -> "Ingresa el departamento."
            phone.isBlank() -> "Ingresa el telefono institucional."
            phone.any { it.isLetter() } -> "El telefono no debe contener letras."
            !Regex("^2277-\\d{4}$").matches(phone.trim()) -> "El telefono debe usar el formato 2277-XXXX."
            else -> null
        }
    }

    private fun validatePasswordChange(current: String, new: String): String? {
        return when {
            current.isBlank() -> "Ingresa la contrasena actual."
            new.length < 8 -> "La nueva contrasena debe tener al menos 8 caracteres."
            else -> null
        }
    }
}

data class AdminProfileUiState(
    val user: UserDto? = null,
    val isInitialLoading: Boolean = false,
    val isSavingProfile: Boolean = false,
    val isChangingPassword: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showEditModal: Boolean = false,
    val passwordChangeSuccessVersion: Int = 0
) {
    val isBusy: Boolean
        get() = isSavingProfile || isChangingPassword
}
