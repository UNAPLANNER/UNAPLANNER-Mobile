package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.remote.model.ChangePasswordRequest
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UserDto

class AdminRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun getCampusContacts(): ApiResult<List<CampusContact>> {
        return try {
            val response = apiService.getCampusContacts()
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error("Error al obtener contactos")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun getContactsByCampus(campusId: Int): ApiResult<List<CampusContact>> {
        return try {
            val response = apiService.getContactsByCampus(campusId)
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error("Error al obtener contactos por sede")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun getProfile(id: Int): ApiResult<UserDto> {
        return try {
            val response = apiService.getProfile(id)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                ApiResult.Error("Error ${response.code()}: $errorMsg")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
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

    suspend fun createCampusContact(contact: CampusContact): ApiResult<CampusContact> {
        return try {
            val response = apiService.createCampusContact(contact)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear contacto"
                ApiResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }
}
