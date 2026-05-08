package com.moviles.unaplanner.navigation

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
import com.moviles.unaplanner.ui.screens.notes.NoteEditorScreen
import com.moviles.unaplanner.ui.screens.notes.NotesViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val notesViewModel: NotesViewModel = viewModel()

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
                },
                notesViewModel = notesViewModel
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
        }*/

    }
}