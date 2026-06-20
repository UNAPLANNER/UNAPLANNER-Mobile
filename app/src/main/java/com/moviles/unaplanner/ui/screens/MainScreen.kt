package com.moviles.unaplanner.ui.screens

import androidx.compose.ui.zIndex
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.screens.calendar.CalendarScreen
import com.moviles.unaplanner.ui.screens.calendar.StudentCalendarViewModel
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.ui.screens.contact.CampusContactsListScreen
import com.moviles.unaplanner.ui.screens.inicio.HomeScreen
import com.moviles.unaplanner.ui.screens.malla.MallaScreen
import com.moviles.unaplanner.ui.screens.malla.MallaViewModel
import com.moviles.unaplanner.ui.screens.notes.NotesScreen
import com.moviles.unaplanner.ui.screens.notes.NotesViewModel
import com.moviles.unaplanner.ui.screens.notifications.NotificationScreen
import com.moviles.unaplanner.ui.screens.notifications.NotificationViewModel
import com.moviles.unaplanner.ui.theme.CrimsonRed

@Composable
fun MainScreen(
    initialIndex: Int = 0,
    onLogout: () -> Unit,
    onNavigateToContactDetail: (Int) -> Unit,
    onNavigateToNoteEdit: (Int?) -> Unit,
    onNavigateToAddActivity: () -> Unit,
    onNavigateToEditActivity: (Int) -> Unit,
    onNavigateToProgreso: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCourseDetail: (Int) -> Unit = {},
    notesViewModel: NotesViewModel,
    calendarViewModel: StudentCalendarViewModel,
    mallaViewModel: MallaViewModel,
    notificationViewModel: NotificationViewModel
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
    var userState by remember { mutableStateOf(com.moviles.unaplanner.data.AuthSession.currentUser) }
    val careerName by mallaViewModel.careerName.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    
    var showNotifications by rememberSaveable { mutableStateOf(false) }

    // Handle the back button to close notifications
    if (showNotifications) {
        BackHandler {
            showNotifications = false
        }
    }

    // Load notes and courses when navigating to the notes tab
    LaunchedEffect(userState) {
        userState?.let { u ->
            // If the session is missing studentId or fullName, refresh from /me
            if (u.studentId == null || u.fullName.isNullOrBlank()) {
                try {
                    val meResponse = com.moviles.unaplanner.data.remote.RetrofitClient.apiService.getMe()
                    if (meResponse.isSuccessful) {
                        meResponse.body()?.let { me ->
                            val enriched = if (!me.token.isNullOrBlank()) me else me.copy(token = u.token)
                            // JWT-backed studentId from the current session is authoritative
                            val finalEnriched = enriched.copy(
                                studentId = u.studentId ?: enriched.studentId
                            )
                            com.moviles.unaplanner.data.AuthSession.setUser(finalEnriched)
                            userState = finalEnriched
                        }
                    }
                } catch (e: Exception) {
                }
            }
            val sid = com.moviles.unaplanner.data.AuthSession.studentId ?: return@LaunchedEffect
            mallaViewModel.loadStudentCurriculum(sid)
        }
    }

    LaunchedEffect(Unit) {
        notificationViewModel.loadNotifications()
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex == 3) {
            notesViewModel.loadNotes()
            notesViewModel.loadStudentCourses()
        }
    }

    val currentMonthYear = remember {
        java.time.LocalDate.now().let { date ->
            val month = date.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("es"))
            "${month.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("es")) else it.toString() }} ${date.year}"
        }
    }

    val titles = listOf(
        userState?.fullName ?: "Inicio",
        "Calendario",
        "Malla Curricular",
        "Mis Notas",
        "Directorio"
    )
    val subtitles = listOf(
        careerName ?: userState?.department ?: "Estudiante",
        currentMonthYear,
        careerName ?: userState?.department ?: "Escuela de Informática",
        "Apuntes por curso",
        when(userState?.campusId) {
            1 -> "Campus Omar Dengo · UNA"
            2 -> "Campus Benjamín Núñez · UNA"
            3 -> "Campus Pérez Zeledón · UNA"
            4 -> "Campus Liberia · UNA"
            5 -> "Campus Nicoya · UNA"
            6 -> "Campus Sarapiquí · UNA"
            else -> "Campus Sarapiquí · UNA"
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = titles[selectedIndex],
                    subtitle = subtitles[selectedIndex],
                    onLogout = onLogout,
                    onNotificationsClick = { showNotifications = true },
                    hasUnreadNotifications = unreadCount > 0,
                    action = if (selectedIndex == 3) {
                        {
                            Button(
                                onClick = { onNavigateToNoteEdit(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    "+ Nueva",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else null
                )
            },
            bottomBar = {
                AppBottomNavBar(
                    selectedIndex = selectedIndex,
                    onItemSelected = { selectedIndex = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedIndex) {
                    0 -> HomeScreen(
                        onNavigateToTab = { index -> selectedIndex = index },
                        onNavigateToProgreso = onNavigateToProgreso
                    )
                    1 -> CalendarScreen(
                        viewModel = calendarViewModel,
                        onAddActivity = onNavigateToAddActivity,
                        onEditActivity = onNavigateToEditActivity
                    )
                    2 -> MallaScreen(viewModel = mallaViewModel)
                    3 -> NotesScreen(onNavigateToEdit = onNavigateToNoteEdit, viewModel = notesViewModel)
                    4 -> CampusContactsListScreen(onContactClick = onNavigateToContactDetail)
                }
            }
        }

        // Overlay de Notificaciones (Top Sheet)
        if (showNotifications) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f)
            ) {
                // Dark background clickable to close
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { showNotifications = false }
                )

                // Animated Notification Panel
                AnimatedVisibility(
                    visible = showNotifications,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 300)),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    NotificationScreen(
                        viewModel = notificationViewModel,
                        onBack = { showNotifications = false }
                    )
                }
                1 -> CalendarScreen(
                    viewModel = calendarViewModel,
                    onAddActivity = onNavigateToAddActivity,
                    onEditActivity = onNavigateToEditActivity
                )
                2 -> MallaScreen(viewModel = mallaViewModel, onCourseClick = onNavigateToCourseDetail)
                3 -> NotesScreen(onNavigateToEdit = onNavigateToNoteEdit, viewModel = notesViewModel)
                4 -> CampusContactsListScreen(onContactClick = onNavigateToContactDetail)
            }
        }
    }
}
