package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.model.StudentsProfileDto

object StudentSession {

    @Volatile
    var profile: StudentsProfileDto? = null
        private set

    fun setProfile(p: StudentsProfileDto) {
        profile = p
    }

    fun clear() {
        profile = null
    }
}