package com.moviles.unaplanner.data

import android.content.Context
import com.moviles.unaplanner.core.NetworkMonitor
import com.moviles.unaplanner.data.local.UNAPlannerDatabase
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.AuthRepository
import com.moviles.unaplanner.data.repository.CurriculumRepository
import com.moviles.unaplanner.data.repository.EvaluationRepository
import com.moviles.unaplanner.data.repository.NotificationRepository
import com.moviles.unaplanner.data.repository.StudentCalendarRepository

object AppContainer {
    private val apiService = RetrofitClient.apiService
    private val calendarApiService = RetrofitClient.calendarApiService
    private val curriculumApiService = RetrofitClient.curriculumApiService
    private val notificationApiService = RetrofitClient.notificationApiService
    private val evaluationApiService = RetrofitClient.evaluationApiService

    private lateinit var database: UNAPlannerDatabase

    lateinit var networkMonitor: NetworkMonitor
        private set

    fun init(context: Context) {
        database = UNAPlannerDatabase.getInstance(context)
        networkMonitor = NetworkMonitor(context)
    }

    val authRepository: AuthRepository by lazy { AuthRepository(apiService) }
    val adminRepository: AdminRepository by lazy { AdminRepository(apiService) }
    val calendarRepository: StudentCalendarRepository by lazy {
        StudentCalendarRepository(calendarApiService, database.calendarEventDao())
    }
    val curriculumRepository: CurriculumRepository by lazy { CurriculumRepository(curriculumApiService) }
    val notificationRepository: NotificationRepository by lazy { NotificationRepository(notificationApiService) }
    val evaluationRepository: EvaluationRepository by lazy { EvaluationRepository(evaluationApiService) }
}
