package com.moviles.unaplanner.data.repository

import com.google.gson.JsonParseException
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.StudentSession
import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.LoginRequest
import com.moviles.unaplanner.data.remote.model.StudentsProfileDto
import com.moviles.unaplanner.data.remote.model.UserDto
class AuthRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    fun clearLocalSession() {
        AuthSession.clear()
    }
    suspend fun login(email: String, password: String): ApiResult<UserDto> {
        return try {
            // Verifica que el nombre del parámetro (contrasena/password) sea el de tu data class
            val response = apiService.login(LoginRequest(email = email, password = password))

            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {

                    android.util.Log.d("LOGIN_RESPONSE", "user=$user")
                    AuthSession.setUser(user)
                    if (user.role?.lowercase() != "admin") {
                        try {
                            val profileResponse = apiService.getProfileStudent(user.id)
                            if (profileResponse.isSuccessful && profileResponse.body() != null) {
                                StudentSession.setProfile(profileResponse.body()!!)
                            } else {
                                StudentSession.setProfile(
                                    StudentsProfileDto(
                                        studentId = user.id,
                                        email = user.email,
                                        fullName = "",
                                        careerId = 0,
                                        careerName = "",
                                        enterYear = null
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            StudentSession.setProfile(
                                StudentsProfileDto(
                                    studentId  = user.id,
                                    email      = user.email,
                                    fullName   = "",
                                    careerId   = 0,
                                    careerName = "",
                                    enterYear  = null
                                )
                            )
                        }
                    }
                    ApiResult.Success(user)
                } else {
                    ApiResult.Error("El servidor respondió sin datos de usuario.")
                }
            } else {
                // Manejo de códigos de error específicos
                val msg = when (response.code()) {
                    401 -> "Correo o contraseña incorrectos."
                    403 -> "Acceso prohibido."
                    else -> "Error del servidor: ${response.code()}"
                }
                ApiResult.Error(msg, response.code())
            }
        } catch (e: JsonParseException) {
            ApiResult.Error("Error al procesar la información del servidor.")
        } catch (e: Exception) {
            ApiResult.Error("No se pudo conectar. Revisa tu conexión a internet.")
        }
    }

}

