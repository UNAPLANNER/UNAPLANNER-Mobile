package com.moviles.unaplanner.ui.screens.admin.profile.studyPlanAdmin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.data.remote.model.StudyPlanCourseDetail
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.data.remote.model.StudyPlanLevel
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.TextSecondary

@Composable
fun StudyPlanDetailScreen(
    career: Career,
    onBackClick: () -> Unit,
    viewModel: StudyPlanDetailViewModel = viewModel(
        key = "study-plan-${career.id}-${career.currentStudyPlanId ?: 0}",
        factory = StudyPlanDetailViewModel.Factory(career)
    )
) {
    val uiState = viewModel.uiState

    when {
        uiState.isLoading && uiState.studyPlan == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null && uiState.studyPlan == null -> {
            StudyPlanMessageState(
                title = "No se pudo cargar el plan",
                message = uiState.error,
                onRetry = viewModel::loadStudyPlan
            )
        }
        uiState.studyPlan != null -> {
            StudyPlanDetailContent(
                studyPlan = uiState.studyPlan,
                onBackClick = onBackClick,
                onStudyPlanUpdated = viewModel::updateStudyPlan
            )
        }
    }
}

@Composable
private fun StudyPlanDetailContent(
    studyPlan: StudyPlanDetail,
    onBackClick: () -> Unit,
    onStudyPlanUpdated: (StudyPlanDetail) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateCourseSheet by remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedCourse by remember { androidx.compose.runtime.mutableStateOf<StudyPlanCourseDetail?>(null) }
    val tabs = listOf("Plan", "Optativos", "Info")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(start = 18.dp, top = 14.dp, end = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { StudyPlanHero(studyPlan = studyPlan) }
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF061450),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CrimsonRed,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
        }
        item { AddCourseActionButton(onClick = { showCreateCourseSheet = true }) }

        when (selectedTab) {
            0 -> {
                if (studyPlan.levels.isEmpty()) {
                    item {
                        EmptyStudyPlanSection(
                            title = "Sin cursos registrados",
                            message = "Este plan aun no tiene cursos. Usa + Curso para registrarlos manualmente."
                        )
                    }
                } else {
                    studyPlan.levels.forEach { level ->
                        item { StudyPlanLevelSection(level = level, onCourseClick = { selectedCourse = it }) }
                    }
                }
            }
            1 -> {
                val electives = studyPlan.levels.flatMap { level ->
                    level.semesters.flatMap { semester ->
                        semester.courses.filter { it.isElective }.map { course -> level.level to course }
                    }
                }
                if (electives.isEmpty()) {
                    item {
                        EmptyStudyPlanSection(
                            title = "Sin cursos optativos",
                            message = "Los cursos optativos apareceran aqui cuando esten registrados."
                        )
                    }
                } else {
                    items(electives) { (level, course) ->
                        StudyPlanCourseCard(
                            course = course,
                            levelLabel = "Nivel $level",
                            forceElectiveStyle = true,
                            onClick = { selectedCourse = course }
                        )
                    }
                }
            }
            else -> {
                item { StudyPlanInfoGrid(studyPlan = studyPlan) }
            }
        }
    }

    if (showCreateCourseSheet) {
        CreateStudyPlanCourseSheet(
            studyPlan = studyPlan,
            onDismiss = { showCreateCourseSheet = false },
            onCourseCreated = { updatedStudyPlan ->
                showCreateCourseSheet = false
                selectedTab = 0
                onStudyPlanUpdated(updatedStudyPlan)
            }
        )
    }

    selectedCourse?.let { course ->
        CourseDetailReadOnlySheet(
            studyPlan = studyPlan,
            course = course,
            onDismiss = { selectedCourse = null }
        )
    }
}

@Composable
private fun StudyPlanHero(studyPlan: StudyPlanDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(Color(0xFF061450), Color(0xFF243B78))))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "PLAN DE ESTUDIOS",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9DAEE7),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = studyPlan.careerName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (studyPlan.effectiveYear == 0) "Plan de estudios pendiente" else studyPlan.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFC9D4F6)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StudyPlanChip(if (studyPlan.effectiveYear == 0) "Sin plan" else "Plan ${studyPlan.effectiveYear}")
                StudyPlanChip(studyPlan.code)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StudyPlanMetric(
                    value = studyPlan.totalCredits.toString(),
                    label = "Creditos",
                    modifier = Modifier.weight(1f)
                )
                StudyPlanMetric(
                    value = studyPlan.courseCount.toString(),
                    label = "Cursos",
                    modifier = Modifier.weight(1f)
                )
                StudyPlanMetric(
                    value = studyPlan.levelCount.toString(),
                    label = "Niveles",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AddCourseActionButton(onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE20E2D), contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Curso", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StudyPlanChip(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.14f),
        contentColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StudyPlanMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.10f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold)
            Text(text = label, color = Color(0xFFC9D4F6), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun StudyPlanLevelSection(
    level: StudyPlanLevel,
    onCourseClick: (StudyPlanCourseDetail) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = Color(0xFFEAF7F0),
            contentColor = Color(0xFF176B3A),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Book, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Nivel ${level.level}", fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = "${level.semesters.size} ciclos",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF5D876F)
                    )
                }
                Text(text = "${level.credits} cr", fontWeight = FontWeight.Bold)
            }
        }

        level.semesters.forEach { semester ->
            Text(
                text = "${semester.semester} CICLO",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            semester.courses.forEach { course ->
                StudyPlanCourseCard(course = course, onClick = { onCourseClick(course) })
            }
        }
    }
}

@Composable
private fun StudyPlanCourseCard(
    course: StudyPlanCourseDetail,
    levelLabel: String? = null,
    forceElectiveStyle: Boolean = false,
    onClick: () -> Unit = {}
) {
    val elective = course.isElective || forceElectiveStyle
    val accent = if (elective) Color(0xFFF28B20) else Color(0xFF2F80ED)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE8EDF6)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = accent.copy(alpha = 0.12f),
                contentColor = accent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = course.code,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF07134B),
                    fontWeight = FontWeight.Bold
                )
                val detail = buildString {
                    if (levelLabel != null) append(levelLabel)
                    if (elective) {
                        if (isNotEmpty()) append(" - ")
                        append(course.electiveType ?: "Optativo")
                    }
                    if (course.prerequisites.isNotEmpty()) {
                        if (isNotEmpty()) append(" - ")
                        append("Req: ")
                        append(course.prerequisites.joinToString { it.code })
                    } else {
                        if (isNotEmpty()) append(" - ")
                        append("Ingreso")
                    }
                }
                Text(
                    text = detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text = "${course.credits} cr",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StudyPlanInfoGrid(studyPlan: StudyPlanDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoStatCard(value = studyPlan.courseCount.toString(), label = "Total cursos", modifier = Modifier.weight(1f))
            InfoStatCard(value = studyPlan.levelCount.toString(), label = "Niveles", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoStatCard(value = studyPlan.cycleCount.toString(), label = "Ciclos", modifier = Modifier.weight(1f))
            InfoStatCard(value = studyPlan.effectiveYear.toString(), label = "Año efectivo", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun InfoStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE8EDF6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = CrimsonRed.copy(alpha = 0.75f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = Color(0xFF07134B), fontWeight = FontWeight.ExtraBold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun EmptyStudyPlanSection(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF07134B))
        Text(text = message, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun StudyPlanMessageState(
    title: String,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(text = message, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed, contentColor = Color.White)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}
