package com.moviles.unaplanner.ui.screens.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.core.UserMessages.Errors.GENERIC_ERROR
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.CampusCareerDto
import com.moviles.unaplanner.data.remote.model.CampusDto
import com.moviles.unaplanner.data.remote.model.RegisterRequest
import com.moviles.unaplanner.data.remote.model.StudyPlan
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.RegisterUserStudentRepository
import kotlinx.coroutines.launch
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
    var selectedCampusId by mutableIntStateOf(0)

    var major by mutableStateOf("Seleccione Carrera Principal")
    var selectedMajorId by mutableIntStateOf(0)
    var selectedCampusCareer by mutableStateOf<CampusCareerDto?>(null)

    var entryYear by mutableStateOf("")
    var currentCycle by mutableStateOf("I Ciclo 2026")

    var showBottomSheet by mutableStateOf(false)
    var currentSelectionType by mutableStateOf(SelectionType.NONE)

    var selectedPlan by mutableStateOf<StudyPlan?>(null)
    var studyPlanSelectedName by mutableStateOf("Seleccione un Plan de Estudio")

    private val _uiState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _campuses = MutableStateFlow<List<CampusDto>>(emptyList())
    val campuses = _campuses.asStateFlow()

    private val _campusCareers = MutableStateFlow<List<CampusCareerDto>>(emptyList())
    val campusCareers = _campusCareers.asStateFlow()

    val availableStudyPlans: List<StudyPlan>
        get() = selectedCampusCareer?.studyPlans ?: emptyList()

    init {
        loadCampuses()
    }

    fun loadCampuses() {
        viewModelScope.launch {
            _uiState.value = RegisterState.Loading
            try {
                val response = RetrofitClient.apiService.getCampuses()
                if (response.isSuccessful) {
                    _campuses.value = response.body() ?: emptyList()
                    _uiState.value = RegisterState.Idle
                } else {
                    val errorMsg = "Error al cargar campus: ${response.code()}"
                    Log.e("RegisterVM", errorMsg)
                    _uiState.value = RegisterState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Error de conexión al cargar campus"
                Log.e("RegisterVM", "Excepción al cargar campus", e)
                _uiState.value = RegisterState.Error(errorMsg)
            }
        }
    }

    private fun loadCampusCareers(campusId: Int) {
        _campusCareers.value = emptyList()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCampusCareers(campusId)
                when {
                    response.isSuccessful -> {
                        _campusCareers.value = response.body() ?: emptyList()
                    }
                    response.code() == 404 -> {
                        _uiState.value = RegisterState.Error(
                            "Este campus no tiene carreras disponibles."
                        )
                    }
                    else -> {
                        _uiState.value = RegisterState.Error(
                            "Error al cargar carreras (${response.code()})"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("RegisterVM", "Excepción al cargar carreras", e)
                _uiState.value = RegisterState.Error(
                    e.localizedMessage ?: "Error al cargar carreras"
                )
            }
        }
    }

    fun onItemSelected(item: Any) {
        when (currentSelectionType) {
            SelectionType.CAMPUS -> if (item is CampusDto) {
                campus = item.name
                selectedCampusId = item.id
                // Reset career and plan when campus changes
                major = "Seleccione Carrera Principal"
                selectedMajorId = 0
                selectedCampusCareer = null
                selectedPlan = null
                studyPlanSelectedName = "Seleccione un Plan de Estudio"
                loadCampusCareers(item.id)
            }

            SelectionType.MAJOR -> if (item is CampusCareerDto) {
                major = item.name
                selectedMajorId = item.careerId
                selectedCampusCareer = item
                // Auto-select if exactly one plan
                val plans = item.studyPlans
                if (plans.size == 1) {
                    selectedPlan = plans.first()
                    studyPlanSelectedName = plans.first().name
                } else {
                    selectedPlan = null
                    studyPlanSelectedName = "Seleccione un Plan"
                }
            }

            SelectionType.DOUBLE_MAJOR -> if (item is CampusCareerDto) {
                // reserved for future use
            }

            SelectionType.STUDY_PLAN -> if (item is StudyPlan) {
                selectedPlan = item
                studyPlanSelectedName = item.name
            }

            SelectionType.NONE -> {}
        }
        showBottomSheet = false
    }

    fun onRegisterClicked(onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        val error = RegisterFormUtils.validate(
            name = name.trim(),
            email = trimmedEmail,
            password = password,
            confirmPassword = confirmPassword,
            campus = campus,
            selectedMajorId = selectedMajorId,
            selectedPlan = selectedPlan,
            entryYear = entryYear.trim()
        )

        if (error != null) {
            _uiState.value = RegisterState.Error(error)
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterState.Loading
            try {
                val request = RegisterRequest(
                    email = trimmedEmail,
                    password = password,
                    fullName = name.trim(),
                    enterYear = entryYear.trim().toInt(),
                    careerId = selectedMajorId,
                    studyPlanId = selectedPlan!!.studyPlanId,
                    currentCycle = currentCycle
                )

                val repository = RegisterUserStudentRepository()
                val result = repository.registerUser(request)

                when (result) {
                    is ApiResult.Success -> {
                        _uiState.value = RegisterState.Success
                        onSuccess()
                    }
                    is ApiResult.Error -> {
                        _uiState.value = RegisterState.Error(
                            RegisterFormUtils.mapError(result.message)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RegisterState.Error(e.localizedMessage ?: GENERIC_ERROR)
            }
        }
    }
}
