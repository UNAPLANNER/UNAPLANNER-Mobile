package com.moviles.unaplanner.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.unaplanner.core.NetworkMonitor
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest
import com.moviles.unaplanner.data.remote.model.CourseDto
import com.moviles.unaplanner.data.remote.model.StudentCalendarResponse
import com.moviles.unaplanner.data.repository.CurriculumRepository
import com.moviles.unaplanner.data.repository.StudentCalendarRepository
import com.moviles.unaplanner.data.repository.buildCalendarResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentCalendarViewModel(
    private val repository: StudentCalendarRepository,
    private val networkMonitor: NetworkMonitor,
    private val curriculumRepository: CurriculumRepository? = null
) : ViewModel() {

    private val _calendarState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val calendarState: StateFlow<CalendarUiState> = _calendarState

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _selectedEvent = MutableStateFlow<CalendarEvent?>(null)
    val selectedEvent: StateFlow<CalendarEvent?> = _selectedEvent

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _studentCourses = MutableStateFlow<List<CourseDto>>(emptyList())
    val studentCourses: StateFlow<List<CourseDto>> = _studentCourses

    init {
        // Monitor connectivity
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _isOffline.value = !online
            }
        }

        // View Room cache to instantly display data
        viewModelScope.launch {
            val userId = AuthSession.currentUser?.id ?: return@launch
            repository.getEventsFlow(userId).collect { cachedEvents ->
                if (_calendarState.value is CalendarUiState.Loading && cachedEvents.isNotEmpty()) {
                    _calendarState.value = CalendarUiState.Success(buildCalendarResponse(cachedEvents))
                }
            }
        }
    }

    fun loadInProgressCourses(studentId: Int) {
        viewModelScope.launch {
            val repo = curriculumRepository ?: return@launch
            val result = repo.getStudentCurriculumCourses(studentId)
            result.onSuccess { courses ->
                _studentCourses.value = courses
                    .filter { it.status == "EnCurso" }
                    .map { CourseDto(id = it.courseId, code = it.code, name = it.name) }
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
                // If the API fails, try to display cached data.
                val cached = repository.getCachedEvents(studentId)
                if (cached.isNotEmpty()) {
                    _calendarState.value = CalendarUiState.Success(buildCalendarResponse(cached))
                } else {
                    _errorMessage.value = error.message ?: "Error desconocido"
                    _calendarState.value = CalendarUiState.Error(
                        if (_isOffline.value) "Sin conexión a internet. No hay datos en caché."
                        else error.message ?: "Error al cargar el calendario"
                    )
                }
            }
        }
    }

    fun loadEventDetail(studentId: Int, eventId: Int) {
        viewModelScope.launch {
            val result = repository.getEventDetail(studentId, eventId)
            result.onSuccess { event ->
                _selectedEvent.value = event
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al cargar detalles"
            }
        }
    }

    fun filterByDateRange(studentId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            _calendarState.value = CalendarUiState.Loading
            val result = repository.getCalendarByDateRange(studentId, startDate, endDate)
            result.onSuccess { calendar ->
                _calendarState.value = CalendarUiState.Success(calendar)
            }.onFailure { error ->
                _calendarState.value = CalendarUiState.Error(error.message ?: "Error al filtrar")
            }
        }
    }

    fun filterByActivityType(studentId: Int, activityType: String) {
        viewModelScope.launch {
            _calendarState.value = CalendarUiState.Loading
            val result = repository.getCalendarByActivityType(studentId, activityType)
            result.onSuccess { calendar ->
                _calendarState.value = CalendarUiState.Success(calendar)
            }.onFailure { error ->
                _calendarState.value = CalendarUiState.Error(error.message ?: "Error al filtrar")
            }
        }
    }

    fun clearSelectedEvent() {
        _selectedEvent.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

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
            val request = CreateCalendarEventRequest(
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
            val request = CreateCalendarEventRequest(
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

sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Success(val calendar: StudentCalendarResponse) : CalendarUiState()
    data class Error(val message: String) : CalendarUiState()
}
