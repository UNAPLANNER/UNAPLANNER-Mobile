package com.moviles.unaplanner.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.data.StudentSession
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.StudentProfileDto
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UpdateStudentProfileRequest
import com.moviles.unaplanner.data.repository.ProfileStudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileStudentViewModel(
    private val repository: ProfileStudentRepository
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val profile: StudentProfileDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    fun setInitialProfile(profile: StudentProfileDto) {
        _uiState.value = UiState.Success(profile)
    }

    fun updateProfile(
        userId: Int,
        request: UpdateStudentProfileRequest
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val updated = repository.updateProfile(userId, request)

                StudentSession.setProfile(
                    StudentSession.profile!!.copy(
                        fullName   = updated.fullName,
                        careerId   = updated.careerId,
                        careerName = updated.careerName,
                        enterYear  = updated.enterYear
                    )
                )
                _uiState.value = UiState.Success(updated)
                _saveSuccess.value = true

            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    e.message ?: "Error updating profile"
                )
            }
        }
    }

    fun resetSaveState() {
        _saveSuccess.value = false
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = ProfileStudentRepository(RetrofitClient.apiService)
                @Suppress("UNCHECKED_CAST")
                return ProfileStudentViewModel(repository) as T
            }
        }
    }
}


