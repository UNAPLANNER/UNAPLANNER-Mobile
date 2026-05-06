package com.moviles.unaplanner.data

import com.moviles.unaplanner.data.remote.RetrofitClient
import com.moviles.unaplanner.data.repository.AuthRepository

object AppContainer {
    private val apiService = RetrofitClient.apiService

    val authRepository: AuthRepository by lazy { AuthRepository(apiService) }
}