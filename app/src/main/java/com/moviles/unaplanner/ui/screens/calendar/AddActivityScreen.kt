package com.moviles.unaplanner.ui.screens.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CourseDto
import com.moviles.unaplanner.ui.components.AppButton
import com.moviles.unaplanner.ui.components.AppTextField
import com.moviles.unaplanner.ui.components.SuccessToast
import com.moviles.unaplanner.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    eventId: Int? = null,
    onBack: () -> Unit,
    viewModel: StudentCalendarViewModel
) {
    val isEditing = eventId != null
    val selectedEventState by viewModel.selectedEvent.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf("Examen") }
    var selectedCourse by remember { mutableStateOf<CourseDto?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Date & time of activity
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.of(14, 0)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Reminder
    var hasReminder by remember { mutableStateOf(false) }
    var reminderDate by remember { mutableStateOf(LocalDate.now()) }
    var reminderTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var showReminderPicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }

    val activityTypes = listOf("Examen", "Tarea", "Proyecto", "Exposición", "Evento", "Otro")
    val studentId = AuthSession.studentId ?: return
    val studentCourses by viewModel.studentCourses.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showSuccessToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Load courses on startup
    LaunchedEffect(Unit) {
        viewModel.loadInProgressCourses(studentId)
        if (isEditing && eventId != null) {
            viewModel.loadEventDetail(studentId, eventId)
        }
    }

    // Fill in the details if it's an edition
    LaunchedEffect(selectedEventState) {
        if (isEditing) {
            selectedEventState?.let { event ->
                title = event.title
                description = event.description ?: ""
                activityType = event.activityType
                selectedDate = LocalDate.parse(event.activityDate.substringBefore("T"))
                try { selectedTime = LocalTime.parse(event.activityDate.substringAfter("T").take(8)) } catch (_: Exception) {}
                hasReminder = event.hasReminder
                event.reminderDate?.let {
                    reminderDate = LocalDate.parse(it.substringBefore("T"))
                    try { reminderTime = LocalTime.parse(it.substringAfter("T").take(8)) } catch (_: Exception) {}
                }
                // The course will be assigned when the course list is loaded.
            }
        }
    }

    // Sync the selected course when the courses load
    LaunchedEffect(studentCourses, selectedEventState) {
        if (isEditing && selectedEventState != null) {
            selectedCourse = studentCourses.find { it.id == selectedEventState?.courseId }
        }
    }

    //Error handling
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Colors for the ComboBox (Extracted from NoteEditorScreen)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        cursorColor = NavyBlue,
        focusedBorderColor = NavyBlue,
        unfocusedBorderColor = AppDivider,
        focusedTrailingIconColor = NavyBlue,
        unfocusedTrailingIconColor = NavyBlue.copy(alpha = 0.7f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            Spacer(modifier = Modifier.height(40.dp))


            Surface(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = NavyBlue
                        )
                    }
                    Text(
                        text = if (isEditing) "Editar Actividad" else "Nueva Actividad",
                        style = MaterialTheme.typography.titleLarge,
                        color = NavyBlue,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Información de la actividad",
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyBlue,
                    fontWeight = FontWeight.Bold
                )

                AppTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Ej: Examen Final de Cálculo",
                    label = "Título *"
                )

                AppTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Escribe una descripción opcional...",
                    label = "Descripción",
                    singleLine = false,
                    modifier = Modifier.height(100.dp)
                )

                // Associate Course Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Curso Asociado", style = MaterialTheme.typography.labelMedium, color = NavyBlue, fontWeight = FontWeight.Bold)

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCourse?.name ?: "General (Sin curso)",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona un curso", color = Color.Gray.copy(alpha = 0.5f)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors,
                            textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                            supportingText = {
                                selectedCourse?.let {
                                    Text("Código: ${it.code}", style = TextStyle(fontSize = 12.sp, color = NavyBlue.copy(alpha = 0.6f)))
                                }
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White).exposedDropdownSize(true)
                        ) {
                            DropdownMenuItem(
                                text = { Text("General (Sin curso)", color = TextPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    selectedCourse = null
                                    expanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            )

                            studentCourses.forEach { course ->
                                HorizontalDivider(color = AppDivider.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                                DropdownMenuItem(
                                    text = {
                                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                            Text(
                                            text = course.name,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp)
                                        )
                                        Text(
                                            text = course.code,
                                            color = NavyBlue.copy(alpha = 0.7f),
                                            style = TextStyle(fontSize = 12.sp)
                                        )
                                        }
                                    },
                                    onClick = {
                                        selectedCourse = course
                                        expanded = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Text("Tipo de Actividad", fontWeight = FontWeight.Bold, color = NavyBlue, fontSize = 14.sp)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    activityTypes.forEach { type ->
                        FilterChip(
                            selected = activityType == type,
                            onClick = { activityType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonRed,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = activityType == type,
                                borderColor = Color.LightGray.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Text("Fecha y Hora de Actividad", fontWeight = FontWeight.Bold, color = NavyBlue, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedCard(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = CrimsonRed)
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                    OutlinedCard(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = CrimsonRed)
                            Text(
                                text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Reminder Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Agregar recordatorio?", fontWeight = FontWeight.Bold, color = NavyBlue, fontSize = 14.sp)
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed, checkedTrackColor = CrimsonRed.copy(alpha = 0.3f))
                    )
                }

                if (hasReminder) {
                    Text("Fecha y hora del recordatorio", fontWeight = FontWeight.Medium, color = NavyBlue, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedCard(
                            onClick = { showReminderPicker = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = CrimsonRed)
                                Text(
                                    text = reminderDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                        OutlinedCard(
                            onClick = { showReminderTimePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = CrimsonRed)
                                Text(
                                    text = reminderTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = if (isEditing) "Actualizar Actividad" else "Guardar Actividad",
                    onClick = {
                        val today = LocalDate.now()

                        if (title.isBlank()) {
                            android.widget.Toast.makeText(context, "El título es obligatorio", android.widget.Toast.LENGTH_SHORT).show()
                            return@AppButton
                        }
                      // Validation: Activity date cannot be passed
                        if (selectedDate.isBefore(today)) {
                            android.widget.Toast.makeText(context, "La fecha de la actividad no puede ser una fecha pasada", android.widget.Toast.LENGTH_LONG).show()
                            return@AppButton
                        }

                       // Validation: Reminder
                        if (hasReminder) {
                            if (reminderDate.isBefore(today)) {
                                android.widget.Toast.makeText(context, "La fecha del recordatorio no puede ser una fecha pasada", android.widget.Toast.LENGTH_LONG).show()
                                return@AppButton
                            }
                            if (reminderDate.isAfter(selectedDate)) {
                                android.widget.Toast.makeText(context, "El recordatorio debe ser antes o el mismo día de la actividad", android.widget.Toast.LENGTH_LONG).show()
                                return@AppButton
                            }
                        }

                        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        val activityDateTime = LocalDateTime.of(selectedDate, selectedTime).format(dateFormatter)
                        val reminderDateTime = if (hasReminder) {
                            LocalDateTime.of(reminderDate, reminderTime).format(dateFormatter)
                        } else null

                        if (isEditing && eventId != null) {
                            viewModel.updateEvent(
                                studentId = studentId,
                                eventId = eventId,
                                title = title,
                                description = description.ifBlank { null },
                                activityDate = activityDateTime,
                                activityType = activityType,
                                courseId = selectedCourse?.id,
                                hasReminder = hasReminder,
                                reminderDate = reminderDateTime
                            ) {
                                viewModel.clearSelectedEvent()
                                onBack()
                            }
                        } else {
                            viewModel.createEvent(
                                studentId = studentId,
                                title = title,
                                description = description.ifBlank { null },
                                activityDate = activityDateTime,
                                activityType = activityType,
                                courseId = selectedCourse?.id,
                                hasReminder = hasReminder,
                                reminderDate = reminderDateTime
                            ) {
                                // Upon successful completion, we return to the calendar
                                onBack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = NavyBlue,
                    icon = Icons.Default.Save
                )

                //End spacer to ensure visibility at the end of the scroll
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Animated Success Toast
        AnimatedVisibility(
            visible = showSuccessToast,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp)
                .zIndex(100f)
        ) {
            SuccessToast(
                message = toastMessage,
                onDismiss = { showSuccessToast = false }
            )
        }
    }
    if (showDatePicker) {
        val todayUtcMillis = LocalDate.now().atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Allows selection only from today onwards (UTC)
                    return utcTimeMillis >= todayUtcMillis
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("ACEPTAR", color = NavyBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCELAR", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker for activity time
    if (showTimePicker) {
        val state = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Hora de la actividad", fontWeight = FontWeight.Bold, color = NavyBlue) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = state)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(state.hour, state.minute)
                    showTimePicker = false
                }) { Text("ACEPTAR", color = NavyBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("CANCELAR", color = Color.Gray) }
            }
        )
    }

    // TimePicker for reminder time
    if (showReminderTimePicker) {
        val state = rememberTimePickerState(
            initialHour = reminderTime.hour,
            initialMinute = reminderTime.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showReminderTimePicker = false },
            title = { Text("Hora del recordatorio", fontWeight = FontWeight.Bold, color = NavyBlue) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = state)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    reminderTime = LocalTime.of(state.hour, state.minute)
                    showReminderTimePicker = false
                }) { Text("ACEPTAR", color = NavyBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showReminderTimePicker = false }) { Text("CANCELAR", color = Color.Gray) }
            }
        )
    }

    //Dialogue for Reminder Date
    if (showReminderPicker) {
        val todayUtcMillis = LocalDate.now().atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = reminderDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // The reminder cannot be earlier than today.
                    // Also, the reminder cannot be after the date of the activity.
                    val activityDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()

                    return utcTimeMillis >= todayUtcMillis && utcTimeMillis <= activityDateMillis
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showReminderPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        reminderDate = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                    }
                    showReminderPicker = false
                }) { Text("ACEPTAR", color = NavyBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showReminderPicker = false }) { Text("CANCELAR", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
