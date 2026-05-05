package com.moviles.unaplanner.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.unaplanner.ui.screens.login.LoginScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.moviles.unaplanner.ui.screens.login.WelcomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.WELCOME,
        modifier = Modifier.fillMaxSize()
    ) {
        // --- PANTALLA DE INICIO (WELCOME) ---
        composable(route = AppDestinations.WELCOME) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(AppDestinations.LOGIN)
                },
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                }
            )
        }

        // --- PANTALLA DE LOGIN ---
        composable(route = AppDestinations.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                },
                onBack = {
                    navController.popBackStack() // Vuelve a Welcome
                },
                onLoginClick = {
                    navController.navigate(AppDestinations.STUDENTS) {
                        popUpTo(AppDestinations.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // --- PANTALLA DE REGISTRO ---
        composable(route = AppDestinations.REGISTER) {
            // Aquí llamarías a tu RegisterScreen pasándole el navController.popBackStack() para el "volver"
        }

        // --- PANTALLA DE ESTUDIANTES / SALONES ---
        composable(route = AppDestinations.STUDENTS) {
            // Tu pantalla de laboratorios previos (Laboratorio 4/5)
        }
    }
}


