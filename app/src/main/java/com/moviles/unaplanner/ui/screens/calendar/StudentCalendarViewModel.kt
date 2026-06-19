package com.moviles.unaplanner.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.StudentCalendarResponse
import com.moviles.unaplanner.data.repository.StudentCalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentCalendarViewModel(
    private val repository: StudentCalendarRepository
) : ViewModel() {

    private val _calendarState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val calendarState: StateFlow<CalendarUiState> = _calendarState

    private val _selectedEvent = MutableStateFlow<CalendarEvent?>(null)
    val selectedEvent: StateFlow<CalendarEvent?> = _selectedEvent

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _studentCourses = MutableStateFlow<List<com.moviles.unaplanner.data.remote.model.CourseDto>>(emptyList())
    val studentCourses: StateFlow<List<com.moviles.unaplanner.data.remote.model.CourseDto>> = _studentCourses

    /**
     * Loads student courses for the dropdown
     */
    fun loadStudentCourses(studentId: Int) {
        viewModelScope.launch {
            val result = repository.getStudentCourses(studentId)
            result.onSuccess { courses ->
                _studentCourses.value = courses
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al cargar cursos"
            }
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
    fun loadStudentCalendar(studentId: Int) {
        viewModelScope.launch {
            _calendarState.value = CalendarUiState.Loading
            val result = repository.getStudentCalendar(studentId)
            result.onSuccess { calendar ->
                _calendarState.value = CalendarUiState.Success(calendar)
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Unknown error occurred"
                _calendarState.value = CalendarUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    /**
     * Loads details for a specific calendar event
     */
    fun loadEventDetail(studentId: Int, eventId: Int) {
        viewModelScope.launch {
            val result = repository.getEventDetail(studentId, eventId)
            result.onSuccess { event ->
                _selectedEvent.value = event
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error loading event details"
            }
        }
    }

    /**
     * Filters calendar by date range
     */
    fun filterByDateRange(studentId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            _calendarState.value = CalendarUiState.Loading
            val result = repository.getCalendarByDateRange(studentId, startDate, endDate)
            result.onSuccess { calendar ->
                _calendarState.value = CalendarUiState.Success(calendar)
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error filtering calendar"
                _calendarState.value = CalendarUiState.Error(error.message ?: "Error filtering calendar")
            }
        }
    }

    /**
     * Filters calendar by activity type
     */
    fun filterByActivityType(studentId: Int, activityType: String) {
        viewModelScope.launch {
            _calendarState.value = CalendarUiState.Loading
            val result = repository.getCalendarByActivityType(studentId, activityType)
            result.onSuccess { calendar ->
                _calendarState.value = CalendarUiState.Success(calendar)
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error filtering calendar"
                _calendarState.value = CalendarUiState.Error(error.message ?: "Error filtering calendar")
            }
        }
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Creates a new event
     */
    fun createEvent(
        studentId: Int,
        title: String,
        description: String?,
        activityDate: String,
        activityType: String,
        courseId: Int?,
        hasReminder: Boolean,
        reminderDate: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val request = com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest(
                title = title,
                description = description,
                activityDate = activityDate,
                activityType = activityType,
                courseId = courseId,
                hasReminder = hasReminder,
                reminderDate = reminderDate
            )

            val result = repository.createEvent(studentId, request)
            result.onSuccess {
                _successMessage.value = "Actividad guardada exitosamente"
                loadStudentCalendar(studentId)
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al crear el evento"
            }
        }
    }

    /**
     * Updates an existing event
     */
    fun updateEvent(
        studentId: Int,
        eventId: Int,
        title: String,
        description: String?,
        activityDate: String,
        activityType: String,
        courseId: Int?,
        hasReminder: Boolean,
        reminderDate: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val request = com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest(
                title = title,
                description = description,
                activityDate = activityDate,
                activityType = activityType,
                courseId = courseId,
                hasReminder = hasReminder,
                reminderDate = reminderDate
            )

            val result = repository.updateEvent(studentId, eventId, request)
            result.onSuccess {
                _successMessage.value = "Actividad guardada exitosamente"
                loadStudentCalendar(studentId)
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al actualizar la actividad"
            }
        }
    }

    /**
     * Deletes an event
     */
    fun deleteEvent(studentId: Int, eventId: Int) {
        viewModelScope.launch {
            val result = repository.deleteEvent(studentId, eventId)
            result.onSuccess {
                _successMessage.value = "Actividad eliminada exitosamente"
                loadStudentCalendar(studentId)
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al eliminar la actividad"
            }
        }
    }
}

/**
 * Sealed class representing different UI states for the calendar
 */
sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Success(val calendar: StudentCalendarResponse) : CalendarUiState()
    data class Error(val message: String) : CalendarUiState()
}
