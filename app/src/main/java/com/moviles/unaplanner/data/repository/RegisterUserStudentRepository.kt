package com.moviles.unaplanner.data.repository

import android.util.Log
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.RegisterRequest
import com.moviles.unaplanner.data.remote.model.RegisterResponse
import org.json.JSONObject

class RegisterUserStudentRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun registerUser(
        request: RegisterRequest
    ): ApiResult<RegisterResponse> {

        return try {

            Log.d("REGISTER_REQUEST", request.toString())

            val response = apiService.registerUser(request)

            Log.d("REGISTER_CODE", response.code().toString())

            if (response.isSuccessful) {

                val body = response.body()

                Log.d("REGISTER_SUCCESS", body.toString())

                if (body != null) {

                    ApiResult.Success(body)

                } else {

                    ApiResult.Error(
                        UserMessages.Errors.SERVER_ERROR
                    )
                }

            } else {

                val errorBody =
                    response.errorBody()?.string()

                Log.e(
                    "REGISTER_ERROR",
                    errorBody ?: "Error vacío"
                )
                val errorMessage = try {
                    JSONObject(errorBody ?: "")
                        .getString("message")
                } catch (e: Exception) {
                    UserMessages.Errors.GENERIC_ERROR
                }

                ApiResult.Error(errorMessage)

            }

        } catch (e: Exception) {

            Log.e(
                "REGISTER_EXCEPTION",
                e.stackTraceToString()
            )

            ApiResult.Error(
                e.localizedMessage
                    ?: UserMessages.Errors.CONNECTION_ERROR
            )
        }
    }
}