package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @GET("api/auth/me")
    suspend fun getMe(): Response<UserDto>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET(AppConstants.Api.Paths.USER_PROFILE)
    suspend fun getProfile(@Path("id") id: Int): Response<UserDto>

    @PUT(AppConstants.Api.Paths.USER_PROFILE)
    suspend fun updateProfile(@Path("id") id: Int, @Body request: UpdateProfileRequest): Response<UserDto>

    @POST(AppConstants.Api.Paths.CHANGE_PASSWORD)
    suspend fun changePassword(@Path("id") id: Int, @Body request: ChangePasswordRequest): Response<Unit>

    @GET(AppConstants.Api.Paths.STUDENT_NOTES)
    suspend fun getStudentNotes(
        @Path("id") studentId: Int,
        @Query("courseId") courseId: Int? = null
    ): Response<NotesResponse>

    @POST(AppConstants.Api.Paths.STUDENT_NOTES)
    suspend fun createNote(
        @Path("id") studentId: Int,
        @Body request: CreateNoteRequest
    ): Response<NoteDto>

    @GET(AppConstants.Api.Paths.STUDENT_COURSES)
    suspend fun getStudentCourses(@Path("id") studentId: Int): Response<CoursesResponse>

    @GET("api/student/{id}/curriculum/courses")
    suspend fun getStudentCurriculumCourses(@Path("id") studentId: Int): Response<List<StudentCourseProgressDto>>

    @DELETE(AppConstants.Api.Paths.NOTE_OPERATIONS)
    suspend fun deleteNote(
        @Path("id") noteId: Int
    ): Response<Unit>

    @PUT(AppConstants.Api.Paths.NOTE_OPERATIONS)
    suspend fun updateNote(
        @Path("id") noteId: Int,
        @Body request: UpdateNoteRequest
    ): Response<NoteDto>

    @POST(AppConstants.Api.Paths.REGISTER_STUDENT)
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET(AppConstants.Api.Paths.CAMPUS_LIST)
    suspend fun getCampuses(): Response<List<CampusDto>>

    @GET(AppConstants.Api.Paths.CAMPUS_CAREERS)
    suspend fun getCampusCareers(
        @Path("campusId") campusId: Int
    ): Response<List<CampusCareerDto>>
}
