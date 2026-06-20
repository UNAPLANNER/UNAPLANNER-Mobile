package com.moviles.unaplanner.ui.screens.malla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CourseDetailDto
import com.moviles.unaplanner.data.remote.model.EnrolledCourseDetailRequest
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.CurriculumRepository
import com.moviles.unaplanner.data.repository.NotesRepository
import retrofit2.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CourseDetailUiState {
    object Loading : CourseDetailUiState()
    data class Success(val detail: CourseDetailDto) : CourseDetailUiState()
    data class Error(val message: String) : CourseDetailUiState()
}

sealed class CourseNotesUiState {
    object Idle : CourseNotesUiState()
    object Loading : CourseNotesUiState()
    data class Success(val notes: List<NoteDto>) : CourseNotesUiState()
    data class Error(val message: String) : CourseNotesUiState()
}

sealed class SaveDetailUiState {
    object Idle : SaveDetailUiState()
    object Saving : SaveDetailUiState()
    object Success : SaveDetailUiState()
    data class Error(val message: String) : SaveDetailUiState()
}

class CourseDetailViewModel(
    private val repository: CurriculumRepository,
    private val notesRepository: NotesRepository = NotesRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val state: StateFlow<CourseDetailUiState> = _state

    private val _notesState = MutableStateFlow<CourseNotesUiState>(CourseNotesUiState.Idle)
    val notesState: StateFlow<CourseNotesUiState> = _notesState

    private val _saveState = MutableStateFlow<SaveDetailUiState>(SaveDetailUiState.Idle)
    val saveState: StateFlow<SaveDetailUiState> = _saveState

    fun loadDetail(studentId: Int, courseId: Int) {
        viewModelScope.launch {
            _state.value = CourseDetailUiState.Loading
            repository.getCourseDetail(studentId, courseId)
                .onSuccess { _state.value = CourseDetailUiState.Success(it) }
                .onFailure { e ->
                    val message = when {
                        e is HttpException && e.code() == 404 ->
                            "No se encontraron datos del curso en el servidor."
                        e is HttpException ->
                            "Error del servidor (${e.code()}). Intente de nuevo."
                        else -> e.message ?: "Error al cargar el detalle del curso"
                    }
                    _state.value = CourseDetailUiState.Error(message)
                }
        }
    }

    fun saveEnrolledDetail(
        studentId: Int,
        courseId: Int,
        enrolledDetailId: Int?,
        professorName: String?,
        classroom: String?,
        schedule: String?,
        syllabusUrl: String?
    ) {
        viewModelScope.launch {
            _saveState.value = SaveDetailUiState.Saving
            val request = EnrolledCourseDetailRequest(
                professorName = professorName?.takeIf { it.isNotBlank() },
                classroom = classroom?.takeIf { it.isNotBlank() },
                schedule = schedule?.takeIf { it.isNotBlank() },
                syllabusUrl = syllabusUrl?.takeIf { it.isNotBlank() }
            )
            val result = if (enrolledDetailId == null) {
                repository.createEnrolledDetail(studentId, courseId, request)
            } else {
                repository.updateEnrolledDetail(studentId, courseId, request)
            }
            result
                .onSuccess { updated ->
                    _state.value = CourseDetailUiState.Success(updated)
                    _saveState.value = SaveDetailUiState.Success
                }
                .onFailure { _saveState.value = SaveDetailUiState.Error(it.message ?: "Error al guardar los datos del curso") }
        }
    }

    fun clearSaveState() {
        _saveState.value = SaveDetailUiState.Idle
    }

    fun loadCourseNotes(studentId: Int, courseId: Int) {
        if (_notesState.value is CourseNotesUiState.Loading) return
        viewModelScope.launch {
            _notesState.value = CourseNotesUiState.Loading
            when (val result = notesRepository.getNotesByCourse(studentId, courseId)) {
                is ApiResult.Success -> _notesState.value = CourseNotesUiState.Success(result.data)
                is ApiResult.Error -> _notesState.value = CourseNotesUiState.Error(result.message)
            }
        }
    }

    fun reloadCourseNotes(studentId: Int, courseId: Int) {
        _notesState.value = CourseNotesUiState.Idle
        loadCourseNotes(studentId, courseId)
    }
}
