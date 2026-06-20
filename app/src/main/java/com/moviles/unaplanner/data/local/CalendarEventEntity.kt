package com.moviles.unaplanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.unaplanner.data.remote.model.CalendarEvent

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val description: String?,
    val activityDate: String,
    val activityType: String,
    val courseId: Int?,
    val courseName: String?,
    val hasReminder: Boolean,
    val reminderDate: String?,
    val isCompleted: Boolean,
    val createdDate: String
)

fun CalendarEventEntity.toCalendarEvent(): CalendarEvent = CalendarEvent(
    id = id,
    title = title,
    description = description,
    activityDate = activityDate,
    activityType = activityType,
    courseId = courseId,
    courseName = courseName,
    hasReminder = hasReminder,
    reminderDate = reminderDate,
    isCompleted = isCompleted,
    createdDate = createdDate
)

fun CalendarEvent.toEntity(userId: Int): CalendarEventEntity = CalendarEventEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    activityDate = activityDate,
    activityType = activityType,
    courseId = courseId,
    courseName = courseName,
    hasReminder = hasReminder,
    reminderDate = reminderDate,
    isCompleted = isCompleted,
    createdDate = createdDate
)
