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
import com.moviles.unaplanner.ui.screens.MainScreen
import com.moviles.unaplanner.ui.screens.login.WelcomeScreen
import com.moviles.unaplanner.ui.screens.contact.detail.CampusContactsDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
        composable(
            route = AppDestinations.MAIN,
            arguments = listOf(navArgument("initialIndex") { 
                type = NavType.IntType
                defaultValue = 0 
            })
        ) { backStackEntry ->
            val initialIndex = backStackEntry.arguments?.getInt("initialIndex") ?: 0
            MainScreen(
                initialIndex = initialIndex,
                onLogout = {
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.MAIN) { inclusive = true }
                    }
                },
                onNavigateToContactDetail = { contactId ->
                    navController.navigate(AppDestinations.createContactDetailRoute(contactId))
                }
            )
        }

        // --- PANTALLA DE DETALLE DE CONTACTO ---
        composable(
            route = AppDestinations.CONTACT_DETAIL,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getInt("contactId") ?: 0
            CampusContactsDetailScreen(
                contactId = contactId,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.MAIN) { inclusive = true }
                    }
                },
                onNavigateToSection = { index ->
                    // Navega a la pantalla principal con el índice seleccionado
                    navController.navigate(AppDestinations.createMainRoute(index)) {
                        // Limpia el detalle de la pila para que no "vuelva" al detalle al dar atrás
                        popUpTo(AppDestinations.MAIN) { inclusive = true }
                    }
                }
            )
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