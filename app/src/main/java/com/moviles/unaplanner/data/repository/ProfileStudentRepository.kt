package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.model.StudentsProfileDto
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UpdateStudentProfileRequest
import com.moviles.unaplanner.data.remote.model.UserDto

class ProfileStudentRepository(
    private val api: ApiService
) {

    // En ProfileStudentRepository.kt
    suspend fun updateProfile(
        userId: Int,
        request: UpdateStudentProfileRequest
    ): StudentsProfileDto {

        // ← TEMPORAL: ver qué se envía
        android.util.Log.d("UPDATE_PROFILE", "userId=$userId request=$request")

        val response = api.updateProfileStudent(userId, request)

        android.util.Log.d("UPDATE_PROFILE", "code=${response.code()}")

        if (response.isSuccessful) {
            return response.body()?.data
                ?: throw Exception("Respuesta vacía del servidor")
        } else {
            throw Exception("Error ${response.code()} al actualizar perfil")
        }
    }
}