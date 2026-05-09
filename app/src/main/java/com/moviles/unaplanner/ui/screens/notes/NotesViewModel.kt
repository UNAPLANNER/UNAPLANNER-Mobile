package com.moviles.unaplanner.ui.screens.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CourseDto
import com.moviles.unaplanner.data.remote.model.CreateNoteRequest
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.remote.model.UpdateNoteRequest
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotesUiState {
    object Loading : NotesUiState()
    data class Success(val notes: List<NoteDto>, val filteredNotes: List<NoteDto>, val courses: List<String>) : NotesUiState()
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

    private val _studentCourses = MutableStateFlow<List<CourseDto>>(emptyList())
    val studentCourses: StateFlow<List<CourseDto>> = _studentCourses.asStateFlow()

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
        
        Log.d("NotesViewModel", "Refrescando notas para el usuario ID: ${user.id}")
        val result = repository.getStudentNotes(user.id)
        
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
                when (val result = refreshNotesFromServer()) {
                    is ApiResult.Success -> {
                        if (allNotes.isEmpty()) {
                            _uiState.value = NotesUiState.Empty
                        }
                    }
                    is ApiResult.Error -> {
                        Log.e("NotesViewModel", "Error al obtener notas: ${result.message}")
                        _uiState.value = NotesUiState.Error(result.message)
                    }
                }
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
                val result = repository.getStudentCourses(user.id)
                when (result) {
                    is ApiResult.Success -> {
                        _studentCourses.value = result.data
                        Log.d("NotesViewModel", "Cursos cargados: ${result.data.size} cursos")
                        // Actualizar UI para incluir los nuevos nombres de cursos en los filtros si es necesario
                        if (_uiState.value is NotesUiState.Success || _uiState.value is NotesUiState.Empty) {
                            updateState()
                        }
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
                val result = repository.createNote(user.id, request)
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
        val user = AuthSession.currentUser
        if (user == null) {
            _editorState.value = NoteEditorUiState.Error("Sesión no iniciada")
            return
        }

        viewModelScope.launch {
            _editorState.value = NoteEditorUiState.Saving
            try {
                val result = repository.deleteNote(noteId, user.id)
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("NotesViewModel", "Nota eliminada, refrescando lista...")
                        refreshNotesFromServer() // Refrescar ANTES de marcar éxito
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

        val coursesList = mutableListOf("Todas")
        
        // Agregar nombres de cursos del plan de estudio del estudiante
        coursesList.addAll(_studentCourses.value.map { it.name })
        
        // Agregar nombres de cursos que tienen notas
        coursesList.addAll(allNotes.map { it.displayCourseName })
        
        // Asegurar que "General" esté siempre disponible
        coursesList.add("General")

        _uiState.value = NotesUiState.Success(
            notes = allNotes,
            filteredNotes = filtered,
            courses = coursesList.distinct().sortedBy { 
                when (it) {
                    "Todas" -> "0"
                    "General" -> "1"
                    else -> "2$it"
                }
            }
        )
    }
}
