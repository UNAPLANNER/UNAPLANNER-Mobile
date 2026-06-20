package com.moviles.unaplanner.ui.screens.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CourseDto
import com.moviles.unaplanner.data.remote.model.CreateNoteRequest
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.remote.model.StudentCourseProgressDto
import com.moviles.unaplanner.data.remote.model.UpdateNoteRequest
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotesUiState {
    object Loading : NotesUiState()
    data class Success(
        val notes: List<NoteDto>,
        val filteredNotes: List<NoteDto>,
        val courses: List<String>,
        val selectedCourse: String
    ) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
    object Empty : NotesUiState()
}

sealed class NoteEditorUiState {
    object Idle : NoteEditorUiState()
    object Loading : NoteEditorUiState() // Para cuando se carga una nota individual
    object Saving : NoteEditorUiState()
    data class Success(val message: String) : NoteEditorUiState()
    data class Error(val message: String) : NoteEditorUiState()
}

class NotesViewModel(
    private val repository: NotesRepository = NotesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _editorState = MutableStateFlow<NoteEditorUiState>(NoteEditorUiState.Idle)
    val editorState: StateFlow<NoteEditorUiState> = _editorState.asStateFlow()

    private val _studentCourses = MutableStateFlow<List<StudentCourseProgressDto>>(emptyList())
    val studentCourses: StateFlow<List<StudentCourseProgressDto>> = _studentCourses.asStateFlow()

    private var allNotes: List<NoteDto> = emptyList()
    private var selectedCourse: String = "Todas"

    init {
        val user = AuthSession.currentUser
        if (user != null) {
            loadNotes()
            loadStudentCourses()
        }
    }

    /**
     * Refresca la lista de notas desde el servidor y actualiza el estado local.
     * Esta es una función interna de ayuda para asegurar que los cambios se reflejen antes de navegar.
     */
    private suspend fun refreshNotesFromServer(): ApiResult<List<NoteDto>> {
        val user = AuthSession.currentUser ?: return ApiResult.Error("Sesión no iniciada")
        val sid = AuthSession.studentId ?: return ApiResult.Error("Sesión de estudiante no disponible")
        Log.d("NotesViewModel", "Refrescando notas para el estudiante ID: $sid")
        val result = repository.getStudentNotes(sid)
        
        if (result is ApiResult.Success) {
            allNotes = result.data
            Log.d("NotesViewModel", "Notas actualizadas localmente: ${allNotes.size}")
            updateState()
        }
        
        return result
    }

    fun loadNotes() {
        _uiState.value = NotesUiState.Loading
        viewModelScope.launch {
            try {
                val result = refreshNotesFromServer()
                if (result is ApiResult.Error) {
                    Log.e("NotesViewModel", "Error al obtener notas: ${result.message}")
                    _uiState.value = NotesUiState.Error(result.message)
                }
                // updateState() ya es llamado dentro de refreshNotesFromServer si es Success
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Excepción al cargar notas", e)
                _uiState.value = NotesUiState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    fun loadStudentCourses() {
        val user = AuthSession.currentUser ?: return
        viewModelScope.launch {
            try {
                val studentId = AuthSession.studentId ?: return@launch
                // Obtenemos los cursos con su estado actual de la malla
                val result = repository.getStudentEnrolledCourses(studentId)
                when (result) {
                    is ApiResult.Success -> {
                        // Filtramos solo los cursos que están "EnCurso"
                        val enrolledCourses = result.data.filter { it.status == "EnCurso" }
                        _studentCourses.value = enrolledCourses
                        Log.d("NotesViewModel", "Cursos EnCurso cargados: ${enrolledCourses.size}")

                        updateState()
                    }
                    is ApiResult.Error -> {
                        Log.e("NotesViewModel", "Error al cargar cursos: ${result.message}")
                        _studentCourses.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Excepción al cargar cursos", e)
                _studentCourses.value = emptyList()
            }
        }
    }

    fun filterByCourse(courseCode: String) {
        selectedCourse = courseCode
        updateState()
    }

    fun createNote(title: String, content: String, courseId: Int? = null) {
        val user = AuthSession.currentUser
        if (user == null) {
            _editorState.value = NoteEditorUiState.Error("Sesión no iniciada")
            return
        }

        if (title.isBlank()) {
            _editorState.value = NoteEditorUiState.Error("El título es requerido")
            return
        }

        viewModelScope.launch {
            _editorState.value = NoteEditorUiState.Saving
            try {
                val request = CreateNoteRequest(
                    title = title.trim(),
                    content = content.trim(),
                    courseId = courseId
                )
                val studentId = AuthSession.studentId ?: run {
                    _editorState.value = NoteEditorUiState.Error("Sesión de estudiante no disponible")
                    return@launch
                }
                val result = repository.createNote(studentId, request)
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("NotesViewModel", "Nota creada, refrescando lista...")
                        refreshNotesFromServer() // Refrescar ANTES de marcar éxito
                        _editorState.value = NoteEditorUiState.Success("Nota guardada exitosamente")
                    }
                    is ApiResult.Error -> {
                        _editorState.value = NoteEditorUiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _editorState.value = NoteEditorUiState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    fun updateNote(noteId: Int, title: String, content: String, courseId: Int? = null) {
        if (title.isBlank()) {
            _editorState.value = NoteEditorUiState.Error("El título es requerido")
            return
        }

        viewModelScope.launch {
            _editorState.value = NoteEditorUiState.Saving
            try {
                val request = UpdateNoteRequest(
                    title = title.trim(),
                    content = content.trim(),
                    courseId = courseId
                )
                val result = repository.updateNote(noteId, request)
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("NotesViewModel", "Nota actualizada, refrescando lista...")
                        refreshNotesFromServer() // Refrescar ANTES de marcar éxito
                        _editorState.value = NoteEditorUiState.Success("Nota actualizada exitosamente")
                    }
                    is ApiResult.Error -> {
                        _editorState.value = NoteEditorUiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _editorState.value = NoteEditorUiState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    fun deleteNote(noteId: Int) {
        if (AuthSession.currentUser == null) {
            _editorState.value = NoteEditorUiState.Error("Sesión no iniciada")
            return
        }

        viewModelScope.launch {
            _editorState.value = NoteEditorUiState.Saving
            try {
                val result = repository.deleteNote(noteId)
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("NotesViewModel", "Nota eliminada, refrescando lista...")
                        refreshNotesFromServer() /// Refresh BEFORE marking success
                        _editorState.value = NoteEditorUiState.Success("Nota eliminada exitosamente")
                    }
                    is ApiResult.Error -> {
                        _editorState.value = NoteEditorUiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _editorState.value = NoteEditorUiState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    fun getNoteById(noteId: Int): NoteDto? {
        return allNotes.find { it.id == noteId }
    }

    fun resetEditorState() {
        _editorState.value = NoteEditorUiState.Idle
    }

    private fun updateState() {
        val filtered = if (selectedCourse == "Todas") {
            allNotes
        } else {
            allNotes.filter { it.displayCourseName == selectedCourse }
        }

        // Tabs: "Todas", "General", and the student's currently enrolled (EnCurso) courses from the API
        val coursesList = mutableListOf("Todas", "General")
        coursesList.addAll(_studentCourses.value.map { it.name })

        // We use Success to ensure that filters and the "+New" button are shown even without notes
        _uiState.value = NotesUiState.Success(
            notes = allNotes,
            filteredNotes = filtered,
            courses = coursesList.distinct().sortedBy {
                when (it) {
                    "Todas" -> "0"
                    "General" -> "1"
                    else -> "2$it"
                }
            },
            selectedCourse = selectedCourse
        )
    }
}
