package com.moviles.unaplanner.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.StudentCourseProgressDto
import com.moviles.unaplanner.data.repository.CurriculumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AreaProgress(
    val name: String,
    val progress: Float,
    val approved: Int,
    val total: Int
)

data class ProgressData(
    val totalCourses: Int,
    val approved: Int,
    val inProgress: Int,
    val pending: Int,
    val percentCompleted: Float,
    val areaProgress: List<AreaProgress>,
    val graduationEstimate: String,
    val careerName: String
)

sealed class ProgressUiState {
    object Idle : ProgressUiState()
    object Loading : ProgressUiState()
    data class Success(val data: ProgressData) : ProgressUiState()
    data class Error(val message: String) : ProgressUiState()
}

class ProgressViewModel(private val repository: CurriculumRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Idle)
    val uiState: StateFlow<ProgressUiState> = _uiState

    fun load() {
        if (_uiState.value is ProgressUiState.Success) return
        val userId = AuthSession.currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.value = ProgressUiState.Loading

            val coursesResult = repository.getStudentCurriculumCourses(userId)
            val courses = coursesResult.getOrNull()
            if (courses == null) {
                val msg = coursesResult.exceptionOrNull()?.message ?: "Error desconocido"
                _uiState.value = ProgressUiState.Error(msg)
                return@launch
            }

            val total = courses.size
            val approved = courses.count { it.status == "Aprobado" }
            val inProgress = courses.count { it.status == "EnCurso" }
            val pending = courses.count { it.status != "Aprobado" && it.status != "EnCurso" }
            val percent = if (total > 0) approved.toFloat() / total else 0f

            _uiState.value = ProgressUiState.Success(
                ProgressData(
                    totalCourses = total,
                    approved = approved,
                    inProgress = inProgress,
                    pending = pending,
                    percentCompleted = percent,
                    areaProgress = computeAreaProgress(courses),
                    graduationEstimate = estimateGraduation(courses),
                    careerName = resolveCareerName(userId)
                )
            )
        }
    }

    fun refresh() {
        _uiState.value = ProgressUiState.Idle
        load()
    }

    private suspend fun resolveCareerName(userId: Int): String {
        val careerId = AuthSession.currentUser?.careerId
            ?: repository.getStudentProfile(userId).getOrNull()?.careerId
            ?: return "Ingeniería en Sistemas de Información"
        return repository.getCareers().getOrNull()
            ?.find { it.id == careerId }?.name
            ?: "Ingeniería en Sistemas de Información"
    }

    private fun computeAreaProgress(courses: List<StudentCourseProgressDto>): List<AreaProgress> {
        val areas = listOf(
            "Programación" to setOf("PRO", "ALG", "POO", "WEB", "MOV", "SOF", "APS", "DES"),
            "Matemáticas" to setOf("MAT", "CAL", "EST", "FIS", "INV"),
            "Bases de Datos / Sistemas" to setOf("BD", "SIS", "RED", "ARQ", "SEG", "SO", "BAS", "INF"),
            "Inglés / Generales" to setOf("ING", "ESP", "HUM", "GEN", "FIL", "SOC", "CUL", "ADM")
        )
        val classified = mutableSetOf<Int>()
        val result = mutableListOf<AreaProgress>()

        for ((name, prefixes) in areas) {
            val areaCourses = courses.filter { course ->
                val prefix = course.code.substringBefore("-").uppercase()
                prefixes.any { prefix.startsWith(it) }
            }
            classified += areaCourses.map { it.courseId }
            if (areaCourses.isEmpty()) continue
            val areaApproved = areaCourses.count { it.status == "Aprobado" }
            result += AreaProgress(
                name = name,
                progress = areaApproved.toFloat() / areaCourses.size,
                approved = areaApproved,
                total = areaCourses.size
            )
        }

        val others = courses.filter { it.courseId !in classified }
        if (others.isNotEmpty()) {
            val othersApproved = others.count { it.status == "Aprobado" }
            result += AreaProgress(
                name = "Otros",
                progress = othersApproved.toFloat() / others.size,
                approved = othersApproved,
                total = others.size
            )
        }

        return result
    }

    private fun estimateGraduation(courses: List<StudentCourseProgressDto>): String {
        val remainingSemesters = courses
            .filter { it.status == "Pendiente" || it.status == "EnCurso" }
            .groupBy { it.level to it.term }
            .size

        // Current: June 2026 = I Ciclo 2026
        var year = 2026
        var ciclo = 1

        repeat(remainingSemesters) {
            if (ciclo == 1) ciclo = 2 else { ciclo = 1; year++ }
        }

        return "${if (ciclo == 1) "I Ciclo" else "II Ciclo"} $year"
    }
}
