package com.moviles.unaplanner.data.repository

import android.util.Log
import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.NotificationApiService
import com.moviles.unaplanner.data.remote.model.FcmTokenRequest
import com.moviles.unaplanner.data.remote.model.NotificationDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationRepository(
    private val apiService: NotificationApiService
) {
    private val _newNotificationEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val newNotificationEvents = _newNotificationEvents.asSharedFlow()

    fun notifyNewNotification() {
        _newNotificationEvents.tryEmit(Unit)
    }

    //Send the FCM token to the backend so it can send push notifications to this device
    suspend fun registerToken(token: String) {
        val userId = AuthSession.currentUser?.id ?: run {
            // The user is not logged in yet; they will be logged in upon logging in.
            Log.d(AppConstants.FCM_LOG_TAG, "Sin sesión activa — token FCM diferido hasta el login.")
            return
        }

        try {
            apiService.registerDeviceToken(
                userId = userId,
                request = FcmTokenRequest(fcmToken = token)
            )
            Log.d(AppConstants.FCM_LOG_TAG, "Token FCM registrado para usuario $userId")
        } catch (e: Exception) {
            Log.w(AppConstants.FCM_LOG_TAG, "Error al registrar token FCM: ${e.message}")
        }
    }

    suspend fun getNotifications(userId: Int): ApiResult<List<NotificationDto>> {
        return try {
            val response = apiService.getNotifications(userId)
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error("Error al obtener notificaciones: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun markAsRead(userId: Int, notificationId: Int): ApiResult<Unit> {
        return try {
            val response = apiService.markAsRead(userId, notificationId)
            if (response.isSuccessful) ApiResult.Success(Unit)
            else ApiResult.Error("Error: ${response.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun markAllAsRead(userId: Int): ApiResult<Unit> {
        return try {
            val response = apiService.markAllAsRead(userId)
            if (response.isSuccessful) ApiResult.Success(Unit)
            else ApiResult.Error("Error: ${response.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun deleteNotification(userId: Int, notificationId: Int): ApiResult<Unit> {
        return try {
            val response = apiService.deleteNotification(userId, notificationId)
            if (response.isSuccessful) ApiResult.Success(Unit)
            else ApiResult.Error("Error: ${response.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun deleteAllNotifications(userId: Int): ApiResult<Unit> {
        return try {
            val response = apiService.deleteAllNotifications(userId)
            if (response.isSuccessful) ApiResult.Success(Unit)
            else ApiResult.Error("Error: ${response.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }
}
