package com.moviles.unaplanner.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.data.AuthSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UNAPlannerFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(AppConstants.FCM_LOG_TAG, "Nuevo token FCM: $token")
        serviceScope.launch {
            try {
                AppContainer.notificationRepository.registerToken(token)
            } catch (e: Exception) {
                Log.w(AppConstants.FCM_LOG_TAG, "Error al registrar token: ${e.message}")
            }
        }
    }

    /**
     * Called ONLY when the app is in the FOREGROUND.
     *
     * When the app is backgrounded or killed and the FCM message has a
     * `notification` field, Android displays the device notification
     * automatically — this method is not invoked in that case.
     *
     * Responsibilities here:
     *   1. Show the device notification (foreground heads-up).
     *   2. Signal the bell to refresh from the database.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "UNAPlanner"
        val body  = message.notification?.body  ?: message.data["body"]  ?: "Tienes una nueva notificación"

        Log.d(AppConstants.FCM_LOG_TAG, "Mensaje en primer plano — título: $title | tipo: ${message.data["type"]}")

        // Show device notification (only reaches here in foreground;
        // background case is handled by the Android system)
        try {
            NotificationHelper.show(context = this, title = title, body = body)
        } catch (e: Exception) {
            Log.e(AppConstants.FCM_LOG_TAG, "Error al mostrar notificación: ${e.message}")
        }

        // Refresh the bell from the database
        serviceScope.launch {
            try {
                AppContainer.notificationRepository.notifyNewNotification()
                AuthSession.currentUser?.id?.let { userId ->
                    AppContainer.notificationRepository.getNotifications(userId)
                }
            } catch (e: Exception) {
                Log.w(AppConstants.FCM_LOG_TAG, "Error al actualizar campana: ${e.message}")
            }
        }
    }
}
