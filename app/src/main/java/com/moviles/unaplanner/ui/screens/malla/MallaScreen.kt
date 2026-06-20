package com.moviles.unaplanner.ui.screens.malla

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CurriculumCourseDto
import com.moviles.unaplanner.data.remote.model.CurriculumLevelDto
import com.moviles.unaplanner.data.remote.model.StudentCourseProgressDto
import com.moviles.unaplanner.ui.theme.*

// ─── Item model for flat LazyColumn list ────────────────────────────────────

private sealed class MallaItem {
    data class SemesterHeader(
        val level: Int,
        val semester: Int,
        val totalCredits: Int
    ) : MallaItem()

    data class SectionHeader(val title: String) : MallaItem()

    data class CourseEntry(
        val course: CurriculumCourseDto,
        val isLeft: Boolean,
        val isFirstInSection: Boolean
    ) : MallaItem()
}

private fun buildMallaItems(levels: List<CurriculumLevelDto>): List<MallaItem> {
    val result = mutableListOf<MallaItem>()
    val allElectives = mutableListOf<CurriculumCourseDto>()

    levels.forEach { level ->
        level.semesters.forEach { semester ->
            // Separate mandatory and electives
            val mandatory = semester.courses.filter { 
                !it.isElective && (it.electiveType == null || it.electiveType == "Obligatorio") 
            }.sortedBy { it.code }
            
            val electivesInSemester = semester.courses.filter { 
                it.isElective || (it.electiveType != null && it.electiveType != "Obligatorio")
            }
            allElectives.addAll(electivesInSemester)

            // Add Semester Header (only mandatory credits for the main plan)
            val mandatoryCredits = mandatory.sumOf { it.credits }
            result.add(MallaItem.SemesterHeader(level.level, semester.semester, mandatoryCredits))

            var isLeft = true
            mandatory.forEachIndexed { index, course ->
                result.add(MallaItem.CourseEntry(
                    course = course,
                    isLeft = isLeft,
                    isFirstInSection = index == 0
                ))
                isLeft = !isLeft
            }
        }
    }

    // Add all electives at the very end
    if (allElectives.isNotEmpty()) {
        result.add(MallaItem.SectionHeader("CURSOS OPTATIVOS"))
        
        val sortedElectives = allElectives.distinctBy { it.id }.sortedWith(compareBy({ 
            when(it.electiveType) {
                "OptativoDisciplinario" -> 0
                "OptativoLibre" -> 1
                else -> 2
            }
        }, { it.code }))

        var isLeft = true
        sortedElectives.forEachIndexed { index, course ->
            result.add(MallaItem.CourseEntry(
                course = course,
                isLeft = isLeft,
                isFirstInSection = index == 0 // Reset connector
            ))
            isLeft = !isLeft
        }
    }

    return result
}

// ─── Main Screen ─────────────────────────────────────────────────────────────

@Composable
fun MallaScreen(
    modifier: Modifier = Modifier,
    viewModel: MallaViewModel,
    onCourseClick: (courseId: Int) -> Unit = {}
) {
    val userId = AuthSession.studentId

    val careerName by viewModel.careerName.collectAsStateWithLifecycle()
    val curriculumState by viewModel.curriculumState.collectAsStateWithLifecycle()
    val studentCoursesState by viewModel.studentCoursesState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        userId?.let { viewModel.loadStudentCurriculum(it) }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1 && userId != null &&
            studentCoursesState is StudentCoursesUiState.Idle
        ) {
            viewModel.loadStudentCourses(userId)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CareerIndicator(careerName = careerName)

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceLight,
                contentColor = NavyBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Malla Visual", fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Mis Cursos", fontSize = 13.sp) }
                )
            }

            when (selectedTab) {
                0 -> MallaVisualTab(
                    state = curriculumState,
                    onRetry = { userId?.let { viewModel.reloadCurriculum(it) } },
                    onCourseClick = onCourseClick
                )
                1 -> MisCursosTab(
                    state = studentCoursesState,
                    onRetry = { userId?.let { viewModel.loadStudentCourses(it) } },
                    onUpdateStatus = { courseId, status, grade, semester, year ->
                        userId?.let { viewModel.updateCourseStatus(it, courseId, status, grade, semester, year) }
                    }
                )
            }
        }
    }
}

