package com.moviles.unaplanner.data.remote.model
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

/** Logged-in user; [id] is an Int from the API. */
data class UserDto(
    @SerializedName("userId")
    val id: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("department")
    val department: String? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("role")
    val role: String? = null
)
