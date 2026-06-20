package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @GET(AppConstants.Api.Paths.USER_PROFILE)
    suspend fun getProfile(@Path("id") id: Int): Response<UserDto>

    @PUT(AppConstants.Api.Paths.USER_PROFILE)
    suspend fun updateProfile(@Path("id") id: Int, @Body request: UpdateProfileRequest): Response<UserDto>

    @POST(AppConstants.Api.Paths.CHANGE_PASSWORD)
    suspend fun changePassword(@Path("id") id: Int, @Body request: ChangePasswordRequest): Response<Unit>

    @GET(AppConstants.Api.Paths.ADMIN_CAREERS)
    suspend fun getCareers(): Response<List<Career>>

    @POST(AppConstants.Api.Paths.ADMIN_CAREERS)
    suspend fun createCareer(@Body request: CreateCareerRequest): Response<Career>

    @PUT(AppConstants.Api.Paths.ADMIN_CAREER_OPERATIONS)
    suspend fun updateCareer(
        @Path("id") id: Int,
        @Body request: UpdateCareerRequest
    ): Response<Career>

    @GET(AppConstants.Api.Paths.STUDY_PLAN_DETAIL)
    suspend fun getStudyPlanDetail(@Path("id") id: Int): Response<StudyPlanDetail>

    @GET(AppConstants.Api.Paths.STUDENT_NOTES)
    suspend fun getStudentNotes(@Path("id") userId: Int): Response<NotesResponse>

    @POST(AppConstants.Api.Paths.STUDENT_NOTES)
    suspend fun createNote(
        @Path("id") userId: Int,
        @Body request: CreateNoteRequest
    ): Response<NoteDto>

    @GET(AppConstants.Api.Paths.STUDENT_COURSES)
    suspend fun getStudentCourses(@Path("id") userId: Int): Response<CoursesResponse>

    @DELETE(AppConstants.Api.Paths.NOTE_OPERATIONS)
    suspend fun deleteNote(
        @Path("id") noteId: Int,
        @Query("userId") userId: Int
    ): Response<Unit>

    @PUT(AppConstants.Api.Paths.NOTE_OPERATIONS)
    suspend fun updateNote(
        @Path("id") noteId: Int,
        @Body request: UpdateNoteRequest
    ): Response<NoteDto>
}
