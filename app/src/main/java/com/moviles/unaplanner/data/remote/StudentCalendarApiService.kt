package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.StudentCalendarResponse
import retrofit2.http.*

interface StudentCalendarApiService {
    
    /**
     * Gets all calendar events for a student with summary statistics
     */
    @GET("api/student/{id}/calendar")
    suspend fun getStudentCalendar(@Path("id") studentId: Int): StudentCalendarResponse

    /**
     * Gets detailed information about a specific calendar event
     */
    @GET("api/student/{id}/calendar/{eventId}")
    suspend fun getEventDetail(
        @Path("id") studentId: Int,
        @Path("eventId") eventId: Int
    ): CalendarEvent

    /**
     * Gets calendar events within a specific date range
     * @param studentId The ID of the student
     * @param startDate Start date (yyyy-MM-dd format)
     * @param endDate End date (yyyy-MM-dd format)
     */
    @GET("api/student/{id}/calendar/filter/date-range")
    suspend fun getCalendarByDateRange(
        @Path("id") studentId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): StudentCalendarResponse

    /**
     * Gets calendar events filtered by activity type
     * @param studentId The ID of the student
     * @param activityType Type of activity (Exam, Assignment, Project, etc.)
     */
    @GET("api/student/{id}/calendar/filter/activity-type")
    suspend fun getCalendarByActivityType(
        @Path("id") studentId: Int,
        @Query("activityType") activityType: String
    ): StudentCalendarResponse

    /**
     * Creates a new calendar event for a student
     */
    @POST("api/student/{id}/calendar")
    suspend fun createEvent(
        @Path("id") studentId: Int,
        @Body request: com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest
    ): com.moviles.unaplanner.data.remote.model.CalendarEvent

    /**
     * Updates an existing calendar event for a student
     */
    @PUT("api/student/{id}/calendar/{eventId}")
    suspend fun updateEvent(
        @Path("id") studentId: Int,
        @Path("eventId") eventId: Int,
        @Body request: com.moviles.unaplanner.data.remote.model.CreateCalendarEventRequest
    ): com.moviles.unaplanner.data.remote.model.CalendarEvent

    /**
     * Gets all courses for a student
     */
    @GET("api/student/{id}/courses")
    suspend fun getStudentCourses(@Path("id") studentId: Int): com.moviles.unaplanner.data.remote.model.CoursesResponse

    /**
     * Deletes a calendar event for a student
     */
    @DELETE("api/student/{id}/calendar/{eventId}")
    suspend fun deleteEvent(
        @Path("id") studentId: Int,
        @Path("eventId") eventId: Int
    ): retrofit2.Response<Unit>
}
