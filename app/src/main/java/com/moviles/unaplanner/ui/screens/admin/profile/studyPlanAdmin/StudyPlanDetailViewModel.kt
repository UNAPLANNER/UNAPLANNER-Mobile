package com.moviles.unaplanner.ui.screens.admin.profile.studyPlanAdmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import kotlinx.coroutines.launch

data class StudyPlanDetailUiState(
    val studyPlan: StudyPlanDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StudyPlanDetailViewModel(
    private val career: Career,
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {

    var uiState by mutableStateOf(StudyPlanDetailUiState())
        private set

    init {
        if (career.currentStudyPlanId == null) {
            uiState = uiState.copy(studyPlan = career.toEmptyStudyPlanDetail())
        } else {
            loadStudyPlan()
        }
    }

    fun loadStudyPlan() {
        val studyPlanId = career.currentStudyPlanId
        if (studyPlanId == null) {
            uiState = uiState.copy(studyPlan = career.toEmptyStudyPlanDetail(), isLoading = false, error = null)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            when (val result = repository.getStudyPlanDetail(studyPlanId)) {
                is ApiResult.Success -> {
                    uiState = uiState.copy(studyPlan = result.data, isLoading = false)
                }
                is ApiResult.Error -> {
                    uiState = uiState.copy(
                        studyPlan = career.toEmptyStudyPlanDetail(),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    private fun Career.toEmptyStudyPlanDetail(): StudyPlanDetail {
        return StudyPlanDetail(
            id = 0,
            name = currentStudyPlanName ?: "Plan pendiente",
            code = currentStudyPlanYear?.let { "Plan $it" } ?: "Sin codigo",
            careerId = id,
            careerName = name,
            careerCode = code,
            effectiveYear = currentStudyPlanYear ?: 0,
            totalCredits = totalCredits,
            courseCount = courseCount ?: 0,
            levelCount = levelCount ?: 0,
            cycleCount = 0,
            levels = emptyList()
        )
    }

    class Factory(private val career: Career) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudyPlanDetailViewModel(career) as T
        }
    }
}
