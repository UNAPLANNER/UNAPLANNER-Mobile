package com.moviles.unaplanner.ui.screens.malla

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CourseDetailDto
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.remote.model.PrerequisiteDto
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.theme.*

@Composable
fun CourseDetailScreen(
    courseId: Int,
    onBack: () -> Unit,
    onNavigateToNoteEdit: (Int?) -> Unit = {},
    viewModel: CourseDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val userId = AuthSession.studentId
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(courseId) {
        userId?.let { viewModel.loadDetail(it, courseId) }
    }

    LaunchedEffect(saveState) {
        when (val s = saveState) {
            is SaveDetailUiState.Success -> {
                snackbarHostState.showSnackbar("Datos guardados correctamente")
                viewModel.clearSaveState()
            }
            is SaveDetailUiState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.clearSaveState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = "Detalles del Curso",
                subtitle = "Malla",
                titleFontSize = 22.sp,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is CourseDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NavyBlue)
                }
            }
            is CourseDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(s.message, color = CrimsonRed, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = { userId?.let { viewModel.loadDetail(it, courseId) } }) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }
            is CourseDetailUiState.Success -> {
                CourseDetailContent(
                    detail = s.detail,
                    courseId = courseId,
                    viewModel = viewModel,
                    onNavigateToNoteEdit = onNavigateToNoteEdit,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

// ─── Content ──────────────────────────────────────────────────────────────────

@Composable
private fun CourseDetailContent(
    detail: CourseDetailDto,
    courseId: Int,
    viewModel: CourseDetailViewModel,
    onNavigateToNoteEdit: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Info", "Evaluaciones", "Notas")
    val userId = AuthSession.studentId

    // Load notes when that tab becomes active
    LaunchedEffect(selectedTab) {
        if (selectedTab == 2 && userId != null) {
            viewModel.loadCourseNotes(userId, courseId)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceLight)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = detail.code,
                color = CrimsonRed,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = detail.name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoChip(text = "${detail.credits} Créditos")
                InfoChip(text = "Nivel ${detail.level} · Ciclo ${detail.term}")
                StatusChip(status = detail.status)
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceLight,
            contentColor = CrimsonRed
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = CrimsonRed,
                    unselectedContentColor = TextSecondary
                )
            }
        }

        // Tab content — weight fills remaining space so LazyColumn has a bounded height
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> InfoTab(detail = detail, viewModel = viewModel, courseId = courseId)
                1 -> EvaluationsTab(viewModel = viewModel, courseId = courseId, courseStatus = detail.status)
                2 -> NotesTab(
                    viewModel = viewModel,
                    courseId = courseId,
                    onNoteClick = onNavigateToNoteEdit,
                    onCreateNote = { onNavigateToNoteEdit(null) }
                )
            }
        }
    }
}

// ─── Evaluations Tab ──────────────────────────────────────────────────────────

