package com.moviles.unaplanner.data.remote.model

import android.graphics.Color

// Request model for creating new events
data class CreateCalendarEventRequest(
    val title: String,
    val description: String?,
    val activityDate: String,  // ISO 8601
    val activityType: String,  // Examen, Tarea, Proyecto, etc.
    val courseId: Int?,
    val hasReminder: Boolean = false,
    val reminderDate: String?
)

// Data class for individual calendar events (Response)
data class CalendarEvent(
    val id: Int,
    val title: String,
    val description: String? = null,
    val activityDate: String,
    val activityType: String, // "Exam", "Assignment", "Project"
    val courseId: Int? = null,
    val courseName: String? = null,
    val hasReminder: Boolean,
    val reminderDate: String? = null,
    val isCompleted: Boolean,
    val createdDate: String
) {
    /**
     * Returns the icon resource ID based on activity type
     */
    fun getActivityIcon(): Int {
        return when (activityType.lowercase()) {
            "exam" -> android.R.drawable.ic_dialog_alert
            "assignment" -> android.R.drawable.ic_menu_agenda
            "project" -> android.R.drawable.ic_menu_view
            else -> android.R.drawable.ic_menu_info_details
        }
    }

    /**
     * Returns the color code based on activity type
     */
    fun getActivityColor(): Int {
        return when (activityType.lowercase()) {
            "exam" -> Color.parseColor("#FF5252")      // Red
            "assignment" -> Color.parseColor("#FFC107") // Amber
            "project" -> Color.parseColor("#4CAF50")    // Green
            else -> Color.parseColor("#9C27B0")          // Purple
        }
    }
}

// Statistics about the calendar
data class CalendarSummaryStats(
    val totalEvents: Int,
    val upcomingEvents: Int,
    val completedEvents: Int,
    val examsCount: Int,
    val assignmentsCount: Int
)

// Complete calendar response with events and summary
data class StudentCalendarResponse(
    val events: List<CalendarEvent>,
    val summary: CalendarSummaryStats
)
