package com.moviles.unaplanner.ui.screens.malla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.*
import com.moviles.unaplanner.data.repository.CurriculumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CurriculumUiState {
    object Idle : CurriculumUiState()
    object Loading : CurriculumUiState()
    data class Success(val levels: List<CurriculumLevelDto>) : CurriculumUiState()
    data class Error(val message: String) : CurriculumUiState()
}

sealed class StudentCoursesUiState {
    object Idle : StudentCoursesUiState()
    object Loading : StudentCoursesUiState()
    data class Success(val courses: List<StudentCourseProgressDto>) : StudentCoursesUiState()
    data class Error(val message: String) : StudentCoursesUiState()
}

class MallaViewModel(private val repository: CurriculumRepository) : ViewModel() {

    private val _careerName = MutableStateFlow<String?>(null)
    val careerName: StateFlow<String?> = _careerName

    private val _curriculumState = MutableStateFlow<CurriculumUiState>(CurriculumUiState.Idle)
    val curriculumState: StateFlow<CurriculumUiState> = _curriculumState

    private val _studentCoursesState = MutableStateFlow<StudentCoursesUiState>(StudentCoursesUiState.Idle)
    val studentCoursesState: StateFlow<StudentCoursesUiState> = _studentCoursesState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadStudentCurriculum(userId: Int) {
        if (_curriculumState.value is CurriculumUiState.Success) return
        viewModelScope.launch {
            _curriculumState.value = CurriculumUiState.Loading

            // Step 1: get careers list
            val careers = repository.getCareers().getOrNull()
            if (careers.isNullOrEmpty()) {
                _curriculumState.value = CurriculumUiState.Error("No se pudo conectar al servidor")
                return@launch
            }

            // Step 2: resolve which career belongs to this student
            val careerId = resolveStudentCareerId(userId, careers)
            if (careerId == null) {
                _curriculumState.value = CurriculumUiState.Error(
                    "No se pudo determinar la carrera del estudiante. Contacte al administrador."
                )
                return@launch
            }

            val career = careers.find { it.id == careerId } ?: run {
                _curriculumState.value = CurriculumUiState.Error("La carrera asignada no existe en el catálogo")
                return@launch
            }

            _careerName.value = career.name

            // Step 3a: get FULL curriculum without userId → guarantees ALL courses (all as Pendiente)
            val fullLevels = repository.getCareerCurriculum(career.id, null).getOrNull()
            if (fullLevels.isNullOrEmpty()) {
                _curriculumState.value = CurriculumUiState.Error("La malla no tiene cursos registrados")
                return@launch
            }

            // Step 3b: get student's actual progress
            val studentProgress = repository.getStudentCurriculumCourses(userId).getOrNull()
            val progressMap = studentProgress?.associateBy { it.courseId } ?: emptyMap()

            // Step 4: merge — apply real status/grade over the complete curriculum
            val mergedLevels = fullLevels.map { level ->
                level.copy(
                    semesters = level.semesters.map { semester ->
                        semester.copy(
                            courses = semester.courses.map { course ->
                                val progress = progressMap[course.id]
                                if (progress != null) {
                                    course.copy(
                                        status = progress.status,
                                        finalGrade = progress.finalGrade
                                    )
                                } else {
                                    course // keeps status = "Pendiente"
                                }
                            }
                        )
                    }
                )
            }

            _curriculumState.value = CurriculumUiState.Success(mergedLevels)
        }
    }

    /**
     * Tries 3 sources in order to find the student's careerId:
     * 1. careerId from login session
     * 2. careerId from student profile endpoint
     * 3. Cross-reference: find which career's curriculum contains the student's enrolled courses
     */
    private suspend fun resolveStudentCareerId(userId: Int, careers: List<CareerDto>): Int? {
        // Source 1: from login response
        val fromSession = AuthSession.currentUser?.careerId
        if (fromSession != null) return fromSession

        // Source 2: from student profile endpoint
        val fromProfile = repository.getStudentProfile(userId).getOrNull()?.careerId
        if (fromProfile != null) return fromProfile

        // Source 3: cross-reference student courses with each career's curriculum
        val studentCourses = repository.getStudentCurriculumCourses(userId).getOrNull()
        if (!studentCourses.isNullOrEmpty()) {
            val enrolledCourseId = studentCourses.first().courseId
            for (career in careers) {
                val levels = repository.getCareerCurriculum(career.id, null).getOrNull() ?: continue
                val courseIds = levels
                    .flatMap { it.semesters }
                    .flatMap { it.courses }
                    .map { it.id }
                    .toSet()
                if (enrolledCourseId in courseIds) return career.id
            }
        }

        return null
    }

    fun reloadCurriculum(userId: Int) {
        _curriculumState.value = CurriculumUiState.Idle
        loadStudentCurriculum(userId)
    }

    fun loadStudentCourses(studentId: Int) {
        viewModelScope.launch {
            _studentCoursesState.value = StudentCoursesUiState.Loading
            repository.getStudentCurriculumCourses(studentId)
                .onSuccess { _studentCoursesState.value = StudentCoursesUiState.Success(it) }
                .onFailure { _studentCoursesState.value = StudentCoursesUiState.Error(it.message ?: "Error al cargar cursos") }
        }
    }

    fun updateCourseStatus(
        studentId: Int,
        courseId: Int,
        status: String,
        finalGrade: Double? = null,
        semester: Int? = null,
        year: Int? = null
    ) {
        viewModelScope.launch {
            val request = UpdateCourseStatusRequest(status, finalGrade, semester, year)
            repository.updateCourseStatus(studentId, courseId, request)
                .onSuccess {
                    loadStudentCourses(studentId)
                    reloadCurriculum(studentId)
                }
                .onFailure { _errorMessage.value = it.message ?: "Error al actualizar estado" }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearForNewSession() {
        _careerName.value = null
        _curriculumState.value = CurriculumUiState.Idle
        _studentCoursesState.value = StudentCoursesUiState.Idle
        _errorMessage.value = null
    }
}

