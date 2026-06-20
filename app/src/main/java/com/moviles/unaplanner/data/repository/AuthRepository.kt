package com.moviles.unaplanner.data.repository

import android.util.Base64
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
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

    /**
     * Decodes the JWT payload and extracts the StudentId claim.
     * The JWT is the authoritative source for studentId — it's set by the backend
     * when the token is signed and cannot be tampered with.
     */
    private fun extractStudentIdFromJwt(token: String?): Int? {
        if (token.isNullOrBlank()) return null
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val padded = payload.replace('-', '+').replace('_', '/')
            val decoded = Base64.decode(padded, Base64.DEFAULT)
            val json = JsonParser.parseString(String(decoded, Charsets.UTF_8)).asJsonObject
            // .NET adds claims with the key name passed to new Claim(key, value)
            (json["StudentId"] ?: json["studentId"] ?: json["student_id"])?.asInt
        } catch (_: Exception) {
            null
        }
    }

    private fun extractCareerIdFromJwt(token: String?): Int? {
        if (token.isNullOrBlank()) return null
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val padded = payload.replace('-', '+').replace('_', '/')
            val decoded = Base64.decode(padded, Base64.DEFAULT)
            val json = JsonParser.parseString(String(decoded, Charsets.UTF_8)).asJsonObject
            (json["CareerId"] ?: json["careerId"] ?: json["career_id"])?.asInt
        } catch (_: Exception) {
            null
        }
    }

    private fun extractStudyPlanIdFromJwt(token: String?): Int? {
        if (token.isNullOrBlank()) return null
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val padded = payload.replace('-', '+').replace('_', '/')
            val decoded = Base64.decode(padded, Base64.DEFAULT)
            val json = JsonParser.parseString(String(decoded, Charsets.UTF_8)).asJsonObject
            (json["StudyPlanId"] ?: json["studyPlanId"] ?: json["study_plan_id"])?.asInt
        } catch (_: Exception) {
            null
        }
    }

    suspend fun login(email: String, password: String): ApiResult<UserDto> {
        return try {
            val response = apiService.login(LoginRequest(email = email, password = password))

            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    // Parse claims directly from the JWT — these are authoritative
                    val jwtStudentId = extractStudentIdFromJwt(user.token)
                    val jwtCareerId = extractCareerIdFromJwt(user.token)
                    val jwtStudyPlanId = extractStudyPlanIdFromJwt(user.token)

                    // Enrich login user with JWT claims before calling /me
                    val userWithJwtClaims = user.copy(
                        studentId = jwtStudentId ?: user.studentId,
                        careerId = jwtCareerId ?: user.careerId,
                        studyPlanId = jwtStudyPlanId ?: user.studyPlanId
                    )
                    AuthSession.setUser(userWithJwtClaims)

                    // Call /me for supplementary data (fullName, etc.) not in JWT
                    try {
                        val meResponse = apiService.getMe()
                        if (meResponse.isSuccessful) {
                            val me = meResponse.body()
                            if (me != null) {
                                val enriched = me.copy(
                                    token = if (!me.token.isNullOrBlank()) me.token else user.token,
                                    // JWT claims are authoritative — don't let /me override them
                                    studentId = jwtStudentId ?: me.studentId ?: user.studentId,
                                    careerId = jwtCareerId ?: me.careerId ?: user.careerId,
                                    studyPlanId = jwtStudyPlanId ?: me.studyPlanId ?: user.studyPlanId
                                )
                                AuthSession.setUser(enriched)
                                return ApiResult.Success(enriched)
                            }
                        }
                    } catch (_: Exception) {
                        // /me failed — continue with JWT-enriched login data
                    }

                    ApiResult.Success(userWithJwtClaims)
                } else {
                    ApiResult.Error("El servidor respondió sin datos de usuario.")
                }
            } else {
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

    suspend fun logout() {
        try { apiService.logout() } catch (_: Exception) { }
        AuthSession.clear()
    }
}
