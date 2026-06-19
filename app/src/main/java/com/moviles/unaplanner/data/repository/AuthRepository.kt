package com.moviles.unaplanner.data.repository

import com.google.gson.JsonParseException
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.GpaResponseDto
import com.moviles.unaplanner.data.remote.model.LoginRequest
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
                    AuthSession.setUser(user)
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

