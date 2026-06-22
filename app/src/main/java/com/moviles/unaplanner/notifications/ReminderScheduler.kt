package com.moviles.unaplanner.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.EvaluationDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val PREFIX_CAL  = "reminder_cal_"
    private const val PREFIX_EVAL = "reminder_eval_"

    fun scheduleCalendar(context: Context, event: CalendarEvent) {
        if (!event.hasReminder) {
            cancelCalendar(context, event.id)
            return
        }
        val millis = parseMillis(event.reminderDate ?: return) ?: return
        val delay = millis - System.currentTimeMillis()
        if (delay <= 0) return
        enqueue(context, "$PREFIX_CAL${event.id}", delay, event.title, event.activityType)
    }

    fun cancelCalendar(context: Context, eventId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("$PREFIX_CAL$eventId")
    }

    fun scheduleEvaluation(context: Context, evaluation: EvaluationDto) {
        if (!evaluation.hasReminder) {
            cancelEvaluation(context, evaluation.id)
            return
        }
        val actMillis = parseMillis(evaluation.date ?: return) ?: return
        val delay = actMillis - TimeUnit.DAYS.toMillis(1) - System.currentTimeMillis()
        if (delay <= 0) return
        enqueue(context, "$PREFIX_EVAL${evaluation.id}", delay, evaluation.name, evaluation.type)
    }

    fun cancelEvaluation(context: Context, evaluationId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("$PREFIX_EVAL$evaluationId")
    }

    private fun enqueue(context: Context, name: String, delayMs: Long, title: String, type: String) {
        val data = workDataOf(ReminderWorker.KEY_TITLE to title, ReminderWorker.KEY_TYPE to type)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request)
    }

    private fun parseMillis(iso: String): Long? = runCatching {
        when {
            iso.endsWith("Z") -> java.time.Instant.parse(iso).toEpochMilli()
            iso.length > 10   -> LocalDateTime.parse(iso)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            else              -> LocalDate.parse(iso)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }.getOrNull()
}
