package com.moviles.unaplanner.ui.screens.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.core.UserMessages.Errors.GENERIC_ERROR
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.INVALID_ENTRY_YEAR
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.STUDY_PLAN_REQUIRED
import com.moviles.unaplanner.data.remote.model.RegisterRequest
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.RegisterUserStudentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.moviles.unaplanner.data.remote.model.StudyPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SelectionType {
    CAMPUS,
    MAJOR,
    DOUBLE_MAJOR,
    STUDY_PLAN,
    NONE
}
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
class RegisterViewModel : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var campus by mutableStateOf("Seleccione un Campus")
    var major by mutableStateOf("Seleccione Carrera Principal")
    var secondMajor by mutableStateOf("Seleccione Carrera Opcional")
    var entryYear by mutableStateOf("")
    var currentCycle by mutableStateOf("I Ciclo 2026")

    var showBottomSheet by mutableStateOf(false)
    var currentSelectionType by mutableStateOf(SelectionType.NONE)

    var selectedPlan by mutableStateOf<StudyPlan?>(null)
    var studyPlanSelectedName by mutableStateOf("Seleccione un Plan")

    private val _uiState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val uiState = _uiState.asStateFlow()

    val campusList = listOf(
        "Campus Sarapiquí",
        "Campus Liberia",
        "Campus Nicoya",
        "Sede Central"
    )

    val majorsList = listOf(
        "Ingeniería en Sistemas de Información",
        "Ingeniería en Ciencia de Datos",
        "Administración",
        "Administración de Oficinas"
    )

    val studyPlansList = listOf(
        StudyPlan(
            studyPlanId = 1,
            careerId = 1,
            name = "Bachillerato en Ingeniería en Sistemas de Información"
        )
    )

    fun onItemSelected(item: Any) {

        when (currentSelectionType) {

            SelectionType.CAMPUS -> if (item is String) campus = item

            SelectionType.MAJOR -> if (item is String) major = item

            SelectionType.DOUBLE_MAJOR -> if (item is String) secondMajor = item

            SelectionType.STUDY_PLAN -> if (item is StudyPlan) {
                selectedPlan = item
                studyPlanSelectedName = item.name
            }

            SelectionType.NONE -> {}
        }

        showBottomSheet = false
    }

    fun onRegisterClicked(onSuccess: () -> Unit) {

        if (password != confirmPassword) {
            _uiState.value = RegisterState.Error(UserMessages.Errors.PASSWORDS_DO_NOT_MATCH)
            return
        }

        viewModelScope.launch {

            _uiState.value = RegisterState.Loading

            try {

                val plan = selectedPlan ?: run {
                    _uiState.value = RegisterState.Error(STUDY_PLAN_REQUIRED)
                    return@launch
                }

                val year = entryYear.toIntOrNull()
                if (year == null) {
                    _uiState.value = RegisterState.Error(INVALID_ENTRY_YEAR)
                    return@launch
                }

                val request = RegisterRequest(
                    email = email,
                    password = password,
                    fullName = name,
                    enterYear = year,
                    careerId = plan.careerId,
                    studyPlanId = plan.studyPlanId
                )

                val repository = RegisterUserStudentRepository()
                val result = repository.registerUser(request)

                when (result) {

                    is ApiResult.Success -> {
                        _uiState.value = RegisterState.Success
                        onSuccess()
                    }

                    is ApiResult.Error -> {
                        _uiState.value = RegisterState.Error(result.message)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = RegisterState.Error(
                    e.localizedMessage ?: GENERIC_ERROR
                )
            }
        }
    }
}