package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.ContactApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.data.remote.model.ChangePasswordRequest
import com.moviles.unaplanner.data.remote.model.CreateCareerRequest
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.data.remote.model.UpdateCareerRequest
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UserDto

class AdminRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val contactApiService: ContactApiService = RetrofitClient.contactApiService
) {
    suspend fun getCampusContacts(): ApiResult<List<CampusContact>> {
        return try {
            val response = contactApiService.getCampusContacts()
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
            val response = contactApiService.getContactsByCampus(campusId)
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error("Error al obtener contactos por sede")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun getCareers(): ApiResult<List<Career>> {
        return try {
            val response = apiService.getCareers()
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error(careerErrorMessage(response.code()), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun createCareer(request: CreateCareerRequest): ApiResult<Career> {
        return try {
            val response = apiService.createCareer(request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string()
                ApiResult.Error(errorMsg ?: createCareerErrorMessage(response.code()), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun updateCareer(id: Int, request: UpdateCareerRequest): ApiResult<Career> {
        return try {
            val response = apiService.updateCareer(id, request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string()
                ApiResult.Error(errorMsg ?: updateCareerErrorMessage(response.code()), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    suspend fun getStudyPlanDetail(id: Int): ApiResult<StudyPlanDetail> {
        return try {
            val response = apiService.getStudyPlanDetail(id)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(studyPlanErrorMessage(response.code()), response.code())
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
                ApiResult.Error(profileErrorMessage(response.code()), response.code())
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
                ApiResult.Error(profileErrorMessage(response.code()), response.code())
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
                ApiResult.Error(passwordErrorMessage(response.code()), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun createCampusContact(contact: CampusContact): ApiResult<CampusContact> {
        return try {
            val response = contactApiService.createCampusContact(contact)
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

    suspend fun deleteCampusContact(id: Int): ApiResult<Unit> {
        return try {
            val response = contactApiService.deleteCampusContact(id)
            when (response.code()) {
                204 -> ApiResult.Success(Unit)
                404 -> ApiResult.Error("Este contacto ya no existe")
                401, 403 -> ApiResult.Error("No tienes permisos para realizar esta accion")
                else -> ApiResult.Error("Error al eliminar el contacto: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    /**
     * Updates an existing campus contact.
     * @param id The ID of the contact to update.
     * @param contact The updated contact data.
     */
    suspend fun updateCampusContact(id: Int, contact: CampusContact): ApiResult<CampusContact> {
        return try {
            val response = contactApiService.updateCampusContact(id, contact)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al actualizar contacto"
                ApiResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red")
        }
    }

    private fun profileErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Validacion fallida. Revisa el nombre, telefono y departamento."
            401, 403 -> "No tienes permisos para actualizar este perfil."
            404 -> "No se encontro el perfil del administrador."
            500 -> "Error del servidor al guardar el perfil."
            else -> "Error al procesar el perfil: $statusCode"
        }
    }

    private fun passwordErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Validacion fallida. Revisa los datos de contrasena."
            401 -> "La contrasena actual es incorrecta."
            403 -> "No tienes permisos para cambiar esta contrasena."
            500 -> "Error del servidor al cambiar la contrasena."
            else -> "Error al cambiar la contrasena: $statusCode"
        }
    }

    private fun careerErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            401, 403 -> "No tienes permisos para consultar las carreras."
            500 -> "Error del servidor al obtener las carreras."
            else -> "Error al obtener las carreras: $statusCode"
        }
    }

    private fun createCareerErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Revisa los datos de la carrera."
            401, 403 -> "No tienes permisos para crear carreras."
            404 -> "No se encontro el perfil administrativo."
            409 -> "Ya existe una carrera con esos datos."
            500 -> "Error del servidor al crear la carrera."
            else -> "Error al crear la carrera: $statusCode"
        }
    }

    private fun updateCareerErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Revisa los datos de la carrera."
            401, 403 -> "No tienes permisos para actualizar carreras."
            404 -> "No se encontro la carrera."
            409 -> "Ya existe una carrera con ese codigo."
            500 -> "Error del servidor al actualizar la carrera."
            else -> "Error al actualizar la carrera: $statusCode"
        }
    }

    private fun studyPlanErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            401, 403 -> "No tienes permisos para consultar el plan."
            404 -> "No se encontro el plan de estudios."
            500 -> "Error del servidor al obtener el plan."
            else -> "Error al obtener el plan: $statusCode"
        }
    }
}
