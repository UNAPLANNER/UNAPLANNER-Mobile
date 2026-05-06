package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.model.UserDto

object AuthSession {
    @Volatile
    var currentUser: UserDto? = null
        private set

    fun setUser(user: UserDto) {
        currentUser = user
    }

    fun clear() {
        currentUser = null
    }
}