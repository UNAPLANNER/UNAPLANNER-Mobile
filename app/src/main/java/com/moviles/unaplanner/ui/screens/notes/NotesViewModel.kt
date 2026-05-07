package com.moviles.unaplanner.ui.screens.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.NoteDto
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

class NotesViewModel(
    private val repository: NotesRepository = NotesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var allNotes: List<NoteDto> = emptyList()
    private var selectedCourse: String = "Todas"

    init {
        loadNotes()
    }

    fun loadNotes() {
        val user = AuthSession.currentUser
        if (user == null) {
            Log.e("NotesViewModel", "No hay usuario en sesión")
            _uiState.value = NotesUiState.Error("Sesión no iniciada. Por favor, inicia sesión de nuevo.")
            return
        }

        val userId = user.id
        Log.d("NotesViewModel", "Cargando notas para el usuario ID: $userId")

        viewModelScope.launch {
            _uiState.value = NotesUiState.Loading
            try {
                val result = repository.getStudentNotes(userId)
                when (result) {
                    is ApiResult.Success -> {
                        Log.d("NotesViewModel", "Notas obtenidas exitosamente: ${result.data.size} notas")
                        allNotes = result.data
                        if (allNotes.isEmpty()) {
                            _uiState.value = NotesUiState.Empty
                        } else {
                            updateState()
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

    fun filterByCourse(courseCode: String) {
        selectedCourse = courseCode
        updateState()
    }

    private fun updateState() {
        val filtered = if (selectedCourse == "Todas") {
            allNotes
        } else if (selectedCourse == "General") {
            allNotes.filter { it.course == null }
        } else {
            allNotes.filter { (it.course?.name ?: it.course?.code) == selectedCourse }
        }

        val coursesList = mutableListOf("Todas")
        coursesList.addAll(allNotes.mapNotNull { it.course?.name ?: it.course?.code }.distinct())
        
        // Solo agregar "General" si realmente hay notas sin curso asociado
        if (allNotes.any { it.course == null }) {
            coursesList.add("General")
        }

        _uiState.value = NotesUiState.Success(
            notes = allNotes,
            filteredNotes = filtered,
            courses = coursesList.distinct()
        )
    }
}
