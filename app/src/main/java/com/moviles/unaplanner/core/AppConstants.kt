package com.moviles.unaplanner.core

object AppConstants {
    object Api {
        /** Emulator reaches host machine at 10.0.2.2; change port to match your API. */
        //const val BASE_URL = "http://10.0.2.2:5147/"
        // adriana
        val BASE_URL = "http://192.168.1.6:5232/"
        //KEYNA
        //val BASE_URL = "http://192.168.100.128:5232/"
        object Paths {
            const val AUTH_LOGIN = "api/auth/login"
        }
    }
}