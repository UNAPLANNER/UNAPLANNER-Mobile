package com.moviles.unaplanner.ui.screens.admin.profile.studyPlanAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CreateStudyPlanCourseRequest
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch
import org.json.JSONObject

data class CreateStudyPlanCourseUiState(
    val code: String = "",
    val name: String = "",
    val credits: String = "",
    val theoryHours: String = "",
    val practiceHours: String = "",
    val labHours: String = "",
    val level: String = "1",
    val term: String = "1",
    val electiveType: String = "Obligatorio",
    val isActive: Boolean = true,
    val selectedPrerequisiteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class CreateStudyPlanCourseViewModel(
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    val levelOptions = listOf("1", "2", "3", "4")
    val termOptions = listOf("1", "2")
    val electiveTypeOptions = listOf("Obligatorio", "OptativoDisciplinario", "OptativoLibre")

    var uiState by mutableStateOf(CreateStudyPlanCourseUiState())
        private set

    var createdStudyPlan by mutableStateOf<StudyPlanDetail?>(null)
        private set

    fun onCodeChange(value: String) {
        uiState = uiState.copy(code = value.uppercase(), fieldErrors = uiState.fieldErrors - "Code")
    }

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value, fieldErrors = uiState.fieldErrors - "Name")
    }

    fun onCreditsChange(value: String) {
        uiState = uiState.copy(credits = digits(value), fieldErrors = uiState.fieldErrors - "Credits")
    }

    fun onTheoryHoursChange(value: String) {
        uiState = uiState.copy(theoryHours = digits(value), fieldErrors = uiState.fieldErrors - "TheoryHours")
    }

    fun onPracticeHoursChange(value: String) {
        uiState = uiState.copy(practiceHours = digits(value), fieldErrors = uiState.fieldErrors - "PracticeHours")
    }

    fun onLabHoursChange(value: String) {
        uiState = uiState.copy(labHours = digits(value), fieldErrors = uiState.fieldErrors - "LabHours")
    }

    fun onLevelChange(value: String) {
        uiState = uiState.copy(level = value, fieldErrors = uiState.fieldErrors - "Level")
    }

    fun onTermChange(value: String) {
        uiState = uiState.copy(term = value, fieldErrors = uiState.fieldErrors - "Term")
    }

    fun onElectiveTypeChange(value: String) {
        uiState = uiState.copy(electiveType = value, fieldErrors = uiState.fieldErrors - "ElectiveType")
    }

    fun onActiveChange(value: Boolean) {
        uiState = uiState.copy(isActive = value)
    }

    fun onPrerequisiteToggle(courseId: Int) {
        val selected = uiState.selectedPrerequisiteIds
        uiState = uiState.copy(
            selectedPrerequisiteIds = if (courseId in selected) selected - courseId else selected + courseId
        )
    }

    fun createCourse(studyPlanId: Int) {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, fieldErrors = emptyMap())
            createdStudyPlan = null

            val request = CreateStudyPlanCourseRequest(
                code = uiState.code.trim(),
                name = uiState.name.trim(),
                credits = uiState.credits.toInt(),
                theoryHours = uiState.theoryHours.toInt(),
                practiceHours = uiState.practiceHours.toInt(),
                labHours = uiState.labHours.toInt(),
                level = uiState.level.toInt(),
                term = uiState.term.toInt(),
                isElective = uiState.electiveType != "Obligatorio",
                electiveType = uiState.electiveType,
                isStatus = uiState.isActive,
                prerequisiteCourseIds = uiState.selectedPrerequisiteIds.toList()
            )

            when (val result = repository.createStudyPlanCourse(studyPlanId, request)) {
                is ApiResult.Success -> {
                    createdStudyPlan = result.data
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                is ApiResult.Error -> handleError(result.message)
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    fun reset() {
        uiState = CreateStudyPlanCourseUiState()
        createdStudyPlan = null
    }

    private fun validateFields(): Boolean {
        val errors = mutableMapOf<String, String>()
        val credits = uiState.credits.toIntOrNull()
        val theoryHours = uiState.theoryHours.toIntOrNull()
        val practiceHours = uiState.practiceHours.toIntOrNull()
        val labHours = uiState.labHours.toIntOrNull()

        if (uiState.code.isBlank()) errors["Code"] = "El codigo es obligatorio"
        if (uiState.name.isBlank()) errors["Name"] = "El nombre es obligatorio"

        if (credits == null) {
            errors["Credits"] = "Los creditos son obligatorios"
        } else if (credits !in 1..20) {
            errors["Credits"] = "Los creditos deben estar entre 1 y 20"
        }

        if (theoryHours == null) errors["TheoryHours"] = "Las horas teoria son obligatorias"
        if (practiceHours == null) errors["PracticeHours"] = "Las horas practica son obligatorias"
        if (labHours == null) errors["LabHours"] = "Las horas laboratorio son obligatorias"

        uiState = uiState.copy(fieldErrors = errors)
        return errors.isEmpty()
    }

    private fun handleError(message: String?) {
        val fallback = "No se pudo crear el curso"
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
                    if (errorArray.length() > 0) fieldErrors[toUiFieldKey(key)] = errorArray.getString(0)
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
            "Code" -> "Code"
            "Name" -> "Name"
            "Credits" -> "Credits"
            "TheoryHours" -> "TheoryHours"
            "PracticeHours" -> "PracticeHours"
            "LabHours" -> "LabHours"
            "Level" -> "Level"
            "Term" -> "Term"
            "ElectiveType" -> "ElectiveType"
            else -> apiFieldKey
        }
    }

    private fun digits(value: String): String = value.filter { it.isDigit() }.take(2)
}
