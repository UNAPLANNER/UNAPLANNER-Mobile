package com.moviles.unaplanner.data.repository

import android.util.Log
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.remote.ApiService
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.remote.model.RegisterRequest
import com.moviles.unaplanner.data.remote.model.RegisterResponse

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

                if (body != null && body.isSuccess) {
                    ApiResult.Success(body)
                } else if (body != null) {
                    ApiResult.Error(body.message.ifBlank { UserMessages.Errors.SERVER_ERROR })
                } else {
                    ApiResult.Error(UserMessages.Errors.SERVER_ERROR)
                }

            } else {

                val errorBody = response.errorBody()?.string()

                Log.e("REGISTER_ERROR", errorBody ?: "Error vacío")

                val errorMessage = try {
                    val json = org.json.JSONObject(errorBody ?: "{}")
                    // ASP.NET Core ModelState errors: { "errors": { "Email": ["msg"] } }
                    val errorsObj = json.optJSONObject("errors")
                    if (errorsObj != null && errorsObj.length() > 0) {
                        val firstKey = errorsObj.keys().next()
                        val firstArray = errorsObj.optJSONArray(firstKey)
                        firstArray?.optString(0)
                            ?: json.optString("title")
                    } else {
                        // Custom error: { "message": "..." } or { "Message": "..." }
                        json.optString("message").ifEmpty { json.optString("Message") }
                            .ifEmpty { json.optString("error") }
                            .ifEmpty { json.optString("title") }
                    }.ifEmpty { UserMessages.Errors.GENERIC_ERROR }
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