package com.moviles.unaplanner.data

import android.content.Context
import com.moviles.unaplanner.core.NetworkMonitor
import com.moviles.unaplanner.data.local.UNAPlannerDatabase
import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.AuthRepository

object AppContainer {
    private val apiService = RetrofitClient.apiService

    val authRepository: AuthRepository by lazy { AuthRepository(apiService) }
    val adminRepository: AdminRepository by lazy { AdminRepository(apiService) }
}
