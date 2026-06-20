package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.local.CalendarEventDao
import com.moviles.unaplanner.data.local.toCalendarEvent
import com.moviles.unaplanner.data.local.toEntity
import com.moviles.unaplanner.data.remote.StudentCalendarApiService
import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.CalendarSummaryStats
import com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest
import com.moviles.unaplanner.data.remote.model.StudentCalendarResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StudentCalendarRepository(
    private val apiService: StudentCalendarApiService,
    private val calendarEventDao: CalendarEventDao
) {

    //UI observes Room — it updates automatically when there is new data
    fun getEventsFlow(userId: Int): Flow<List<CalendarEvent>> =
        calendarEventDao.getEventsByUserIdFlow(userId)
            .map { entities -> entities.map { it.toCalendarEvent() } }

    // Direct cache read (for offline fallback)
    suspend fun getCachedEvents(userId: Int): List<CalendarEvent> =
        withContext(Dispatchers.IO) {
            calendarEventDao.getEventsByUserId(userId).map { it.toCalendarEvent() }
        }

    suspend fun getStudentCalendar(studentId: Int): Result<StudentCalendarResponse> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.getStudentCalendar(studentId)
                // Guardar en Room para acceso offline
                val entities = response.events.map { it.toEntity(studentId) }
                calendarEventDao.upsertAll(entities)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getEventDetail(studentId: Int, eventId: Int): Result<CalendarEvent> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.getEventDetail(studentId, eventId)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getCalendarByDateRange(
        studentId: Int,
        startDate: String,
        endDate: String
    ): Result<StudentCalendarResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getCalendarByDateRange(studentId, startDate, endDate)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCalendarByActivityType(
        studentId: Int,
        activityType: String
    ): Result<StudentCalendarResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getCalendarByActivityType(studentId, activityType)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(studentId: Int, request: CreateCalendarEventRequest): Result<CalendarEvent> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.createEvent(studentId, request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateEvent(
        studentId: Int,
        eventId: Int,
        request: CreateCalendarEventRequest
    ): Result<CalendarEvent> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.updateEvent(studentId, eventId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentCourses(studentId: Int): Result<List<com.moviles.unaplanner.data.remote.model.CourseDto>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.getStudentCourses(studentId)
                Result.success(response.data)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteEvent(studentId: Int, eventId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.deleteEvent(studentId, eventId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al eliminar el evento"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

// Calculates CalendarSummaryStats from a list of events (used with cached data)
fun buildCalendarResponse(events: List<CalendarEvent>): StudentCalendarResponse {
    val summary = CalendarSummaryStats(
        totalEvents = events.size,
        upcomingEvents = events.count { !it.isCompleted },
        completedEvents = events.count { it.isCompleted },
        examsCount = events.count {
            it.activityType.equals("Examen", ignoreCase = true) ||
            it.activityType.equals("Exam", ignoreCase = true)
        },
        assignmentsCount = events.count {
            it.activityType.equals("Tarea", ignoreCase = true) ||
            it.activityType.equals("Assignment", ignoreCase = true) ||
            it.activityType.equals("Proyecto", ignoreCase = true) ||
            it.activityType.equals("Project", ignoreCase = true)
        }
    )
    return StudentCalendarResponse(events = events, summary = summary)
}
