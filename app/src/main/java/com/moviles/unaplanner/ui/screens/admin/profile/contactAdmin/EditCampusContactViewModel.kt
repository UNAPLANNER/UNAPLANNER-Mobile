package com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * UI State for the Edit Campus Contact screen.
 */
data class EditContactUiState(
    val id: Int = 0,
    val departmentName: String = "",
    val phone: String = "",
    val email: String = "",
    val description: String = "",
    val campusId: Int = 0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class EditCampusContactViewModel(
    private val contactId: Int,
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EditContactUiState())
        private set

    init {
        loadContactDetails()
    }

    /**
     * Loads the initial contact details to pre-populate the form.
     */
    private fun loadContactDetails() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            // Note: We use the existing getCampusContact logic
            // Since AdminRepository doesn't have a single GET, we might need to add it or use common repository
            // For now, assuming we can get it or it's passed.
            // Actually, let's add getCampusContact to AdminRepository or use ContactApiService directly.
            
            // For this implementation, we'll fetch it from the repository
            when (val result = repository.getCampusContacts()) { // Mocking fetch for now or adding to repo
                is ApiResult.Success -> {
                    val contact = result.data.find { it.id == contactId }
                    if (contact != null) {
                        uiState = uiState.copy(
                            id = contact.id,
                            departmentName = contact.departmentName ?: "",
                            phone = contact.phone ?: "",
                            email = contact.email ?: "",
                            description = contact.description ?: "",
                            campusId = contact.campusId,
                            isLoading = false
                        )
                    } else {
                        uiState = uiState.copy(isLoading = false, error = "Contacto no encontrado")
                    }
                }
                is ApiResult.Error -> {
                    uiState = uiState.copy(isLoading = false, error = result.message)
                }
            }
        }
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

    /**
     * Performs the update operation.
     */
    fun updateContact() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, fieldErrors = emptyMap())
            
            val updatedContact = CampusContact(
                id = uiState.id,
                campusId = uiState.campusId,
                departmentName = uiState.departmentName,
                phone = uiState.phone,
                email = if (uiState.email.isBlank()) null else uiState.email,
                description = if (uiState.description.isBlank()) null else uiState.description
            )

            when (val result = repository.updateCampusContact(uiState.id, updatedContact)) {
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
            errors["DepartmentName"] = UserMessages.CampusContacts.Errors.DEPT_NAME_REQUIRED
        } else if (uiState.departmentName.length > 100) {
            errors["DepartmentName"] = UserMessages.CampusContacts.Errors.MAX_CHARS_100
        }

        if (uiState.phone.isBlank()) {
            errors["Phone"] = UserMessages.CampusContacts.Errors.PHONE_REQUIRED
        } else if (uiState.phone.length < 7) {
            errors["Phone"] = UserMessages.CampusContacts.Errors.MIN_CHARS_7
        } else if (uiState.phone.length > 25) {
            errors["Phone"] = UserMessages.CampusContacts.Errors.MAX_CHARS_25
        }

        if (uiState.email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) {
            errors["Email"] = UserMessages.CampusContacts.Errors.INVALID_EMAIL
        }

        if (uiState.description.length > 500) {
            errors["Description"] = UserMessages.CampusContacts.Errors.MAX_CHARS_500
        }

        uiState = uiState.copy(fieldErrors = errors)
        return errors.isEmpty()
    }

    private fun handleError(message: String?) {
        val fallback = UserMessages.CampusContacts.Errors.GENERAL_ERROR
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
                    error = if (fieldErrors.isEmpty()) title else UserMessages.CampusContacts.Errors.CHECK_FIELDS
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

    class Factory(private val contactId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditCampusContactViewModel(contactId) as T
        }
    }
}
