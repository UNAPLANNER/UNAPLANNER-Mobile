package com.moviles.unaplanner.core

import android.os.Build

object AppConstants {

    const val FCM_LOG_TAG = "FCM"
    object Api {
        /** Emulator reaches host machine at 10.0.2.2; change port to match your API. */
        private const val EMULATOR_URL = "http://10.0.2.2:5232/"
        //KEYNA Usando IP local
        //private const val PHYSICAL_DEVICE_URL = "http://192.168.100.128:5232/"
        //Adriana Usando IP local
        //private const val PHYSICAL_DEVICE_URL = "http://192.168.1.8:5232/"
        //REICHEL Usando IP local
        //private const val PHYSICAL_DEVICE_URL = "http://192.168.100.124:5232/"
        private const val PHYSICAL_DEVICE_URL = "http://192.168.100.64:5232/"
        //Natalia Usando IP local

        /**
         * Selecciona automáticamente la URL base.
         */
        val BASE_URL: String
            get() = if (isEmulator) com.moviles.unaplanner.core.AppConstants.Api.EMULATOR_URL else com.moviles.unaplanner.core.AppConstants.Api.PHYSICAL_DEVICE_URL

        /**
         * Lógica para detectar si la app está corriendo en un emulador.
         */
        private val isEmulator: Boolean
            get() = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.HARDWARE.contains("goldfish")
                    || Build.HARDWARE.contains("ranchu")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.PRODUCT.contains("sdk_google")
                    || Build.PRODUCT.contains("google_sdk")
                    || Build.PRODUCT.contains("sdk")
                    || Build.PRODUCT.contains("sdk_x86")
                    || Build.PRODUCT.contains("vbox86p")
                    || Build.PRODUCT.contains("emulator")
                    || Build.PRODUCT.contains("simulator")

        object Paths {
            const val AUTH_LOGIN = "api/auth/login"
            const val CAMPUS_CONTACTS = "api/campus-contacts"
            const val STUDENT_NOTES = "api/student/{id}/notes"
            const val STUDENT_COURSES = "api/student/{id}/courses"
            const val NOTE_OPERATIONS = "api/notes/{id}"
            const val CONTACT_OPERATIONS = "api/campus-contacts/{id}"
            const val USER_PROFILE = "api/profile/{id}"
            const val CHANGE_PASSWORD = "api/profile/{id}/change-password"
            const val REGISTER_STUDENT = "api/auth/register"

        }
    }
}
