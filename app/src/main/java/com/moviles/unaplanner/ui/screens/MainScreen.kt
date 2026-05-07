package com.moviles.unaplanner.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.screens.calendar.CalendarPlaceholderScreen
import com.moviles.unaplanner.ui.screens.contact.CampusContactsListScreen
import com.moviles.unaplanner.ui.screens.inicio.InicioPlaceholderScreen
import com.moviles.unaplanner.ui.screens.malla.MallaPlaceholderScreen
import com.moviles.unaplanner.ui.screens.notes.NotesPlaceholderScreen

@Composable
fun MainScreen(
    initialIndex: Int = 0,
    onLogout: () -> Unit,
    onNavigateToContactDetail: (Int) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }

    val titles = listOf("Inicio", "Calendario", "Malla Curricular", "Notas", "Directorio")
    val subtitles = listOf(
        "Bienvenido a UNAPLANNER",
        "Marzo 2026",
        "Escuela de Informática",
        "Tus apuntes",
        "Campus Sarapiquí · UNA"
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = titles[selectedIndex],
                subtitle = subtitles[selectedIndex],
                onLogout = onLogout
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
                3 -> NotesPlaceholderScreen()
                4 -> CampusContactsListScreen(onContactClick = onNavigateToContactDetail)
            }
        }
    }
}
