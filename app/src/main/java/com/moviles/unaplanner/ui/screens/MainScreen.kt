package com.moviles.unaplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.data.StudentSession
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
import com.moviles.unaplanner.ui.theme.CrimsonRed

@Composable
fun MainScreen(
    initialIndex: Int = 0,
    onLogout: () -> Unit,
    onNavigateToContactDetail: (Int) -> Unit,
    onNavigateToNoteEdit: (Int?) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    notesViewModel: NotesViewModel
    onNavigateToAddActivity: () -> Unit,
    onNavigateToEditActivity: (Int) -> Unit,
    onNavigateToProgreso: () -> Unit,
    notesViewModel: NotesViewModel,
    calendarViewModel: StudentCalendarViewModel,
    mallaViewModel: MallaViewModel
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
    var userState by remember { mutableStateOf(com.moviles.unaplanner.data.AuthSession.currentUser) }
    val careerName by mallaViewModel.careerName.collectAsState()

    // Load notes and courses when navigating to the notes tab
    LaunchedEffect(userState) {
        userState?.let { u ->
            // Si el nombre no está en la sesión o es nulo, lo recuperamos del perfil completo
            if (u.fullName.isNullOrBlank()) {
                try {
                    val response = com.moviles.unaplanner.data.remote.RetrofitClient.apiService.getProfile(u.id)
                    if (response.isSuccessful) {
                        response.body()?.let { fullUser ->
                            com.moviles.unaplanner.data.AuthSession.setUser(fullUser)
                            userState = fullUser
                        }
                    }
                } catch (e: Exception) {
                    // Fallback silencioso
                }
            }
            mallaViewModel.loadStudentCurriculum(u.id)
        }
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
        StudentSession.profile?.fullName ?: "Bienvenido a UNAPLANNER",
        "Bienvenido a UNAPLANNER",
        "Marzo 2026",
        "Escuela de Informática",
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

    Scaffold(
        topBar = {
            AppTopBar(
                title = titles[selectedIndex],
                subtitle = subtitles[selectedIndex],
                onLogout = onLogout,
                onNavigateToEditProfile = onNavigateToEditProfile,
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
}
