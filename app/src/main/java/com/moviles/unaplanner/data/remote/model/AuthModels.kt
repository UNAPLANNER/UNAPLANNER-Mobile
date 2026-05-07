package com.moviles.unaplanner.data.remote.model
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

/** Logged-in user; [id] is a Guid string from the API. */
data class UserDto(
    @SerializedName("id", alternate = ["userId", "UserId"])
    val id: Int,
    val name: String,
    val email: String,
    val token: String? = null
)