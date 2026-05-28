package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.remote.model.CampusContact
import retrofit2.Response
import retrofit2.http.*

interface ContactApiService {
    @GET(AppConstants.Api.Paths.CAMPUS_CONTACTS)
    suspend fun getCampusContacts(): Response<List<CampusContact>>

    @POST("api/campus-contacts")
    suspend fun createCampusContact(@Body contact: CampusContact): Response<CampusContact>

    @GET("api/campus-contacts/campus/{campusId}")
    suspend fun getContactsByCampus(@Path("campusId") campusId: Int): Response<List<CampusContact>>

    @GET("${AppConstants.Api.Paths.CAMPUS_CONTACTS}/{id}")
    suspend fun getCampusContact(@Path("id") id: Int): Response<CampusContact>

    @DELETE(AppConstants.Api.Paths.CONTACT_OPERATIONS)
    suspend fun deleteCampusContact(@Path("id") id: Int): Response<Unit>

    @PUT("${AppConstants.Api.Paths.CAMPUS_CONTACTS}/{id}")
    suspend fun updateCampusContact(@Path("id") id: Int, @Body contact: CampusContact): Response<CampusContact>
}
