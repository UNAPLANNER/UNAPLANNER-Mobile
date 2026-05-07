package com.moviles.unaplanner.data.remote.model

data class UpdateProfileRequest(
    val fullName: String?,
    val phone: String?,
    val department: String?
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
