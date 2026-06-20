package com.moviles.unaplanner.ui.screens.admin.profile.homeAdmin

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.AdminDashboard
import com.moviles.unaplanner.data.remote.model.AdminDashboardCareer
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.TextSecondary

@Composable
fun HomeAdminScreen(
    onNavigateToSection: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            AdminTopBar(
                title = "UNAPlanner Admin",
                isHome = true
            )
        },
        bottomBar = {
            AdminAppBottomNavBar(
                selectedIndex = 0,
                onItemSelected = onNavigateToSection
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeAdminContent(onNavigateToSection = onNavigateToSection)
        }
    }
}

@Composable
fun HomeAdminContent(
    onNavigateToSection: (Int) -> Unit = {},
    onCreateCareerClick: () -> Unit = {},
    onViewStudyPlan: (Career) -> Unit = {},
    viewModel: HomeAdminViewModel = viewModel(factory = HomeAdminViewModel.Factory)
) {
    val uiState = viewModel.uiState

    when {
        uiState.isLoading && uiState.dashboard == null -> DashboardLoadingState()
        uiState.error != null && uiState.dashboard == null -> DashboardMessageState(
            message = uiState.error,
            onRetry = viewModel::loadDashboard
        )
        uiState.dashboard != null -> DashboardContent(
            dashboard = uiState.dashboard,
            onCreateCareerClick = onCreateCareerClick,
            onPlanClick = { onNavigateToSection(1) },
            onViewAllCareersClick = { onNavigateToSection(1) },
            onViewStudyPlan = onViewStudyPlan
        )
    }
}

@Composable
private fun DashboardContent(
    dashboard: AdminDashboard,
    onCreateCareerClick: () -> Unit,
    onPlanClick: () -> Unit,
    onViewAllCareersClick: () -> Unit,
    onViewStudyPlan: (Career) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(start = 18.dp, top = 14.dp, end = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { AdminIdentityCard(dashboard = dashboard) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardMetricCard(
                    value = dashboard.activeCareers.toString(),
                    label = "Carreras activas",
                    iconColor = CrimsonRed,
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    value = dashboard.studyPlans.toString(),
                    label = "Planes de estudio",
                    iconColor = Color(0xFF233A95),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardMetricCard(
                    value = dashboard.registeredCourses.toString(),
                    label = "Cursos registrados",
                    iconColor = Color(0xFF168A4A),
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    value = dashboard.activeStudents.toString(),
                    label = "Estudiantes activos",
                    iconColor = Color(0xFFE79B18),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Text(
                text = "Acciones rapidas",
                color = Color(0xFF07134B),
                fontWeight = FontWeight.ExtraBold
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onCreateCareerClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Carrera", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onPlanClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061450))
                ) {
                    Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Plan", fontWeight = FontWeight.Bold)
                }
            }
        }
        dashboard.latestCareer?.let { latestCareer ->
            item { LatestCareerCard(career = latestCareer) }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Carreras",
                    color = Color(0xFF07134B),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    onClick = onViewAllCareersClick,
                    color = Color.Transparent,
                    contentColor = CrimsonRed
                ) {
                    Text(
                        text = "Ver todas +",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        items(dashboard.careers.take(3), key = { it.id }) { career ->
            DashboardCareerRow(
                career = career,
                onClick = { onViewStudyPlan(career.toCareer(dashboard.campusId, dashboard.campusName)) }
            )
        }
    }
}

@Composable
private fun AdminIdentityCard(dashboard: AdminDashboard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF172467))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CrimsonRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dashboard.adminName,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = dashboard.department ?: dashboard.campusName,
                    color = Color.White.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            StatusPill(isActive = true)
        }
    }
}

@Composable
private fun DashboardMetricCard(
    value: String,
    label: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(min = 126.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = iconColor.copy(alpha = 0.12f),
                contentColor = iconColor,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = when (label) {
                        "Cursos registrados" -> Icons.Default.Folder
                        "Estudiantes activos" -> Icons.Default.Group
                        "Planes de estudio" -> Icons.Default.Book
                        else -> Icons.Default.WorkspacePremium
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                color = Color(0xFF07134B),
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LatestCareerCard(career: AdminDashboardCareer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF061450)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = CrimsonRed.copy(alpha = 0.18f),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Ultima carrera implementada", color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    text = "${career.name} - ${career.courseCount} cursos",
                    color = Color.White.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.62f)
                        .height(3.dp)
                        .background(Color(0xFF1EDB76), RoundedCornerShape(50))
                )
            }
        }
    }
}

@Composable
private fun DashboardCareerRow(
    career: AdminDashboardCareer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF061450), Color(0xFF2F70EA))), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(21.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = career.name,
                    color = Color(0xFF07134B),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${career.totalCredits} cr - ${career.courseCount} cursos - ${career.studyPlanYear?.let { "Plan $it" } ?: "Sin plan"}",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            StatusPill(isActive = career.isStatus)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFC4CCDB), modifier = Modifier.size(18.dp))
        }
    }
}

private fun AdminDashboardCareer.toCareer(campusId: Int, campusName: String): Career {
    return Career(
        id = id,
        campusId = campusId,
        campusName = campusName,
        name = name,
        code = code,
        description = null,
        totalCredits = totalCredits,
        currentStudyPlanId = studyPlanId,
        currentStudyPlanName = studyPlanYear?.let { "Plan $it" },
        currentStudyPlanYear = studyPlanYear,
        courseCount = courseCount,
        levelCount = null,
        isStatus = isStatus,
        createdDate = createdDate
    )
}

@Composable
private fun StatusPill(isActive: Boolean) {
    val contentColor = if (isActive) Color(0xFF079545) else Color(0xFFD46A00)
    Surface(
        color = if (isActive) Color(0xFFD8FAD8) else Color(0xFFFFEDD3),
        contentColor = contentColor,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(contentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isActive) "Activo" else "Revision",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DashboardLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = CrimsonRed)
    }
}

@Composable
private fun DashboardMessageState(
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
        Text(text = "No se pudo cargar el dashboard", color = Color(0xFF07134B), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(text = message, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reintentar")
        }
    }
}
