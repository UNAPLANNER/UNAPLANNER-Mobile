package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @GET(AppConstants.Api.Paths.CAMPUS_CONTACTS)
    suspend fun getCampusContacts(): Response<List<CampusContact>>

    @GET("api/campus-contacts/campus/{campusId}")
    suspend fun getContactsByCampus(@Path("campusId") campusId: Int): Response<List<CampusContact>>

    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") id: Int): Response<UserDto>

    @PUT("api/profile/{id}")
    suspend fun updateProfile(@Path("id") id: Int, @Body request: UpdateProfileRequest): Response<UserDto>

    @POST("api/profile/{id}/change-password")
    suspend fun changePassword(@Path("id") id: Int, @Body request: ChangePasswordRequest): Response<Unit>
}
