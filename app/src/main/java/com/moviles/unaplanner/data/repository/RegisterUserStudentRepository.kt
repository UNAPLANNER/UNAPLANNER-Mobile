package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.RegisterRequest
import com.moviles.unaplanner.data.remote.model.RegisterResponse

class RegisterUserStudentRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun registerUser(request: RegisterRequest): ApiResult<RegisterResponse> {
        return try {
            val response = apiService.registerUser(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error(UserMessages.Errors.SERVER_ERROR)
                }
            } else {
                ApiResult.Error("${UserMessages.Errors.GENERIC_ERROR}: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(UserMessages.Errors.CONNECTION_ERROR)
        }
    }
}