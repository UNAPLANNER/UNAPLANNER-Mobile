package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.CampusContact

class CampusContactsRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun getCampusContacts(): ApiResult<List<CampusContact>> {
        return try {
            val response = apiService.getCampusContacts()
            if (response.isSuccessful) {
                val contacts = response.body()
                if (contacts != null) {
                    ApiResult.Success(contacts)
                } else {
                    ApiResult.Error("No se encontraron contactos.")
                }
            } else {
                ApiResult.Error("Error al obtener contactos: ${response.code()}", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error de conexión. Inténtalo de nuevo.")
        }
    }

    suspend fun getCampusContact(id: Int): ApiResult<CampusContact> {
        return try {
            val response = apiService.getCampusContact(id)
            if (response.isSuccessful) {
                val contact = response.body()
                if (contact != null) {
                    ApiResult.Success(contact)
                } else {
                    ApiResult.Error("No se encontró el contacto.")
                }
            } else {
                ApiResult.Error("Error al obtener el contacto: ${response.code()}", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error de conexión. Inténtalo de nuevo.")
        }
    }
}
