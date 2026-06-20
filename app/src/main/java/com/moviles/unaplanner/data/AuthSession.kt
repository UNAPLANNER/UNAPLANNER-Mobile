package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.model.UserDto

object AuthSession {
    @Volatile
    var currentUser: UserDto? = null
        private set

    /** Student ID from the JWT. Only non-null for student accounts. */
    val studentId: Int? get() = currentUser?.studentId

    fun setUser(user: UserDto) {
        currentUser = user
    }

    fun clear() {
        currentUser = null
    }
}
