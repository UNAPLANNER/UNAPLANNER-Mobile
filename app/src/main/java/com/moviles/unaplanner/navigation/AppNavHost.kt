package com.moviles.unaplanner.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
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
import com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin.CreateCampusContactScreen
import com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin.EditCampusContactScreen
import com.moviles.unaplanner.ui.screens.notes.NoteEditorScreen
import com.moviles.unaplanner.ui.screens.notes.NotesViewModel
import com.moviles.unaplanner.ui.screens.register.RegisterScreen
import com.moviles.unaplanner.ui.screens.calendar.AddActivityScreen
import com.moviles.unaplanner.ui.screens.calendar.StudentCalendarViewModel
import com.moviles.unaplanner.ui.screens.malla.MallaViewModel
import com.moviles.unaplanner.ui.screens.malla.CourseDetailViewModel
import com.moviles.unaplanner.ui.screens.malla.CourseDetailScreen
import com.moviles.unaplanner.ui.screens.progress.AcademicProgressScreen
import com.moviles.unaplanner.ui.screens.progress.ProgressViewModel
import com.moviles.unaplanner.ui.screens.notifications.NotificationScreen
import com.moviles.unaplanner.ui.screens.notifications.NotificationViewModel
import com.moviles.unaplanner.data.AppContainer


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val notesViewModel: NotesViewModel = viewModel()

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NotificationViewModel(AppContainer.notificationRepository) as T
            }
        }
    )

    val calendarViewModel: StudentCalendarViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return StudentCalendarViewModel(
                    AppContainer.calendarRepository,
                    AppContainer.networkMonitor,
                    AppContainer.curriculumRepository
                ) as T
            }
        }
    )

    val mallaViewModel: MallaViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MallaViewModel(AppContainer.curriculumRepository) as T
            }
        }
    )

    val progressViewModel: ProgressViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProgressViewModel(AppContainer.curriculumRepository) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = AppDestinations.WELCOME,
        modifier = Modifier.fillMaxSize()
    ) {
        // --- HOME SCREEN (WELCOME) ---
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

        // --- LOGIN SCREEN---
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

        // --- REGISTER SCREEN (PLACEHOLDER) ---
        composable(route = AppDestinations.REGISTER) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pantalla de Registro (Próximamente)")
            }
        }

        // --- MAIN SCREEN (WITH BOTTOM NAV) ---
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
                    AppContainer.authRepository.clearLocalSession()
                    mallaViewModel.clearForNewSession()
                    progressViewModel.clearForNewSession()
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
                onNavigateToAddActivity = {
                    navController.navigate(AppDestinations.ADD_ACTIVITY)
                },
                onNavigateToEditActivity = { eventId ->
                    navController.navigate(AppDestinations.createEditActivityRoute(eventId))
                },
                onNavigateToProgreso = {
                    navController.navigate(AppDestinations.PROGRESO)
                },
                onNavigateToNotifications = {
                    // Now handled internally in MainScreen as an overlay
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate(AppDestinations.createCourseDetailRoute(courseId))
                },
                notesViewModel = notesViewModel,
                calendarViewModel = calendarViewModel,
                mallaViewModel = mallaViewModel,
                notificationViewModel = notificationViewModel
            )
        }

        // --- MAIN ADMIN SCREEN (TAB CONTAINER)---
        composable(route = AppDestinations.ADMIN_MAIN) {
            AdminMainScreen(
                navController = navController,
                onLogout = {
                    AppContainer.authRepository.clearLocalSession()
                    navController.navigate(AppDestinations.WELCOME) {
                        popUpTo(AppDestinations.ADMIN_MAIN) { inclusive = true }
                    }
                },
                onNavigateToCreateContact = {
                    navController.navigate(AppDestinations.CREATE_CONTACT)
                },
                onNavigateToEditContact = { contact ->
                    navController.navigate(AppDestinations.createEditContactRoute(contact.id))
                }
            )
        }

        // --- CONTACT DETAILS SCREEN ---
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
                    // Navigate to the main screen with the selected index
                    navController.navigate(AppDestinations.createMainRoute(index)) {
                        // Clean the battery detail so that it doesn't "return" to the detail when you back out.
                        popUpTo(AppDestinations.MAIN) { inclusive = true }
                    }
                }
            )
        }

        // --- NOTE EDITING/CREATION SCREEN ---
        composable(
            route = AppDestinations.NOTE_EDIT,
            arguments = listOf(
                androidx.navigation.navArgument("noteId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("courseId") {
                    type = androidx.navigation.NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            val preselectedCourseId = backStackEntry.arguments?.getInt("courseId")?.takeIf { it > 0 }
            NoteEditorScreen(
                noteId = noteId,
                preselectedCourseId = preselectedCourseId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = notesViewModel
            )
        }

        // --- CONTACT CREATION SCREEN (ADMIN) ---
        composable(route = AppDestinations.CREATE_CONTACT) {
            CreateCampusContactScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("contact_created", true)
                    navController.popBackStack()
                }
            )
        }

        // --- CONTACT EDITING SCREEN (ADMIN) ---
        composable(
            route = AppDestinations.EDIT_CONTACT,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getInt("contactId") ?: 0
            EditCampusContactScreen(
                contactId = contactId,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("contact_updated", true)
                    navController.popBackStack()
                }
            )
        }


        // --- Register Student Screen ---
        composable(route = AppDestinations.REGISTER) {
            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(AppDestinations.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // --- ADD ACTIVITY SCREEN ---
        composable(route = AppDestinations.ADD_ACTIVITY) {
            AddActivityScreen(
                onBack = { navController.popBackStack() },
                viewModel = calendarViewModel
            )
        }

        // --- EDIT ACTIVITY SCREEN ---
        composable(
            route = AppDestinations.EDIT_ACTIVITY,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: 0
            AddActivityScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                viewModel = calendarViewModel
            )
        }
        // --- ACADEMIC PROGRESS SCREEN ---
        composable(route = AppDestinations.PROGRESO) {
            AcademicProgressScreen(
                viewModel = progressViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- NOTIFICATIONS LIST SCREEN ---
        composable(
            route = AppDestinations.NOTIFICATIONS_LIST,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 400)
                ) + fadeIn(animationSpec = tween(durationMillis = 400))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 400)
                ) + fadeOut(animationSpec = tween(durationMillis = 400))
            }
        ) {
            NotificationScreen(
                viewModel = notificationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        // --- COURSE DETAIL SCREEN ---
        composable(
            route = AppDestinations.COURSE_DETAIL,
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            val courseDetailViewModel: CourseDetailViewModel = viewModel(
                key = "course_detail_$courseId",
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CourseDetailViewModel(AppContainer.curriculumRepository) as T
                    }
                }
            )
            CourseDetailScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() },
                onNavigateToNoteEdit = { noteId ->
                    if (noteId == null) {
                        navController.navigate(AppDestinations.createNoteWithCourseRoute(courseId))
                    } else {
                        navController.navigate(AppDestinations.createNoteEditRoute(noteId))
                    }
                },
                viewModel = courseDetailViewModel
            )
        }
    }
}