package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.ChangePasswordRequest
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UserDto

class AdminRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun getProfile(id: Int): ApiResult<UserDto> {
        return try {
            val response = apiService.getProfile(id)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al obtener perfil")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun updateProfile(id: Int, request: UpdateProfileRequest): ApiResult<UserDto> {
        return try {
            val response = apiService.updateProfile(id, request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al actualizar perfil")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun changePassword(id: Int, request: ChangePasswordRequest): ApiResult<Unit> {
        return try {
            val response = apiService.changePassword(id, request)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Error al cambiar contraseña")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido")
        }
    }
}
