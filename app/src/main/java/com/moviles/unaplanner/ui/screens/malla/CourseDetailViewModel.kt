package com.moviles.unaplanner.ui.screens.malla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CourseDetailDto
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.CurriculumRepository
import com.moviles.unaplanner.data.repository.NotesRepository
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

class CourseDetailViewModel(
    private val repository: CurriculumRepository,
    private val notesRepository: NotesRepository = NotesRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val state: StateFlow<CourseDetailUiState> = _state

    private val _notesState = MutableStateFlow<CourseNotesUiState>(CourseNotesUiState.Idle)
    val notesState: StateFlow<CourseNotesUiState> = _notesState

    fun loadDetail(studentId: Int, courseId: Int) {
        viewModelScope.launch {
            _state.value = CourseDetailUiState.Loading
            repository.getCourseDetail(studentId, courseId)
                .onSuccess { _state.value = CourseDetailUiState.Success(it) }
                .onFailure { _state.value = CourseDetailUiState.Error(it.message ?: "Error al cargar el detalle del curso") }
        }
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
