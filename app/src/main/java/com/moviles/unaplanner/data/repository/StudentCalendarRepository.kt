package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.StudentCalendarApiService
import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.StudentCalendarResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudentCalendarRepository(private val apiService: StudentCalendarApiService) {

    /**
     * Fetches all calendar events for a student
     */
    suspend fun getStudentCalendar(studentId: Int): Result<StudentCalendarResponse> = 
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.getStudentCalendar(studentId)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Fetches details of a specific calendar event
     */
    suspend fun getEventDetail(studentId: Int, eventId: Int): Result<CalendarEvent> = 
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.getEventDetail(studentId, eventId)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Fetches calendar events within a date range
     */
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

    /**
     * Fetches calendar events filtered by activity type
     */
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

    /**
     * Creates a new calendar event
     */
    suspend fun createEvent(studentId: Int, request: com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest): Result<CalendarEvent> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.createEvent(studentId, request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Updates an existing calendar event
     */
    suspend fun updateEvent(
        studentId: Int,
        eventId: Int,
        request: com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest
    ): Result<CalendarEvent> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.updateEvent(studentId, eventId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches all courses for a student
     */
    suspend fun getStudentCourses(studentId: Int): Result<List<com.moviles.unaplanner.data.remote.model.CourseDto>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.getStudentCourses(studentId)
                Result.success(response.data)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Deletes a calendar event
     */
    suspend fun deleteEvent(studentId: Int, eventId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = apiService.deleteEvent(studentId, eventId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error deleting event"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
