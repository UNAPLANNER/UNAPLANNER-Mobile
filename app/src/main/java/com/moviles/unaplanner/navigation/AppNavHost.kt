package com.moviles.unaplanner.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.unaplanner.ui.screens.login.LoginScreen
import com.moviles.unaplanner.ui.screens.MainScreen
import com.moviles.unaplanner.ui.screens.login.WelcomeScreen
import com.moviles.unaplanner.ui.screens.admin.AdminMainScreen
import com.moviles.unaplanner.ui.screens.admin.profile.AdminProfileScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.LOGIN,
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
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    // Logic to distinguish admin vs student could be here
                    // For now, it goes to MAIN (student)
                    navController.navigate(AppDestinations.MAIN) {
                        popUpTo(AppDestinations.WELCOME) { inclusive = true }
                    }
                },
                onNavigateToAdminHome = {
                    navController.navigate(AppDestinations.ADMIN_MAIN) {
                        popUpTo(AppDestinations.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // --- PANTALLA PRINCIPAL ESTUDIANTE (CON BOTTOM NAV) ---
        composable(route = AppDestinations.MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.MAIN) { inclusive = true }
                    }
                }
            )
        }

        // --- PANTALLA PRINCIPAL ADMIN (CONTENEDOR DE PESTAÑAS) ---
        composable(route = AppDestinations.ADMIN_MAIN) {
            AdminMainScreen(
                onLogout = {
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.ADMIN_MAIN) { inclusive = true }
                    }
                }
            )
        }

        // --- PANTALLA DE PERFIL DE ADMIN ---
        composable(route = AppDestinations.ADMIN_PROFILE) {
            AdminProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.ADMIN_PROFILE) { inclusive = true }
                    }
                }
            )
        }

    }
}
