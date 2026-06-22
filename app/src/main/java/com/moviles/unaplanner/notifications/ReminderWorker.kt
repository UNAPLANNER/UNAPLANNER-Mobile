package com.moviles.unaplanner.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val activityType = inputData.getString(KEY_TYPE) ?: ""
        val notifTitle = when (activityType.lowercase()) {
            "examen"     -> "Recordatorio: Examen mañana"
            "tarea"      -> "Recordatorio: Tarea mañana"
            "proyecto"   -> "Recordatorio: Proyecto mañana"
            "exposicion",
            "exposición" -> "Recordatorio: Exposición mañana"
            "evento"     -> "Recordatorio: Evento mañana"
            else         -> "Recordatorio de actividad"
        }
        NotificationHelper.show(applicationContext, notifTitle, "\"$title\" es mañana. ¡No te olvides!")
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_TYPE  = "activityType"
    }
}
