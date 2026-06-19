package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

// Minimal student DTO to resolve careerId when it's missing from the login response
data class StudentProfileDto(
    val studentId: Int? = null,
    val userId: Int? = null,
    val careerId: Int? = null,
    val studyPlanId: Int? = null
)

interface CurriculumApiService {

    @GET("api/student/{id}/profile")
    suspend fun getStudentProfile(@Path("id") userId: Int): StudentProfileDto

    @GET("api/careers")
    suspend fun getCareers(): List<CareerDto>

    @GET("api/careers/{id}/curriculum")
    suspend fun getCareerCurriculum(
        @Path("id") careerId: Int,
        @Query("userId") userId: Int? = null
    ): List<CurriculumLevelDto>

    @GET("api/student/{id}/curriculum")
    suspend fun getStudentCurriculum(
        @Path("id") studentId: Int
    ): StudentCurriculumResponse

    @GET("api/student/{id}/curriculum/courses")
    suspend fun getStudentCurriculumCourses(
        @Path("id") studentId: Int
    ): List<StudentCourseProgressDto>

    @PUT("api/student/{id}/courses/{courseId}")
    suspend fun updateCourseStatus(
        @Path("id") studentId: Int,
        @Path("courseId") courseId: Int,
        @Body request: UpdateCourseStatusRequest
    ): StudentCourseProgressDto

    @GET("api/student/{id}/gpa")
    suspend fun getStudentGpa(
        @Path("id") studentId: Int
    ): Response<GpaResponseDto>
}
