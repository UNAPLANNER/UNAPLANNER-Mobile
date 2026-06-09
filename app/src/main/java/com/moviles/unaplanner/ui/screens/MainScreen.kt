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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.screens.calendar.CalendarScreen
import com.moviles.unaplanner.ui.screens.calendar.StudentCalendarViewModel
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.ui.screens.contact.CampusContactsListScreen
import com.moviles.unaplanner.ui.screens.inicio.InicioPlaceholderScreen
import com.moviles.unaplanner.ui.screens.malla.MallaPlaceholderScreen
import com.moviles.unaplanner.ui.screens.notes.NotesScreen
import com.moviles.unaplanner.ui.screens.notes.NotesViewModel
import com.moviles.unaplanner.ui.theme.CrimsonRed

@Composable
fun MainScreen(
    initialIndex: Int = 0,
    onLogout: () -> Unit,
    onNavigateToContactDetail: (Int) -> Unit,
    onNavigateToNoteEdit: (Int?) -> Unit,
    onNavigateToAddActivity: () -> Unit,
    notesViewModel: NotesViewModel,
    calendarViewModel: StudentCalendarViewModel
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
    

    // Cargar notas y cursos cuando se navega a la pestaña de notas
    LaunchedEffect(selectedIndex) {
        if (selectedIndex == 3) {
            notesViewModel.loadNotes()
            notesViewModel.loadStudentCourses()
        }
    }

    val titles = listOf("Inicio", "Calendario", "Malla Curricular", "Mis Notas", "Directorio")
    val subtitles = listOf(
        "Bienvenido a UNAPLANNER",
        "Marzo 2026",
        "Escuela de Informática",
        "Apuntes por curso",
        "Campus Sarapiquí · UNA"
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
                1 -> CalendarScreen(
                    viewModel = calendarViewModel,
                    onAddActivity = onNavigateToAddActivity
                )
                2 -> MallaPlaceholderScreen()
                3 -> NotesScreen(onNavigateToEdit = onNavigateToNoteEdit, viewModel = notesViewModel)
                4 -> CampusContactsListScreen(onContactClick = onNavigateToContactDetail)
            }
        }
    }
}
