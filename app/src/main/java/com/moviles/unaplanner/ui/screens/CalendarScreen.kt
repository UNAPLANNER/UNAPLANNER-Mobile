package com.moviles.unaplanner.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.components.ActivityCard
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.components.CalendarGrid
import com.moviles.unaplanner.ui.components.DayHighlight
import com.moviles.unaplanner.ui.components.AddActivityButton
import com.moviles.unaplanner.ui.theme.*

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableIntStateOf(15) }
    var navSelectedIndex by remember { mutableIntStateOf(1) }

    val highlights = listOf(
        DayHighlight(3,  EventBlue.copy(alpha = 0.3f)),
        DayHighlight(5,  EventOrange.copy(alpha = 0.3f)),
        DayHighlight(7,  EventBlue.copy(alpha = 0.3f)),
        DayHighlight(13, EventGreen.copy(alpha = 0.3f))
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Calendario",
                subtitle = "Marzo 2026"
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedIndex = navSelectedIndex,
                onItemSelected = { navSelectedIndex = it }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CalendarGrid(
                    month = "Marzo",
                    year = 2026,
                    startDayOfWeek = 6,
                    totalDays = 31,
                    selectedDay = selectedDay,
                    highlights = highlights,
                    onDaySelected = { selectedDay = it },
                    onPreviousMonth = { },
                    onNextMonth = { }
                )
            }

            item {
                Text(
                    text = "ACTIVIDADES DE MARZO",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActivityCard(
                        title = "Examen Parcial – EIF211",
                        datetime = "Lunes 3 · 7:00 a.m.",
                        dotColor = CrimsonRed
                    )
                    ActivityCard(
                        title = "Entrega Proyecto – EIF411",
                        datetime = "Miércoles 5 · 11:59 p.m.",
                        dotColor = EventOrange
                    )
                    ActivityCard(
                        title = "Tarea 2 – LIX412",
                        datetime = "Viernes 7 · 8:00 a.m.",
                        dotColor = EventBlue
                    )
                }
            }

            item {
                AddActivityButton(
                    onClick = { },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Modo Claro")
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Modo Oscuro"
)
@Composable
fun CalendarScreenPreview() {
    UNAPLANNERTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CalendarScreen()
        }
    }
}