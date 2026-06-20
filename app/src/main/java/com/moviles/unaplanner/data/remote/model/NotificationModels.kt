package com.moviles.unaplanner.data.remote.model

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("deviceName")
    val deviceName: String? = null
)

data class NotificationDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("relatedId")
    val relatedId: Int? = null,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("createdDate")
    val createdDate: String,
    @SerializedName("sentDate")
    val sentDate: String? = null
)
