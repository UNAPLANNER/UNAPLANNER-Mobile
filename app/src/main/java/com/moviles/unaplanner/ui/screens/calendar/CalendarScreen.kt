package com.moviles.unaplanner.ui.screens.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CalendarEvent
import com.moviles.unaplanner.data.remote.model.CalendarSummaryStats
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.intl.Locale as ComposeLocale
import androidx.compose.ui.text.style.TextAlign
import com.moviles.unaplanner.ui.components.AddActivityButton
import com.moviles.unaplanner.ui.components.CalendarGrid
import com.moviles.unaplanner.ui.components.DayHighlight
import com.moviles.unaplanner.ui.components.SuccessToast
import com.moviles.unaplanner.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: StudentCalendarViewModel,
    onAddActivity: () -> Unit,
    onEditActivity: (Int) -> Unit
) {
    val uiState by viewModel.calendarState.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val studentId = AuthSession.currentUser?.id ?: 1

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    
    // Toast state
    var showSuccessToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Bottom Sheet state
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        viewModel.loadStudentCalendar(studentId)
    }

    //Managing success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            toastMessage = it
            showSuccessToast = true
            delay(3000)
            showSuccessToast = false
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            // Offline banner
            if (isOffline) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Sin conexión — mostrando datos en caché",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
            when (val state = uiState) {
                is CalendarUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CalendarUiState.Success -> {
                    CalendarContent(
                        events = state.calendar.events,
                        currentMonth = currentMonth,
                        selectedDay = selectedDay,
                        onMonthChange = { currentMonth = it },
                        onDaySelected = { selectedDay = it },
                        onAddActivity = onAddActivity,
                        onEventClick = {
                            selectedEvent = it
                            showBottomSheet = true
                        }
                    )
                }
                is CalendarUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            } // closes internal column
        } // closes external Column

        // Success Toast
        AnimatedVisibility(
            visible = showSuccessToast,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .zIndex(100f)
        ) {
            SuccessToast(
                message = toastMessage,
                onDismiss = { showSuccessToast = false }
            )
        }

        // Deletion Confirmation Dialog
        if (showDeleteConfirmation && selectedEvent != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                containerColor = Color.White,
                title = { 
                    Text(
                        "Eliminar Actividad", 
                        fontWeight = FontWeight.ExtraBold, 
                        color = NavyBlue,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) 
                },
                text = { 
                    Text(
                        "¿Estás seguro de que deseas eliminar esta actividad? Esta acción no se puede deshacer.",
                        color = Color.Black.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) 
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteEvent(studentId, selectedEvent!!.id)
                            showDeleteConfirmation = false
                            showBottomSheet = false
                        }
                    ) {
                        Text("Eliminar", fontWeight = FontWeight.Bold, color = CrimsonRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancelar", color = NavyBlue, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }

    if (showBottomSheet && selectedEvent != null) {
        EventDetailsBottomSheet(
            event = selectedEvent!!,
            onDismiss = { showBottomSheet = false },
            onEdit = {
                showBottomSheet = false
                onEditActivity(selectedEvent!!.id)
            },
            onDelete = { showDeleteConfirmation = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsBottomSheet(
    event: CalendarEvent,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(event.getActivityColor()))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = event.activityType.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(event.getActivityColor()),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = CrimsonRed
                    )
                }
            }

            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = NavyBlue
            )

            if (!event.courseName.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Curso: ${event.courseName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
            }

            val dateStr = try {
                val date = LocalDate.parse(event.activityDate.substringBefore("T"))
                date.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM, yyyy", Locale.forLanguageTag("es")))
            } catch (e: Exception) {
                event.activityDate
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = android.R.drawable.ic_menu_my_calendar), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dateStr.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            if (!event.description.isNullOrBlank()) {
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NavyBlue)
                ) {
                    Text("Editar", fontWeight = FontWeight.Bold, color = NavyBlue)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CalendarContent(
    events: List<CalendarEvent>,
    currentMonth: YearMonth,
    selectedDay: LocalDate?,
    onMonthChange: (YearMonth) -> Unit,
    onDaySelected: (LocalDate?) -> Unit,
    onAddActivity: () -> Unit,
    onEventClick: (CalendarEvent) -> Unit
) {
    val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    // Parse events to LocalDates and associate with colors
    val eventDates = remember(events) {
        events.mapNotNull {
            try {
                val date = LocalDate.parse(it.activityDate.substringBefore("T"))
                date to Color(it.getActivityColor())
            } catch (e: Exception) {
                null
            }
        }
    }

    // Filter events for the current month
    val eventsInMonth = remember(events, currentMonth) {
        events.filter {
            try {
                val date = LocalDate.parse(it.activityDate.substringBefore("T"))
                YearMonth.from(date) == currentMonth
            } catch (e: Exception) {
                false
            }
        }.sortedBy { it.activityDate }
    }

    // Highlights for the calendar
    val highlights = eventDates
        .filter { YearMonth.from(it.first) == currentMonth }
        .map { DayHighlight(it.first.dayOfMonth, it.second) }
        .distinctBy { it.day }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value // 1 (Mon) to 7 (Sun)
            // Adjust for grid: if Mon is 0, then we use (value - 1)
            CalendarGrid(
                monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")).replaceFirstChar { it.uppercase() },
                year = currentMonth.year,
                startDayOfWeek = firstDayOfMonth - 1, 
                totalDays = currentMonth.lengthOfMonth(),
                selectedDay = if (selectedDay?.month == currentMonth.month && selectedDay?.year == currentMonth.year) selectedDay?.dayOfMonth else null,
                today = if (LocalDate.now().month == currentMonth.month && LocalDate.now().year == currentMonth.year) LocalDate.now().dayOfMonth else null,
                highlights = highlights,
                onDaySelected = { day ->
                    val date = currentMonth.atDay(day)
                    onDaySelected(if (selectedDay == date) null else date)
                    
                    // Check if there are any events on that day to open the details
                    val eventsOnDay = events.filter {
                        try {
                            LocalDate.parse(it.activityDate.substringBefore("T")) == date
                        } catch (e: Exception) {
                            false
                        }
                    }
                    if (eventsOnDay.isNotEmpty()) {
                        onEventClick(eventsOnDay.first())
                    }
                },
                onPreviousMonth = { onMonthChange(currentMonth.minusMonths(1)) },
                onNextMonth = { onMonthChange(currentMonth.plusMonths(1)) }
            )
        }

        item {
            val textMonth = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")).uppercase()
            Text(
                text = "ACTIVIDADES DE $textMonth",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = NavyBlue,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }

        if (eventsInMonth.isEmpty()) {
            item {
                Text(
                    "No hay actividades para este mes",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            items(eventsInMonth) { event ->
                CompactEventItem(event, onClick = { onEventClick(event) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            AddActivityButton(
                onClick = onAddActivity,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CompactEventItem(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    val dateText = try {
        val date = LocalDate.parse(event.activityDate.substringBefore("T"))
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")).replaceFirstChar { it.uppercase() }
        val dayNum = date.dayOfMonth
        // For the time (assuming ISO format)
        val timePart = event.activityDate.substringAfter("T").substring(0, 5)
        "$dayName $dayNum - $timePart"
    } catch (e: Exception) {
        event.activityDate
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dot color
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(event.getActivityColor()))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (event.courseName != null) "${event.title} – ${event.courseName}" else event.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
