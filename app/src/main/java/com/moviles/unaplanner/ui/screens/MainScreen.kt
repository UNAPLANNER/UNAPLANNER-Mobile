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
    notesViewModel: NotesViewModel
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }

    // Cargar notas y cursos cuando se navega a la pestaña de notas
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

    Scaffold(
        topBar = {
            AppTopBar(
                title = titles[selectedIndex],
                subtitle = subtitles[selectedIndex],
                onLogout = onLogout,
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
                0 -> InicioPlaceholderScreen()
                1 -> CalendarPlaceholderScreen()
                2 -> MallaPlaceholderScreen()
                3 -> NotesScreen(onNavigateToEdit = onNavigateToNoteEdit, viewModel = notesViewModel)
                4 -> CampusContactsListScreen(onContactClick = onNavigateToContactDetail)
            }
        }
    }
}