// ─── Career Indicator (compact, auto-detected) ───────────────────────────────

@Composable
private fun CareerIndicator(careerName: String?) {
    if (careerName == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(NavyBlue.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = careerName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = NavyBlue,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Malla Visual Tab ────────────────────────────────────────────────────────

@Composable
private fun MallaVisualTab(
    state: CurriculumUiState,
    onRetry: () -> Unit,
    onCourseClick: (courseId: Int) -> Unit = {}
) {
    when (state) {
        is CurriculumUiState.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Cargando tu malla curricular…",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
        is CurriculumUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NavyBlue)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando malla…", color = TextSecondary)
                }
            }
        }
        is CurriculumUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text(state.message, color = CrimsonRed, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reintentar")
                    }
                }
            }
        }
        is CurriculumUiState.Success -> CurriculumZigzagList(levels = state.levels, onCourseClick = onCourseClick)
    }
}

@Composable
private fun CurriculumZigzagList(
    levels: List<CurriculumLevelDto>,
    onCourseClick: (courseId: Int) -> Unit = {}
) {
    val items = remember(levels) { buildMallaItems(levels) }

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay cursos en esta carrera.", color = TextSecondary)
        }
        return
    }

    // Status legend
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDot("Pendiente", Disabled)
        StatusDot("En Curso", EventBlue)
        StatusDot("Aprobado", EventGreen)
        StatusDot("Reprobado", CrimsonRed)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        itemsIndexed(items) { _, item ->
            when (item) {
                is MallaItem.SemesterHeader -> SemesterHeaderItem(item)
                is MallaItem.SectionHeader -> SectionHeaderItem(item.title)
                is MallaItem.CourseEntry -> ZigzagCourseItem(item, onCourseClick = onCourseClick)
            }
        }
    }
}

@Composable
private fun SectionHeaderItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(NavyBlue.copy(alpha = 0.15f)))
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Box(modifier = Modifier.weight(1f).height(1.dp).background(NavyBlue.copy(alpha = 0.15f)))
    }
}

