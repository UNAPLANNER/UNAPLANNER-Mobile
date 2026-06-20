package com.moviles.unaplanner.data.repository

import com.moviles.unaplanner.data.remote.CurriculumApiService
import com.moviles.unaplanner.data.remote.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurriculumRepository(private val apiService: CurriculumApiService) {

    suspend fun getStudentProfile(userId: Int): Result<com.moviles.unaplanner.data.remote.StudentProfileDto> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(apiService.getStudentProfile(userId))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getCareers(): Result<List<CareerDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getCareers())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentCurriculum(studentId: Int): Result<StudentCurriculumResponse> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(apiService.getStudentCurriculum(studentId))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getCareerCurriculum(
        careerId: Int,
        userId: Int? = null
    ): Result<List<CurriculumLevelDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getCareerCurriculum(careerId, userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentCurriculumCourses(
        studentId: Int
    ): Result<List<StudentCourseProgressDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getStudentCurriculumCourses(studentId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCourseStatus(
        studentId: Int,
        courseId: Int,
        request: UpdateCourseStatusRequest
    ): Result<StudentCourseProgressDto> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.updateCourseStatus(studentId, courseId, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getStudentGpa(studentId: Int): ApiResult<GpaResponseDto> {
        return try {
            val response = apiService.getStudentGpa(studentId)
            if (response.isSuccessful) {
                val gpaData = response.body()
                if (gpaData != null) {
                    ApiResult.Success(gpaData)
                } else {
                    ApiResult.Error("Respuesta vacía del servidor.")
                }
            } else {
                ApiResult.Error("Error del servidor: ${response.code()}", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("No se pudo conectar al servidor.")
        }
    }


}
