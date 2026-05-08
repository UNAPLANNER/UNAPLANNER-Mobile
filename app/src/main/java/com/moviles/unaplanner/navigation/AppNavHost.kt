package com.moviles.unaplanner.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.unaplanner.ui.screens.login.LoginScreen
import com.moviles.unaplanner.ui.screens.MainScreen
import com.moviles.unaplanner.ui.screens.login.WelcomeScreen
import com.moviles.unaplanner.ui.screens.admin.AdminMainScreen
import com.moviles.unaplanner.ui.screens.admin.profile.AdminProfileScreen
import com.moviles.unaplanner.ui.screens.contact.detail.CampusContactsDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.moviles.unaplanner.ui.screens.notes.NoteEditorScreen
import com.moviles.unaplanner.ui.screens.notes.NotesViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val notesViewModel: NotesViewModel = viewModel()

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
                },
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate(AppDestinations.createNoteEditRoute(noteId))
                },
                notesViewModel = notesViewModel
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

        // --- PANTALLA DE EDICIÓN/CREACIÓN DE NOTA ---
        composable(route = AppDestinations.NOTE_EDIT) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NoteEditorScreen(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = notesViewModel
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
        }*/

    }
}
