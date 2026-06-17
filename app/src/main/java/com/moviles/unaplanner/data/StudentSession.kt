package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.model.StudentProfileDto

object StudentSession {

    @Volatile
    var profile: StudentProfileDto? = null
        private set

    fun setProfile(p: StudentProfileDto) {
        profile = p
    }

    fun clear() {
        profile = null
    }
}