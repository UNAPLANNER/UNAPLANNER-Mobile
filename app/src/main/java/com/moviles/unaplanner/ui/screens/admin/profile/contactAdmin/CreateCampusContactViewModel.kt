package com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch
import org.json.JSONObject

data class CreateContactUiState(
    val departmentName: String = "",
    val phone: String = "",
    val email: String = "",
    val description: String = "",
    val campusId: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class CreateCampusContactViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CreateContactUiState())
        private set

    init {
        // Inicializar con el campus del admin si está disponible
        uiState = uiState.copy(campusId = AuthSession.currentUser?.campusId ?: 1)
    }

    fun onDepartmentNameChange(value: String) {
        uiState = uiState.copy(departmentName = value)
    }

    fun onPhoneChange(value: String) {
        uiState = uiState.copy(phone = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onDescriptionChange(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun createContact() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, fieldErrors = emptyMap())
            
            val newContact = CampusContact(
                id = 0,
                campusId = uiState.campusId ?: 1,
                departmentName = uiState.departmentName,
                phone = uiState.phone,
                email = if (uiState.email.isBlank()) null else uiState.email,
                description = if (uiState.description.isBlank()) null else uiState.description
            )

            when (val result = repository.createCampusContact(newContact)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                is ApiResult.Error -> {
                    handleError(result.message)
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()
        
        if (uiState.departmentName.isBlank()) {
            errors["DepartmentName"] = "El nombre del departamento es obligatorio"
        } else if (uiState.departmentName.length > 100) {
            errors["DepartmentName"] = "Máximo 100 caracteres"
        }

        if (uiState.phone.isBlank()) {
            errors["Phone"] = "El teléfono es obligatorio"
        } else if (uiState.phone.length < 7) {
            errors["Phone"] = "Mínimo 7 caracteres"
        } else if (uiState.phone.length > 25) {
            errors["Phone"] = "Máximo 25 caracteres"
        }

        if (uiState.email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            errors["Email"] = "Formato de correo inválido"
        }

        if (uiState.description.length > 500) {
            errors["Description"] = "Máximo 500 caracteres"
        }

        uiState = uiState.copy(fieldErrors = errors)
        return errors.isEmpty()
    }

    private fun handleError(message: String?) {
        val fallback = "Error al procesar la solicitud. Intente nuevamente."
        if (message.isNullOrBlank()) {
            uiState = uiState.copy(isLoading = false, error = fallback)
            return
        }

        try {
            val json = JSONObject(message)
            val title = json.optString("title").takeIf { it.isNotBlank() } ?: fallback
            val errorsJson = json.optJSONObject("errors")
            
            if (errorsJson != null) {
                val fieldErrors = mutableMapOf<String, String>()
                val keys = errorsJson.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val errorArray = errorsJson.getJSONArray(key)
                    if (errorArray.length() > 0) {
                        val firstError = errorArray.getString(0)
                        if (firstError.isNotBlank()) {
                            fieldErrors[key] = firstError
                        }
                    }
                }
                
                uiState = uiState.copy(
                    isLoading = false, 
                    fieldErrors = fieldErrors,
                    error = if (fieldErrors.isEmpty()) title else "Por favor, revise los errores en los campos marcados"
                )
            } else {
                val detail = json.optString("detail").takeIf { it.isNotBlank() } ?: title
                uiState = uiState.copy(isLoading = false, error = detail)
            }
        } catch (e: Exception) {
            val displayMsg = if (message.length > 150) message.take(150) + "..." else message
            uiState = uiState.copy(isLoading = false, error = displayMsg)
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateCampusContactViewModel() as T
        }
    }
}
