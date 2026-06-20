package com.moviles.unaplanner.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.moviles.unaplanner.MainActivity
import com.moviles.unaplanner.R
import com.moviles.unaplanner.core.AppConstants

object NotificationHelper {

    const val CHANNEL_ID = "unaplanner_notifications"
    private const val CHANNEL_NAME = "UNAPlanner Notificaciones"
    private val TAG = AppConstants.FCM_LOG_TAG

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // If an older installation created the channel with low importance, delete and recreate it
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing != null && existing.importance < NotificationManager.IMPORTANCE_HIGH) {
                manager.deleteNotificationChannel(CHANNEL_ID)
            }
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios y avisos de actividades académicas"
                enableVibration(true)
                enableLights(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun show(context: Context, title: String, body: String) {
        createChannel(context)

        Log.d(TAG, "NotificationHelper.show() invocado — título: $title")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Log.w(TAG, "POST_NOTIFICATIONS no concedido — revisar Ajustes → Apps → UNAPlanner → Notificaciones")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_unaplanner)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_unaplanner_monochrome)
            .setLargeIcon(largeIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Asegura que "salte" en pantalla
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // Sonido y vibración por defecto
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        Log.d(TAG, "Mostrando notificación id=$notificationId título=$title")
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
