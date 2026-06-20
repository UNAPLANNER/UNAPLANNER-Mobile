package com.moviles.unaplanner.ui.screens.malla

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.data.remote.model.CourseDetailDto
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.data.remote.model.PrerequisiteDto
import com.moviles.unaplanner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Int,
    onBack: () -> Unit,
    onNavigateToNoteEdit: (Int?) -> Unit = {},
    viewModel: CourseDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userId = AuthSession.currentUser?.id

    LaunchedEffect(courseId) {
        userId?.let { viewModel.loadDetail(it, courseId) }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Malla", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = NavyBlue
                )
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
    val userId = AuthSession.currentUser?.id

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
                0 -> InfoTab(detail = detail)
                1 -> ComingSoonTab()
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

// ─── Info Tab ─────────────────────────────────────────────────────────────────

@Composable
private fun InfoTab(detail: CourseDetailDto) {
    val showTeacherInfo = detail.status == "EnCurso" || detail.status == "Aprobado"

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
    val userId = AuthSession.currentUser?.id

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
        val instant = java.time.Instant.parse(isoDate)
        val local = java.time.ZoneId.systemDefault().let { instant.atZone(it) }
        "%02d/%02d/%d".format(local.dayOfMonth, local.monthValue, local.year)
    } catch (e: Exception) {
        isoDate.take(10)
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
