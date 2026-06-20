package com.moviles.unaplanner.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CurriculumLevelDto
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
    val careerName: String,
    val gpa: Double,
    val lastCycleGpa: Double
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
    private var loadedForUserId: Int? = null
    fun load() {
        val userId = AuthSession.currentUser?.id ?: return
        if (_uiState.value is ProgressUiState.Success && loadedForUserId == userId) return
        val careerId = AuthSession.currentUser?.careerId
        loadedForUserId = userId

        viewModelScope.launch {
            _uiState.value = ProgressUiState.Loading

            if (careerId == null) {
                _uiState.value = ProgressUiState.Error(
                    "No se pudo determinar tu carrera. Vuelve a iniciar sesión."
                )
                return@launch
            }

            // We use careerId + userId instead of studentId: this endpoint
            // is already designed to resolve the progress of the user who has logged in
            // against their curriculum, without the need for the internal studentId.
            val levelsResult = repository.getCareerCurriculum(careerId, userId)
            val levels = levelsResult.getOrNull()
            if (levels == null) {
                val msg = levelsResult.exceptionOrNull()?.message ?: "Error desconocido"
                _uiState.value = ProgressUiState.Error(msg)
                return@launch
            }

            val courses = levels.toFlatCourses()

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
                    careerName = resolveCareerName(careerId),
                    gpa = computeGpa(courses),
                    lastCycleGpa = computeLastCycleGpa(courses)
                )
            )
        }
    }

    fun refresh() {
        _uiState.value = ProgressUiState.Idle
        load()
    }

    // Flattens the nested structure (level -> semester -> courses) into a flat list,
    // reusing the same form as before (StudentCourseProgressDto) to
    // not having to rewrite the rest of the calculations in this class.
    // year/semester (actual date when it was taken) do not come in this endpoint,
    // so they remain null; that's why computeLastCycleGpa uses level/term instead
    // of year/semester to define "last cycle".
    private fun List<CurriculumLevelDto>.toFlatCourses(): List<StudentCourseProgressDto> {
        val result = mutableListOf<StudentCourseProgressDto>()
        for (levelDto in this) {
            for (semesterDto in levelDto.semesters) {
                for (course in semesterDto.courses) {
                    result += StudentCourseProgressDto(
                        courseId = course.id,
                        code = course.code,
                        name = course.name,
                        credits = course.credits,
                        isElective = course.isElective,
                        electiveType = course.electiveType,
                        level = levelDto.level,
                        term = semesterDto.semester,
                        status = course.status,
                        finalGrade = course.finalGrade,
                        semester = null,
                        year = null
                    )
                }
            }
        }
        return result
    }

    // Weighted average by credits of all passed courses.
    // finalGrade is on a 0-100 scale (the same as individual grades).
    private fun computeGpa(courses: List<StudentCourseProgressDto>): Double {
        val approved = courses.filter { it.status == "Aprobado" && it.finalGrade != null }
        if (approved.isEmpty()) return 0.0
        return approved.sumOf { it.finalGrade!! } / approved.size
    }

    // Weighted average by credits, but only for the courses passed in
    // the most advanced level/term of the plan (approximation of "last cycle",
    // since we don't have real year/semester with this endpoint).
    private fun computeLastCycleGpa(courses: List<StudentCourseProgressDto>): Double {
        val approved = courses.filter { it.status == "Aprobado" && it.finalGrade != null }
        if (approved.isEmpty()) return 0.0

        val lastCycle = approved.maxWithOrNull(
            compareBy({ it.level }, { it.term })
        ) ?: return 0.0

        val lastCycleCourses = approved.filter {
            it.level == lastCycle.level && it.term == lastCycle.term
        }

        val totalCredits = lastCycleCourses.sumOf { it.credits }
        if (totalCredits == 0) return 0.0

        val weightedSum = lastCycleCourses.sumOf { it.finalGrade!! * it.credits }
        return weightedSum / totalCredits
    }

    private suspend fun resolveCareerName(careerId: Int): String {
        return repository.getCareers().getOrNull()
            ?.find { it.id == careerId }?.name
            ?: ""
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
