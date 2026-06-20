package com.moviles.unaplanner.ui.screens.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.UserDto
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: UserDto) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

private const val PREFS_NAME = "unaplanner_login_prefs"
private const val KEY_EMAIL = "saved_email"
private const val KEY_PASSWORD = "saved_password"
private const val KEY_REMEMBER = "remember_me"

class LoginViewModel(
    application: Application,
    private val repository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _savedEmail = MutableStateFlow(prefs.getString(KEY_EMAIL, "") ?: "")
    val savedEmail: StateFlow<String> = _savedEmail.asStateFlow()

    private val _savedPassword = MutableStateFlow(prefs.getString(KEY_PASSWORD, "") ?: "")
    val savedPassword: StateFlow<String> = _savedPassword.asStateFlow()

    private val _rememberMe = MutableStateFlow(prefs.getBoolean(KEY_REMEMBER, false))
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    fun login(email: String, pass: String, remember: Boolean) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            when (val result = repository.login(email, pass)) {
                is ApiResult.Success -> {
                    AuthSession.setUser(result.data)
                    saveCredentials(email, pass, remember)
                    _uiState.value = LoginUiState.Success(result.data)
                    registerFcmToken()
                }
                is ApiResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                }
            }
        }
    }

    fun setRememberMe(value: Boolean) {
        _rememberMe.value = value
        if (!value) clearCredentials()
    }

    private fun saveCredentials(email: String, password: String, remember: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_REMEMBER, remember)
            if (remember) {
                putString(KEY_EMAIL, email)
                putString(KEY_PASSWORD, password)
            } else {
                remove(KEY_EMAIL)
                remove(KEY_PASSWORD)
            }
        }.apply()
    }

    private fun clearCredentials() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .putBoolean(KEY_REMEMBER, false)
            .apply()
    }

    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch {
                AppContainer.notificationRepository.registerToken(token)
            }
        }
    }

    fun resetError() {
        _uiState.value = LoginUiState.Idle
    }
}

class LoginViewModelFactory(
    private val application: Application,
    private val repository: AuthRepository = AuthRepository()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(application, repository) as T
    }
}