@Composable
private fun EvaluationsTab(
    viewModel: CourseDetailViewModel,
    courseId: Int,
    courseStatus: String
) {
    val evaluationsState by viewModel.evaluationsState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    val userId = AuthSession.studentId
    val context = LocalContext.current

    // EnCurso / Reprobado → editable; Aprobado → solo lectura; Pendiente → bloqueado
    val canView = courseStatus == "EnCurso" || courseStatus == "Aprobado" || courseStatus == "Reprobado"
    val canEdit = courseStatus == "EnCurso" || courseStatus == "Reprobado"

    LaunchedEffect(courseId, canView) {
        if (canView) userId?.let { viewModel.loadEvaluations(it, courseId) }
    }

    if (!canView) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Las evaluaciones estarán disponibles cuando el curso esté En Curso",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        when (val s = evaluationsState) {
            is EvaluationsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NavyBlue)
                }
            }
            is EvaluationsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(s.message, color = CrimsonRed, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
                    OutlinedButton(onClick = { userId?.let { viewModel.loadEvaluations(it, courseId) } }) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reintentar")
                    }
                }
            }
            is EvaluationsUiState.Success -> {
                val evaluations = s.evaluations
                val totalPercentage = evaluations.sumOf { it.percentage }
                val totalEarned = evaluations.sumOf { it.earnedPoints }
                val remainingFor70 = (70.0 - totalEarned).coerceAtLeast(0.0)

                val currentEvaluations = (evaluationsState as? EvaluationsUiState.Success)?.evaluations ?: emptyList()
                val porcentajeObtenido = if (totalPercentage > 0) (totalEarned / totalPercentage) * 100 else 0.0
                Column(modifier = Modifier.fillMaxSize()) {
                    // Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SummaryItem(label = "Puntos Obtenidos", value = "%.2f".format(totalEarned), color = if (totalEarned >= 70) EventGreen else CrimsonRed)
                                SummaryItem(label = "% Logrado", value = "%.1f%%".format(porcentajeObtenido), color = if (porcentajeObtenido >= 70) EventGreen else CrimsonRed)
                                SummaryItem(label = "Definido", value = "${totalPercentage.toInt()}%", color = NavyBlue)
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (totalEarned < 70) {
                                Text(
                                    text = "Faltan ${"%.2f".format(remainingFor70)} puntos para alcanzar el 70%",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "¡Has alcanzado el 70% necesario para aprobar!",
                                    color = EventGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (evaluations.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay evaluaciones registradas", color = TextSecondary)
                                if (canEdit) {
                                    TextButton(onClick = { showAddDialog = true }) {
                                        Text("+ Agregar primera evaluación", color = NavyBlue)
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Listado de Evaluaciones", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    if (canEdit) {
                                        TextButton(onClick = { showAddDialog = true }) {
                                            Text("+ Nueva", color = NavyBlue, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                            items(evaluations) { evaluation ->
                                EvaluationCard(
                                    evaluation = evaluation,
                                    allEvaluations = evaluations,
                                    readOnly = !canEdit,
                                    onUpdate = { name, type, percentage, grade, date, hasReminder ->
                                        userId?.let {
                                            viewModel.updateEvaluation(
                                                it, courseId, evaluation.id, name, type, percentage, grade, date, hasReminder,
                                                onScheduleReminder = { eval ->
                                                    com.moviles.unaplanner.notifications.ReminderScheduler.scheduleEvaluation(context, eval)
                                                }
                                            )
                                        }
                                    },
                                    onDelete = {
                                        userId?.let {
                                            viewModel.deleteEvaluation(it, courseId, evaluation.id,
                                                onCancelReminder = { id ->
                                                    com.moviles.unaplanner.notifications.ReminderScheduler.cancelEvaluation(context, id)
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && canEdit) {
        val currentEvaluations = (evaluationsState as? EvaluationsUiState.Success)?.evaluations ?: emptyList()
        EvaluationFormDialog(
            title = "Nueva Evaluación",
            allEvaluations = currentEvaluations,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type, percentage, grade, date, hasReminder ->
                userId?.let {
                    viewModel.addEvaluation(it, courseId, name, type, percentage, date, hasReminder,
                        onScheduleReminder = { eval ->
                            com.moviles.unaplanner.notifications.ReminderScheduler.scheduleEvaluation(context, eval)
                        }
                    )
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SummaryItem(label: String, value: String, color: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun EvaluationCard(
    evaluation: com.moviles.unaplanner.data.remote.model.EvaluationDto,
    allEvaluations: List<com.moviles.unaplanner.data.remote.model.EvaluationDto>,
    readOnly: Boolean = false,
    onUpdate: (String, String, Double, Double?, String?, Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = evaluation.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    EvaluationTypeBadge(type = evaluation.type)
                }
                Text(
                    text = "Peso: ${evaluation.percentage}%",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (!evaluation.date.isNullOrBlank()) {
                    Text(
                        text = "Fecha: ${formatNoteDate(evaluation.date)}",
                        fontSize = 11.sp,
                        color = NavyBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val grade = evaluation.grade
                val color = if (grade != null && grade >= 70) EventGreen else if (grade != null) CrimsonRed else TextSecondary
                
                Text(
                    text = if (grade != null) "%.1f".format(grade) else "—",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "Obtenido: %.2f".format(evaluation.earnedPoints),
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            
            if (!readOnly) {
                Row {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = NavyBlue.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = CrimsonRed.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }

    if (showEditDialog && !readOnly) {
        EvaluationFormDialog(
            title = "Editar Evaluación",
            initialName = evaluation.name,
            initialType = evaluation.type,
            initialPercentage = evaluation.percentage,
            initialGrade = evaluation.grade,
            initialDate = evaluation.date,
            initialHasReminder = evaluation.hasReminder,
            allEvaluations = allEvaluations,
            editingEvaluationId = evaluation.id,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, type, percentage, grade, date, hasReminder ->
                onUpdate(name, type, percentage, grade, date, hasReminder)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar evaluación") },
            text = { Text("¿Seguro que querés eliminar \"${evaluation.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Eliminar", color = CrimsonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EvaluationTypeBadge(type: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = NavyBlue.copy(alpha = 0.1f)
    ) {
        Text(
            text = type,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvaluationFormDialog(
    title: String,
    initialName: String = "",
    initialType: String = "Examen",
    initialPercentage: Double? = null,
    initialGrade: Double? = null,
    initialDate: String? = null,
    initialHasReminder: Boolean = false,
    allEvaluations: List<com.moviles.unaplanner.data.remote.model.EvaluationDto> = emptyList(),
    editingEvaluationId: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double?, String?, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var type by remember { mutableStateOf(initialType) }
    var percentage by remember { mutableStateOf(initialPercentage?.toString() ?: "") }
    var grade by remember { mutableStateOf(initialGrade?.toString() ?: "") }
    var hasReminder by remember { mutableStateOf(initialHasReminder) }
    val types = listOf("Examen", "Tarea", "Proyecto", "Exposición", "Otro")

    // Parse date and optional time from initialDate ("2026-06-21" or "2026-06-21T10:00:00")
    val initDateOnly = remember { initialDate?.substringBefore('T') ?: "" }
    val initTimeStr  = remember {
        if (initialDate?.contains('T') == true) initialDate.substringAfter('T').take(5) else null
    }
    var date by remember { mutableStateOf(initDateOnly) }
    var time by remember { mutableStateOf(initTimeStr) }  // "HH:mm" or null

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initDateOnly.takeIf { it.isNotBlank() }?.let {
            try {
                java.time.LocalDate.parse(it).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
            } catch (e: Exception) {
                null
            }
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour   = initTimeStr?.substringBefore(':')?.toIntOrNull() ?: 8,
        initialMinute = initTimeStr?.substringAfter(':')?.take(2)?.toIntOrNull() ?: 0,
        is24Hour      = true
    )

    // Percentage validation logic
    val totalOtherPercentages = allEvaluations
        .filter { it.id != editingEvaluationId }
        .sumOf { it.percentage }
    val percentageDouble = percentage.toDoubleOrNull() ?: 0.0
    val isPercentageValid = percentageDouble > 0 && (totalOtherPercentages + percentageDouble) <= 100.0
    val availablePercentage = 100.0 - totalOtherPercentages

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    placeholder = { Text("Ej: Parcial 1") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Tipo", style = MaterialTheme.typography.labelMedium)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = percentage,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) percentage = it },
                        label = { Text("Peso (%)") },
                        isError = !isPercentageValid && percentage.isNotEmpty(),
                        supportingText = {
                            if (!isPercentageValid && percentage.isNotEmpty()) {
                                Text(
                                    if (percentageDouble <= 0) "Debe ser > 0" 
                                    else "Máx: ${availablePercentage.toInt()}%",
                                    color = CrimsonRed
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = grade,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grade = it },
                        label = { Text("Nota (0-100)") },
                        placeholder = { Text("—") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (date.isNotBlank() && time != null) "$date  $time" else date,
                        onValueChange = { },
                        label = { Text("Fecha") },
                        placeholder = { Text("Seleccionar fecha") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        }
                    )
                    // Invisible box to capture clicks over the field and show picker
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                if (date.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (time != null) "Hora: $time" else "Sin hora específica",
                            fontSize = 13.sp,
                            color = if (time != null) TextPrimary else TextSecondary
                        )
                        Row {
                            TextButton(onClick = { showTimePicker = true }) {
                                Text(if (time != null) "Cambiar" else "Agregar hora", color = NavyBlue, fontSize = 13.sp)
                            }
                            if (time != null) {
                                TextButton(onClick = { time = null }) {
                                    Text("Quitar", color = CrimsonRed, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recordatorio en calendario", fontSize = 14.sp)
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor    = androidx.compose.ui.graphics.Color.White,
                            checkedTrackColor    = NavyBlue,
                            checkedBorderColor   = NavyBlue,
                            uncheckedThumbColor  = Disabled,
                            uncheckedTrackColor  = Divider,
                            uncheckedBorderColor = Disabled
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = percentage.toDoubleOrNull() ?: 0.0
                    val g = grade.toDoubleOrNull()
                    val d = when {
                        date.isBlank() -> null
                        time != null   -> "${date}T${time}:00"
                        else           -> date
                    }
                    if (name.isNotBlank() && isPercentageValid) {
                        onConfirm(name, type, p, g, d, hasReminder)
                    }
                },
                enabled = name.isNotBlank() && isPercentageValid,
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = java.time.Instant.ofEpochMilli(it)
                        val localDate = java.time.LocalDate.ofInstant(instant, java.time.ZoneOffset.UTC)
                        date = localDate.toString()
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Hora de la evaluación", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    time = "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }
}

// ─── Info Tab ─────────────────────────────────────────────────────────────────

@Composable
private fun InfoTab(detail: CourseDetailDto, viewModel: CourseDetailViewModel, courseId: Int) {
    val showTeacherInfo = detail.status == "EnCurso" || detail.status == "Aprobado"
    val isEnCurso = detail.status == "EnCurso"
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()
    val userId = AuthSession.studentId
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    InfoRow(label = "Código", value = detail.code)
                    if (showTeacherInfo) {
                        HorizontalDivider(color = Divider)
                        InfoRow(label = "Profesor", value = detail.professorName ?: "—")
                        HorizontalDivider(color = Divider)
                        InfoRow(label = "Horario", value = detail.schedule ?: "—")
                        HorizontalDivider(color = Divider)
                        InfoRow(label = "Aula", value = detail.classroom ?: "—")
                        if (!detail.syllabusUrl.isNullOrBlank()) {
                            HorizontalDivider(color = Divider)
                            InfoRow(label = "Programa", value = detail.syllabusUrl)
                        }
                    }
                    HorizontalDivider(color = Divider)
                    InfoRow(label = "Créditos", value = "${detail.credits}")
                    if ((detail.theoryHours ?: 0) > 0 || (detail.practiceHours ?: 0) > 0 || (detail.labHours ?: 0) > 0) {
                        HorizontalDivider(color = Divider)
                        InfoRow(
                            label = "Horas",
                            value = buildHorasText(detail.theoryHours, detail.practiceHours, detail.labHours)
                        )
                    }
                    if (detail.finalGrade != null) {
                        HorizontalDivider(color = Divider)
                        InfoRow(label = "Nota final", value = "%.1f".format(detail.finalGrade))
                    }
                }
            }
        }

        // Edit button — visible only when EnCurso
        if (isEnCurso) {
            item {
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (detail.enrolledDetailId != null) "Editar datos del curso" else "Registrar datos del curso",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        val prereqs = detail.prerequisites.orEmpty()
        if (prereqs.isNotEmpty()) {
            item {
                SectionHeader(
                    icon = { Icon(Icons.Default.VpnKey, null, tint = NavyBlue, modifier = Modifier.size(18.dp)) },
                    title = "REQUISITOS PARA ESTE CURSO"
                )
            }
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        prereqs.forEachIndexed { index, prereq ->
                            if (index > 0) HorizontalDivider(color = Divider)
                            PrerequisiteRow(prereq = prereq)
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EnrolledDetailDialog(
            detail = detail,
            isSaving = saveState is SaveDetailUiState.Saving,
            onDismiss = { showEditDialog = false },
            onConfirm = { profesor, aula, horario, url ->
                userId?.let {
                    viewModel.saveEnrolledDetail(it, courseId, detail.enrolledDetailId, profesor, aula, horario, url)
                }
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun EnrolledDetailDialog(
    detail: CourseDetailDto,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (profesor: String?, aula: String?, horario: String?, url: String?) -> Unit
) {
    var profesorText by remember { mutableStateOf(detail.professorName ?: "") }
    var aulaText by remember { mutableStateOf(detail.classroom ?: "") }
    var horarioText by remember { mutableStateOf(detail.schedule ?: "") }
    var urlText by remember { mutableStateOf(detail.syllabusUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Datos del curso en curso", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "${detail.code} – ${detail.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                OutlinedTextField(
                    value = profesorText,
                    onValueChange = { profesorText = it },
                    label = { Text("Profesor") },
                    placeholder = { Text("Nombre del profesor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = horarioText,
                    onValueChange = { horarioText = it },
                    label = { Text("Horario") },
                    placeholder = { Text("Ej: L-J 8:00-10:00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = aulaText,
                    onValueChange = { aulaText = it },
                    label = { Text("Aula") },
                    placeholder = { Text("Ej: B-205") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("Enlace del programa (opcional)") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(profesorText, aulaText, horarioText, urlText) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = SurfaceLight, strokeWidth = 2.dp)
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun buildHorasText(theory: Int?, practice: Int?, lab: Int?): String {
    val parts = mutableListOf<String>()
    if ((theory ?: 0) > 0) parts.add("T: $theory")
    if ((practice ?: 0) > 0) parts.add("P: $practice")
    if ((lab ?: 0) > 0) parts.add("L: $lab")
    return parts.joinToString(" / ")
}

// ─── Notes Tab ────────────────────────────────────────────────────────────────

@Composable
private fun NotesTab(
    viewModel: CourseDetailViewModel,
    courseId: Int,
    onNoteClick: (Int?) -> Unit,
    onCreateNote: () -> Unit
) {
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()
    val userId = AuthSession.studentId

    Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        when (val s = notesState) {
            is CourseNotesUiState.Idle,
            is CourseNotesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NavyBlue)
                }
            }

            is CourseNotesUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(s.message, color = CrimsonRed, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
                    OutlinedButton(onClick = { userId?.let { viewModel.reloadCourseNotes(it, courseId) } }) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reintentar")
                    }
                }
            }

            is CourseNotesUiState.Success -> {
                if (s.notes.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = null,
                            tint = Disabled,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No hay notas para este curso",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCreateNote,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                        ) {
                            Text("+ Nueva nota")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${s.notes.size} nota${if (s.notes.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                TextButton(onClick = onCreateNote) {
                                    Text("+ Nueva", color = NavyBlue, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        items(s.notes, key = { it.id }) { note ->
                            NoteCard(note = note, onClick = { onNoteClick(note.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: NoteDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(44.dp)
                    .background(NavyBlue, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!note.content.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.content,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatNoteDate(note.lastUpdated),
                    fontSize = 11.sp,
                    color = Disabled
                )
            }
        }
    }
}

private fun formatNoteDate(isoDate: String): String {
    return try {
        if (isoDate.length > 10 && isoDate[10] == 'T' && !isoDate.endsWith('Z')) {
            val dt = java.time.LocalDateTime.parse(isoDate)
            val hasTime = dt.hour != 0 || dt.minute != 0
            if (hasTime)
                "%02d/%02d/%d %02d:%02d".format(dt.dayOfMonth, dt.monthValue, dt.year, dt.hour, dt.minute)
            else
                "%02d/%02d/%d".format(dt.dayOfMonth, dt.monthValue, dt.year)
        } else {
            val instant = java.time.Instant.parse(isoDate)
            val local = instant.atZone(java.time.ZoneId.systemDefault())
            "%02d/%02d/%d".format(local.dayOfMonth, local.monthValue, local.year)
        }
    } catch (e: Exception) {
        try {
            val ld = java.time.LocalDate.parse(isoDate.take(10))
            "%02d/%02d/%d".format(ld.dayOfMonth, ld.monthValue, ld.year)
        } catch (e2: Exception) {
            isoDate.take(10)
        }
    }
}

// ─── Shared row components ────────────────────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 14.sp)
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = TextPrimary,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PrerequisiteRow(prereq: PrerequisiteDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${prereq.code.orEmpty()} – ${prereq.name.orEmpty()}",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
            if (!prereq.type.isNullOrBlank()) {
                Text(text = prereq.type, fontSize = 12.sp, color = TextSecondary)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (prereq.isPassed) PassedBadge() else PendingBadge()
    }
}

// ─── Badges ───────────────────────────────────────────────────────────────────

@Composable
private fun PassedBadge() {
    Surface(shape = RoundedCornerShape(8.dp), color = EventGreen.copy(alpha = 0.12f)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = EventGreen, modifier = Modifier.size(13.dp))
            Text("Aprobado", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EventGreen)
        }
    }
}

@Composable
private fun PendingBadge() {
    Surface(shape = RoundedCornerShape(8.dp), color = Disabled.copy(alpha = 0.18f)) {
        Text(
            "Pendiente",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
    }
}

// ─── Section header ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: @Composable () -> Unit, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    ) {
        icon()
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = NavyBlue,
            letterSpacing = 0.8.sp,
            fontSize = 11.sp
        )
    }
}

// ─── Header chips ─────────────────────────────────────────────────────────────

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BackgroundLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, Divider)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, textColor, label) = when (status) {
        "Aprobado" -> Triple(EventGreen.copy(alpha = 0.15f), EventGreen, "Aprobado")
        "EnCurso" -> Triple(EventGreen.copy(alpha = 0.15f), EventGreen, "En curso")
        "Reprobado" -> Triple(CrimsonRed.copy(alpha = 0.12f), CrimsonRed, "Reprobado")
        else -> Triple(Disabled.copy(alpha = 0.18f), TextSecondary, "Pendiente")
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Placeholder tab ─────────────────────────────────────────────────────────

@Composable
private fun ComingSoonTab() {
    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Text("Próximamente", color = TextSecondary, fontSize = 14.sp)
    }
}
