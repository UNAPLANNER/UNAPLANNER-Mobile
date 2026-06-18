package com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.data.remote.model.UpdateCareerRequest
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch
import org.json.JSONObject

data class EditCareerUiState(
    val name: String = "",
    val code: String = "",
    val description: String = "",
    val totalCredits: String = "",
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class EditCareerViewModel(
    private val career: Career,
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(career.toUiState())
        private set

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value, fieldErrors = uiState.fieldErrors - "Name")
    }

    fun onCodeChange(value: String) {
        uiState = uiState.copy(code = value.uppercase(), fieldErrors = uiState.fieldErrors - "Code")
    }

    fun onDescriptionChange(value: String) {
        uiState = uiState.copy(description = value, fieldErrors = uiState.fieldErrors - "Description")
    }

    fun onTotalCreditsChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(3)
        uiState = uiState.copy(totalCredits = digitsOnly, fieldErrors = uiState.fieldErrors - "TotalCredits")
    }

    fun onActiveChange(value: Boolean) {
        uiState = uiState.copy(isActive = value)
    }

    fun updateCareer() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, fieldErrors = emptyMap())

            val request = UpdateCareerRequest(
                name = uiState.name.trim(),
                code = uiState.code.trim(),
                description = uiState.description.trim().ifBlank { null },
                totalCredits = uiState.totalCredits.toInt(),
                isStatus = uiState.isActive
            )

            when (val result = repository.updateCareer(career.id, request)) {
                is ApiResult.Success -> uiState = uiState.copy(isLoading = false, isSuccess = true)
                is ApiResult.Error -> handleError(result.message)
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun clearSuccess() {
        uiState = uiState.copy(isSuccess = false)
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()
        val credits = uiState.totalCredits.toIntOrNull()

        if (uiState.name.isBlank()) {
            errors["Name"] = "El nombre de la carrera es obligatorio"
        } else if (uiState.name.length > 150) {
            errors["Name"] = "El nombre no puede exceder 150 caracteres"
        }

        if (uiState.code.isBlank()) {
            errors["Code"] = "El codigo de la carrera es obligatorio"
        } else if (uiState.code.length > 20) {
            errors["Code"] = "El codigo no puede exceder 20 caracteres"
        }

        if (uiState.description.length > 500) {
            errors["Description"] = "La descripcion no puede exceder 500 caracteres"
        }

        if (credits == null) {
            errors["TotalCredits"] = "Los creditos son obligatorios"
        } else if (credits !in 1..500) {
            errors["TotalCredits"] = "Los creditos deben estar entre 1 y 500"
        }

        uiState = uiState.copy(fieldErrors = errors)
        return errors.isEmpty()
    }

    private fun handleError(message: String?) {
        val fallback = "No se pudo actualizar la carrera"
        if (message.isNullOrBlank()) {
            uiState = uiState.copy(isLoading = false, error = fallback)
            return
        }

        try {
            val json = JSONObject(message)
            val errorsJson = json.optJSONObject("errors")
            if (errorsJson != null) {
                val fieldErrors = mutableMapOf<String, String>()
                val keys = errorsJson.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val errorArray = errorsJson.getJSONArray(key)
                    if (errorArray.length() > 0) {
                        fieldErrors[key] = errorArray.getString(0)
                    }
                }

                uiState = uiState.copy(
                    isLoading = false,
                    fieldErrors = fieldErrors,
                    error = if (fieldErrors.isEmpty()) fallback else "Revisa los campos indicados"
                )
                return
            }

            val detail = json.optString("message")
                .takeIf { it.isNotBlank() }
                ?: json.optString("detail").takeIf { it.isNotBlank() }
                ?: json.optString("title").takeIf { it.isNotBlank() }
                ?: fallback

            uiState = uiState.copy(isLoading = false, error = detail)
        } catch (e: Exception) {
            uiState = uiState.copy(isLoading = false, error = message.take(180))
        }
    }

    private fun Career.toUiState(): EditCareerUiState {
        return EditCareerUiState(
            name = name,
            code = code,
            description = description.orEmpty(),
            totalCredits = totalCredits.toString(),
            isActive = isStatus != false
        )
    }

    class Factory(private val career: Career) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditCareerViewModel(career) as T
        }
    }
}
