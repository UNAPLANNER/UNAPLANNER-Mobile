package com.moviles.unaplanner.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.core.UserMessages.Errors.GENERIC_ERROR
import com.moviles.unaplanner.data.remote.model.RegisterRequest
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.RegisterUserStudentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class StudyPlan(val id: Int, val name: String, val careerId: Int)
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
    // Datos personales
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // Datos académicos
    var campus by mutableStateOf("Campus Sarapiquí")
    var major by mutableStateOf("Ingeniería en Sistemas de Información")
    var secondMajor by mutableStateOf("")
    var entryYear by mutableStateOf("")
    var currentCycle by mutableStateOf("I Ciclo 2026")

    // Control de UI (Selectores)
    var showBottomSheet by mutableStateOf(false)
    var currentSelectionType by mutableStateOf(SelectionType.NONE)

    // Datos del Plan de Estudios
    var studyPlanId by mutableIntStateOf(1)
    var studyPlanSelectedName by mutableStateOf("Seleccione un Plan")
    var selectedPlan by mutableStateOf<StudyPlan?>(null)


    // Estado de la API
    var uiState by mutableStateOf<RegisterState>(RegisterState.Idle)
        private set

    // Listas de datos
    val campusList = listOf("Campus Sarapiquí", "Campus Liberia", "Campus Nicoya", "Sede Central")
    val majorsList = listOf("Ingeniería en Sistemas de Información", "Ingeniería en Ciencia de Datos", "Administración", "Administración de Oficinas")
    val studyPlansList = listOf(
        StudyPlan(1, "Plan Ingeniería 2024", 101),
        StudyPlan(2, "Plan Administración 2022", 102)
    )

    fun onItemSelected(item: Any) {
        when (currentSelectionType) {
            SelectionType.CAMPUS -> if (item is String) campus = item
            SelectionType.MAJOR -> if (item is String) major = item
            SelectionType.DOUBLE_MAJOR -> if (item is String) secondMajor = item
            SelectionType.STUDY_PLAN -> if (item is StudyPlan) {
                selectedPlan = item
                studyPlanSelectedName = item.name
                studyPlanId = item.id
            }
            SelectionType.NONE -> {}
        }
        showBottomSheet = false
    }

    fun onRegisterClicked(onSuccess: () -> Unit) {
        if (password != confirmPassword) {
            uiState = RegisterState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            uiState = RegisterState.Loading
            try {
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    campus = campus,
                    major = major,
                    secondMajor = secondMajor.ifBlank { null },
                    studyPlanId = studyPlanId,
                    entryYear = entryYear.toIntOrNull() ?: 2026,
                    currentCycle = currentCycle
                )

                val repository = RegisterUserStudentRepository()
                val result = repository.registerUser(request)

                delay(1500)

                when (result) {
                    is ApiResult.Success -> {
                        uiState = RegisterState.Success
                        onSuccess()
                    }
                    is ApiResult.Error -> {
                        uiState = RegisterState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                uiState = RegisterState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}

