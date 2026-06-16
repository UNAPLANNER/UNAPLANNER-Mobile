package com.moviles.unaplanner.data.remote

import com.moviles.unaplanner.core.AppConstants
import com.moviles.unaplanner.data.AuthSession
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para añadir el Token Bearer automáticamente
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        // Si tenemos un usuario en sesión con token, lo añadimos
        AuthSession.currentUser?.token?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        chain.proceed(requestBuilder.build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.Api.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val contactApiService: ContactApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.Api.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactApiService::class.java)
    }
}