@Composable
private fun SemesterHeaderItem(item: MallaItem.SemesterHeader) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top connector circle
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(NavyBlue)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pill header badge
        Surface(
            shape = RoundedCornerShape(50),
            color = SurfaceLight,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBlue.copy(alpha = 0.25f))
        ) {
            Text(
                text = "NIVEL ${item.level}  ·  CICLO ${item.semester}  ·  ${item.totalCredits} CR",
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ZigzagCourseItem(
    item: MallaItem.CourseEntry,
    onCourseClick: (courseId: Int) -> Unit = {}
) {
    val course = item.course

    // Vertical connector dashes above card (except first in section)
    if (!item.isFirstInSection) {
        DashedConnector(fromLeft = !item.isLeft, toLeft = item.isLeft)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp)
    ) {
        ZigzagCourseCard(
            course = course,
            modifier = Modifier
                .fillMaxWidth(0.68f)
                .align(if (item.isLeft) Alignment.CenterStart else Alignment.CenterEnd)
                .clickable { onCourseClick(course.id) }
        )
    }
}

@Composable
private fun DashedConnector(fromLeft: Boolean, toLeft: Boolean) {
    val lineColor = NavyBlue.copy(alpha = 0.25f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        val dashLen = 5.dp.toPx()
        val gap = 4.dp.toPx()
        // Start X: center of previous card (left or right)
        val startX = if (fromLeft) size.width * 0.34f else size.width * 0.66f
        val endX = if (toLeft) size.width * 0.34f else size.width * 0.66f
        val steps = 6
        for (i in 0 until steps) {
            val t0 = i.toFloat() / steps
            val t1 = (i.toFloat() + 0.6f) / steps
            drawLine(
                color = lineColor,
                start = Offset(startX + (endX - startX) * t0, size.height * t0),
                end = Offset(startX + (endX - startX) * t1, size.height * t1),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ZigzagCourseCard(course: CurriculumCourseDto, modifier: Modifier = Modifier) {
    val (cardBg, textColor, badgeBg) = statusCardColors(course.status)
    val statusIcon = when (course.status) {
        "Aprobado" -> Icons.Default.CheckCircle
        "EnCurso" -> Icons.Default.Schedule
        "Reprobado" -> Icons.Default.Cancel
        else -> Icons.Default.RadioButtonUnchecked
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Code + credits row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = course.code,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
                Text(
                    text = "${course.credits} cr",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Course name
            Text(
                text = course.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = textColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            val isActuallyElective = course.isElective || (course.electiveType != null && course.electiveType != "Obligatorio")
            if (isActuallyElective) {
                Spacer(modifier = Modifier.height(4.dp))
                val electiveLabel = when (course.electiveType) {
                    "OptativoDisciplinario" -> "Optativo Disciplinario"
                    "OptativoLibre" -> "Optativo Libre"
                    else -> "Optativo"
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = textColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = electiveLabel.uppercase(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = statusLabel(course.status),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
                if (course.finalGrade != null) {
                    Text(
                        text = "· %.1f".format(course.finalGrade),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
    }
}

// ─── Mis Cursos Tab ──────────────────────────────────────────────────────────

@Composable
private fun MisCursosTab(
    state: StudentCoursesUiState,
    onRetry: () -> Unit,
    onUpdateStatus: (courseId: Int, status: String, grade: Double?, semester: Int?, year: Int?) -> Unit
) {
    when (state) {
        is StudentCoursesUiState.Idle,
        is StudentCoursesUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyBlue)
            }
        }
        is StudentCoursesUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text(state.message, color = CrimsonRed, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reintentar")
                    }
                }
            }
        }
        is StudentCoursesUiState.Success -> StudentCoursesList(
            courses = state.courses,
            onUpdateStatus = onUpdateStatus
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentCoursesList(
    courses: List<StudentCourseProgressDto>,
    onUpdateStatus: (courseId: Int, status: String, grade: Double?, semester: Int?, year: Int?) -> Unit
) {
    var filterStatus by remember { mutableStateOf<String?>(null) }
    val statuses = listOf("Pendiente", "EnCurso", "Aprobado", "Reprobado")
    val filtered = if (filterStatus != null) courses.filter { it.status == filterStatus } else courses
    var editingCourse by remember { mutableStateOf<StudentCourseProgressDto?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val todosSelected = filterStatus == null
            FilterChip(
                selected = todosSelected,
                onClick = { filterStatus = null },
                label = { Text("Todos", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NavyBlue,
                    selectedLabelColor = Color.White,
                    labelColor = NavyBlue
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = todosSelected,
                    borderColor = NavyBlue.copy(alpha = 0.5f),
                    selectedBorderColor = NavyBlue,
                    borderWidth = 1.dp
                )
            )
            statuses.forEach { status ->
                val isSelected = filterStatus == status
                val (_, accent) = statusColors(status)
                FilterChip(
                    selected = isSelected,
                    onClick = { filterStatus = if (isSelected) null else status },
                    label = { Text(statusLabel(status), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accent,
                        selectedLabelColor = Color.White,
                        labelColor = accent
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = accent.copy(alpha = 0.5f),
                        selectedBorderColor = accent,
                        borderWidth = 1.dp
                    )
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay cursos para mostrar.", color = TextSecondary, textAlign = TextAlign.Center)
            }
        } else {
            Text(
                text = "${filtered.size} curso${if (filtered.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.courseId }) { course ->
                    StudentCourseCard(course = course, onEditClick = { editingCourse = course })
                }
            }
        }
    }

    editingCourse?.let { course ->
        UpdateStatusDialog(
            course = course,
            onDismiss = { editingCourse = null },
            onConfirm = { status, grade, semester, year ->
                onUpdateStatus(course.courseId, status, grade, semester, year)
                editingCourse = null
            }
        )
    }
}

@Composable
private fun StudentCourseCard(
    course: StudentCourseProgressDto,
    onEditClick: () -> Unit
) {
    val (_, accentColor, _) = statusCardColors(course.status)
    val (lightBg, _) = statusColors(course.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onEditClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = course.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Año ${course.level}, Ciclo ${course.term} · ${course.credits} cr",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (course.year != null && course.semester != null) {
                    Text(
                        text = "${course.year} · Sem ${course.semester}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(8.dp), color = lightBg) {
                    Text(
                        text = statusLabel(course.status),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (course.finalGrade != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.1f".format(course.finalGrade),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = accentColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateStatusDialog(
    course: StudentCourseProgressDto,
    onDismiss: () -> Unit,
    onConfirm: (status: String, grade: Double?, semester: Int?, year: Int?) -> Unit
) {
    val statuses = listOf("Pendiente", "EnCurso", "Aprobado", "Reprobado")
    var selectedStatus by remember { mutableStateOf(course.status) }
    var gradeText by remember { mutableStateOf(course.finalGrade?.toString() ?: "") }
    var yearText by remember { mutableStateOf(course.year?.toString() ?: "") }
    var selectedSemester by remember { mutableStateOf<Int?>(course.semester) }
    var statusExpanded by remember { mutableStateOf(false) }
    var semesterExpanded by remember { mutableStateOf(false) }

    val needsDateFields = selectedStatus != "Pendiente"
    val needsGradeField = selectedStatus == "Aprobado" || selectedStatus == "Reprobado"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(course.name, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(course.code, style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                // status dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it }
                ) {
                    OutlinedTextField(
                        value = statusLabel(selectedStatus),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(statusLabel(status)) },
                                onClick = { selectedStatus = status; statusExpanded = false }
                            )
                        }
                    }
                }

                if (needsDateFields) {
                    // Cycle dropdown (1 o 2)
                    ExposedDropdownMenuBox(
                        expanded = semesterExpanded,
                        onExpandedChange = { semesterExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSemester?.let { "Ciclo $it" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ciclo") },
                            placeholder = { Text("Seleccionar ciclo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semesterExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = semesterExpanded,
                            onDismissRequest = { semesterExpanded = false }
                        ) {
                            listOf(1, 2).forEach { ciclo ->
                                DropdownMenuItem(
                                    text = { Text("Ciclo $ciclo") },
                                    onClick = { selectedSemester = ciclo; semesterExpanded = false }
                                )
                            }
                        }
                    }

                    // year
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { if (it.length <= 4) yearText = it.filter { c -> c.isDigit() } },
                        label = { Text("Año") },
                        placeholder = { Text("Ej: 2025") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }

                if (needsGradeField) {
                    OutlinedTextField(
                        value = gradeText,
                        onValueChange = { gradeText = it },
                        label = { Text("Nota final (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val semester = if (needsDateFields) selectedSemester else null
                    val year = if (needsDateFields) yearText.toIntOrNull() else null
                    val grade = if (needsGradeField) gradeText.toDoubleOrNull() else null
                    onConfirm(selectedStatus, grade, semester, year)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

/** Returns (cardBackground, textColor, badgeBackground) for zigzag cards */
private fun statusCardColors(status: String): Triple<Color, Color, Color> = when (status) {
    "Aprobado" -> Triple(EventGreen, Color.White, EventGreen.copy(alpha = 0.6f))
    "EnCurso" -> Triple(EventBlue, Color.White, EventBlue.copy(alpha = 0.6f))
    "Reprobado" -> Triple(CrimsonRed, Color.White, CrimsonRed.copy(alpha = 0.6f))
    else -> Triple(SurfaceLight, TextPrimary, Disabled.copy(alpha = 0.3f))
}

/** Returns (lightBackground, accentColor) for list cards */
private fun statusColors(status: String): Pair<Color, Color> = when (status) {
    "Aprobado" -> Pair(EventGreen.copy(alpha = 0.12f), EventGreen)
    "EnCurso" -> Pair(EventBlue.copy(alpha = 0.12f), EventBlue)
    "Reprobado" -> Pair(CrimsonRed.copy(alpha = 0.12f), CrimsonRed)
    else -> Pair(Disabled.copy(alpha = 0.18f), TextSecondary)
}

private fun statusLabel(status: String): String = when (status) {
    "EnCurso" -> "En Curso"
    else -> status
}
