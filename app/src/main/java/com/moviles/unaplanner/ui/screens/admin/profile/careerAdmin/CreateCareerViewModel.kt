package com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CreateCareerRequest
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import java.time.Year
import kotlinx.coroutines.launch
import org.json.JSONObject

data class CreateCareerUiState(
    val name: String = "",
    val degree: String = "Bachillerato",
    val planYear: String = "",
    val school: String = "",
    val bachelorCredits: String = "",
    val diplomaCredits: String = "",
    val degreeCredits: String = "",
    val officialResolution: String = "",
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class CreateCareerViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    val degreeOptions = listOf("Diplomado", "Bachillerato", "Licenciatura", "Maestría", "Doctorado")

    var uiState by mutableStateOf(CreateCareerUiState())
        private set

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value, fieldErrors = uiState.fieldErrors - "Name")
    }

    fun onDegreeChange(value: String) {
        uiState = uiState.copy(
            degree = value,
            fieldErrors = uiState.fieldErrors - "Degree" - "BachelorCredits" - "DiplomaCredits" - "DegreeCredits"
        )
    }

    fun onActiveChange(value: Boolean) {
        uiState = uiState.copy(isActive = value)
    }

    fun onPlanYearChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(4)
        uiState = uiState.copy(planYear = digitsOnly, fieldErrors = uiState.fieldErrors - "PlanYear")
    }

    fun onSchoolChange(value: String) {
        uiState = uiState.copy(school = value, fieldErrors = uiState.fieldErrors - "School")
    }

    fun onBachelorCreditsChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(3)
        uiState = uiState.copy(bachelorCredits = digitsOnly, fieldErrors = uiState.fieldErrors - "BachelorCredits")
    }

    fun onDiplomaCreditsChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(3)
        uiState = uiState.copy(diplomaCredits = digitsOnly, fieldErrors = uiState.fieldErrors - "DiplomaCredits")
    }

    fun onDegreeCreditsChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(3)
        uiState = uiState.copy(degreeCredits = digitsOnly, fieldErrors = uiState.fieldErrors - "DegreeCredits")
    }

    fun onOfficialResolutionChange(value: String) {
        uiState = uiState.copy(
            officialResolution = value.uppercase(),
            fieldErrors = uiState.fieldErrors - "OfficialResolution"
        )
    }

    fun createCareer() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, fieldErrors = emptyMap())

            val request = CreateCareerRequest(
                name = uiState.name.trim(),
                degreeLevel = uiState.degree.trim(),
                planYear = uiState.planYear.toInt(),
                school = uiState.school.trim(),
                bachelorCredits = if (requiresSpecificDegreeCredits(uiState.degree)) null else uiState.bachelorCredits.toInt(),
                diplomaCredits = if (requiresSpecificDegreeCredits(uiState.degree)) null else uiState.diplomaCredits.toInt(),
                degreeCredits = if (requiresSpecificDegreeCredits(uiState.degree)) uiState.degreeCredits.toInt() else null,
                officialResolution = uiState.officialResolution.trim(),
                isStatus = uiState.isActive
            )

            when (val result = repository.createCareer(request)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                is ApiResult.Error -> handleError(result.message)
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun resetForm() {
        uiState = CreateCareerUiState()
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()
        val bachelorCredits = uiState.bachelorCredits.toIntOrNull()
        val diplomaCredits = uiState.diplomaCredits.toIntOrNull()
        val degreeCredits = uiState.degreeCredits.toIntOrNull()
        val planYear = uiState.planYear.toIntOrNull()
        val currentYear = Year.now().value

        if (uiState.name.isBlank()) {
            errors["Name"] = "El nombre de la carrera es obligatorio"
        } else if (uiState.name.length > 150) {
            errors["Name"] = "El nombre no puede exceder 150 caracteres"
        }

        if (uiState.degree.isBlank()) {
            errors["Degree"] = "El grado es obligatorio"
        }

        if (planYear == null) {
            errors["PlanYear"] = "El año del plan es obligatorio"
        } else if (planYear !in 1900..currentYear) {
            errors["PlanYear"] = "El año no puede ser mayor al actual"
        }

        if (uiState.school.isBlank()) {
            errors["School"] = "La escuela es obligatoria"
        } else if (uiState.school.length > 150) {
            errors["School"] = "La escuela no puede exceder 150 caracteres"
        }

        if (requiresSpecificDegreeCredits(uiState.degree)) {
            if (degreeCredits == null) {
                errors["DegreeCredits"] = "Los creditos de ${uiState.degree.lowercase()} son obligatorios"
            } else if (degreeCredits !in 1..500) {
                errors["DegreeCredits"] = "Los creditos deben estar entre 1 y 500"
            }
        } else {
            if (bachelorCredits == null) {
                errors["BachelorCredits"] = "Los creditos de bachillerato son obligatorios"
            } else if (bachelorCredits !in 1..500) {
                errors["BachelorCredits"] = "Los creditos deben estar entre 1 y 500"
            }

            if (diplomaCredits == null) {
                errors["DiplomaCredits"] = "Los creditos de diplomado son obligatorios"
            } else if (diplomaCredits !in 1..500) {
                errors["DiplomaCredits"] = "Los creditos deben estar entre 1 y 500"
            }
        }

        if (uiState.officialResolution.isBlank()) {
            errors["OfficialResolution"] = "La resolucion oficial es obligatoria"
        } else if (uiState.officialResolution.length > 20) {
            errors["OfficialResolution"] = "La resolucion no puede exceder 20 caracteres"
        }

        uiState = uiState.copy(fieldErrors = errors)
        return errors.isEmpty()
    }

    private fun requiresSpecificDegreeCredits(degree: String): Boolean {
        return degree in setOf("Licenciatura", "Maestría", "Doctorado")
    }

    private fun handleError(message: String?) {
        val fallback = "No se pudo crear la carrera"
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
                        fieldErrors[toUiFieldKey(key)] = errorArray.getString(0)
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

    private fun toUiFieldKey(apiFieldKey: String): String {
        return when (apiFieldKey) {
            "Code" -> "OfficialResolution"
            "OfficialResolution" -> "OfficialResolution"
            "DegreeLevel" -> "Degree"
            "PlanYear" -> "PlanYear"
            "School" -> "School"
            "BachelorCredits" -> "BachelorCredits"
            "DiplomaCredits" -> "DiplomaCredits"
            "DegreeCredits" -> "DegreeCredits"
            "TotalCredits" -> if (requiresSpecificDegreeCredits(uiState.degree)) "DegreeCredits" else "BachelorCredits"
            else -> apiFieldKey
        }
    }

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateCareerViewModel() as T
        }
    }
}
