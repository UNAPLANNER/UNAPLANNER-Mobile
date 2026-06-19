package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.model.GPAStudentProfileDto

object GPAStudentSession {
    @Volatile
    var profile: GPAStudentProfileDto? = null
        private set

    fun setProfile(p: GPAStudentProfileDto) {
        profile = p
    }

    fun clear() {
        profile = null
    }
}