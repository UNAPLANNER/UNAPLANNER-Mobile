package com.moviles.unaplanner.data.remote.model

data class LoginRequest(
    val email: String,
    val password: String
)

/** Logged-in user; [id] is a Guid string from the API. */
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val token: String? = null
)