package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.AuthRepository
import com.moviles.unaplanner.data.repository.CurriculumRepository
import com.moviles.unaplanner.data.repository.StudentCalendarRepository

object AppContainer {
    private val apiService = RetrofitClient.apiService
    private val calendarApiService = RetrofitClient.calendarApiService
    private val curriculumApiService = RetrofitClient.curriculumApiService

    val authRepository: AuthRepository by lazy { AuthRepository(apiService) }
    val adminRepository: AdminRepository by lazy { AdminRepository(apiService) }
    val calendarRepository: StudentCalendarRepository by lazy { StudentCalendarRepository(calendarApiService) }
    val curriculumRepository: CurriculumRepository by lazy { CurriculumRepository(curriculumApiService) }
}
