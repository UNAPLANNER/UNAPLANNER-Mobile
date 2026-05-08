package com.moviles.unaplanner.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.unaplanner.ui.screens.notes.login.LoginScreen
import com.moviles.unaplanner.ui.screens.MainScreen
import com.moviles.unaplanner.ui.screens.notes.login.WelcomeScreen

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
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(AppDestinations.MAIN) {
                        popUpTo(AppDestinations.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // --- PANTALLA PRINCIPAL (CON BOTTOM NAV) ---
        composable(route = AppDestinations.MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.MAIN) { inclusive = true }
                    }
                },
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate(AppDestinations.createNoteEditRoute(noteId))
                }
            )
        }

        // --- PANTALLA DE EDICIÓN DE NOTA ---
        composable(route = AppDestinations.NOTE_EDIT) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            // Aquí iría la pantalla de edición, por ahora un placeholder o pantalla básica
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Editando nota ID: $noteId")
            }
        }

        // --- PANTALLA DE REGISTRO ---
        /*composable(route = AppDestinations.REGISTER) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(AppDestinations.LOGIN)
                }
            )
        }*/

    }
}