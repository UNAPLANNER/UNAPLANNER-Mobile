package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.remote.model.FcmTokenRequest
import com.moviles.unaplanner.data.remote.model.NotificationDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApiService {

    @POST(AppConstants.Api.Paths.DEVICE_TOKEN)
    suspend fun registerDeviceToken(
        @Path("userId") userId: Int,
        @Body request: FcmTokenRequest
    ): Response<Unit>

    @GET(AppConstants.Api.Paths.NOTIFICATIONS)
    suspend fun getNotifications(
        @Path("userId") userId: Int
    ): Response<List<NotificationDto>>

    @PATCH(AppConstants.Api.Paths.NOTIFICATION_READ)
    suspend fun markAsRead(
        @Path("userId") userId: Int,
        @Path("notificationId") notificationId: Int
    ): Response<Unit>

    @PATCH(AppConstants.Api.Paths.NOTIFICATIONS_READ_ALL)
    suspend fun markAllAsRead(
        @Path("userId") userId: Int
    ): Response<Unit>

    @DELETE(AppConstants.Api.Paths.NOTIFICATION_DELETE)
    suspend fun deleteNotification(
        @Path("userId") userId: Int,
        @Path("notificationId") notificationId: Int
    ): Response<Unit>

    @DELETE(AppConstants.Api.Paths.NOTIFICATIONS_DELETE_ALL)
    suspend fun deleteAllNotifications(
        @Path("userId") userId: Int
    ): Response<Unit>
}
