package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.remote.model.LoginRequest
import com.moviles.unaplanner.data.remote.model.UserDto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST


interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

}