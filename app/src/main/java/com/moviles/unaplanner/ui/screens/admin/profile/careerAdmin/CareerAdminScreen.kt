package com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.TextSecondary

@Composable
fun CareerAdminScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onNavigateToSection: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Carreras",
                subtitle = "Gestion academica",
                showBackButton = true,
                onBackClick = onBackClick,
                showAddButton = true,
                onAddClick = onAddClick
            )
        },
        bottomBar = {
            AdminAppBottomNavBar(
                selectedIndex = 1,
                onItemSelected = onNavigateToSection
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            CareerAdminContent()
        }
    }
}

@Composable
fun CareerAdminContent(
    viewModel: CareerAdminViewModel = viewModel(factory = CareerAdminViewModel.Factory),
    onEditCareer: (Career) -> Unit = {},
    onViewStudyPlan: (Career) -> Unit = {}
) {
    val uiState = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        CareerSearchField(
            query = uiState.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange
        )

        when {
            uiState.isLoading && uiState.careers.isEmpty() -> CareerLoadingState()
            uiState.error != null && uiState.careers.isEmpty() -> CareerMessageState(
                title = "No se pudieron cargar las carreras",
                message = uiState.error,
                onRetry = viewModel::loadCareers
            )
            uiState.careers.isEmpty() -> CareerMessageState(
                title = "No hay carreras registradas",
                message = "Las carreras disponibles apareceran aqui.",
                onRetry = viewModel::loadCareers
            )
            uiState.filteredCareers.isEmpty() -> NoCareerResultsState()
            else -> CareerList(
                careers = uiState.filteredCareers,
                onEditCareer = onEditCareer,
                onViewStudyPlan = onViewStudyPlan
            )
        }

        if (uiState.isLoading && uiState.careers.isNotEmpty()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun CareerSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, top = 14.dp, end = 18.dp),
        placeholder = {
            Text(
                text = "Buscar carrera...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF7586A8)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                androidx.compose.material3.IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar busqueda",
                        tint = Color(0xFF7586A8)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFFB6C5E4),
            unfocusedBorderColor = Color(0xFFDDE5F3),
            focusedTextColor = Color(0xFF07134B),
            unfocusedTextColor = Color(0xFF07134B)
        )
    )
}

@Composable
private fun CareerList(
    careers: List<Career>,
    onEditCareer: (Career) -> Unit,
    onViewStudyPlan: (Career) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, top = 14.dp, end = 18.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(careers, key = { it.id }) { career ->
            CareerListItem(
                career = career,
                onEditCareer = onEditCareer,
                onViewStudyPlan = onViewStudyPlan
            )
        }
    }
}

@Composable
private fun CareerListItem(
    career: Career,
    onEditCareer: (Career) -> Unit,
    onViewStudyPlan: (Career) -> Unit
) {
    val accent = careerAccent(career.id)
    val outline = if (accent.hasOutline) accent.line else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, outline),
        onClick = { onViewStudyPlan(career) }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(accent.start, accent.end)))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = career.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                CareerStatusBadge(isActive = career.isStatus != false)
                Spacer(modifier = Modifier.width(8.dp))
                CareerEditButton(onClick = { onEditCareer(career) })
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CareerSummaryValue(
                        label = "Creditos",
                        value = career.totalCredits.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricDivider()
                    CareerSummaryValue(
                        label = "Cursos",
                        value = (career.courseCount ?: 0).toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricDivider()
                    CareerSummaryValue(
                        label = "Niveles",
                        value = (career.levelCount ?: 0).toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            accent.line,
                            RoundedCornerShape(50)
                        )
                )

                HorizontalDivider(color = Color(0xFFE8EDF6))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CareerChip(
                        text = if (career.currentStudyPlanId != null) "Plan vigente" else "Sin plan"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CareerChip(
                        text = career.currentStudyPlanYear?.let { "Plan $it" } ?: "Sin plan",
                        highlight = true
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { onViewStudyPlan(career) },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE20E2D),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Ver plan",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CareerEditButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.18f),
        contentColor = Color.White,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Editar",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CareerStatusBadge(isActive: Boolean) {
    val containerColor = if (isActive) {
        Color(0xFFD8FAD8)
    } else {
        Color(0xFFFFEDD3)
    }
    val contentColor = if (isActive) Color(0xFF079545) else Color(0xFFD46A00)

    Surface(
        color = containerColor,
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
                text = if (isActive) "Activa" else "Inactiva",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CareerSummaryValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF07134B),
            fontWeight = FontWeight.ExtraBold
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun MetricDivider() {
    Box(
        modifier = Modifier
            .height(38.dp)
            .width(1.dp)
            .background(Color(0xFFE8EDF6))
    )
}

@Composable
private fun CareerChip(text: String, highlight: Boolean = false) {
    Surface(
        color = if (highlight) Color(0xFFEAF3FF) else Color(0xFFF0F3F8),
        contentColor = if (highlight) Color(0xFF2675EA) else Color(0xFF0C1948),
        shape = RoundedCornerShape(9.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class CareerAccent(
    val start: Color,
    val end: Color,
    val line: Color,
    val hasOutline: Boolean = false
)

private fun careerAccent(id: Int): CareerAccent {
    return when ((id - 1).mod(7)) {
        0 -> CareerAccent(
            start = Color(0xFF061450),
            end = Color(0xFF283A73),
            line = Color(0xFF061450),
            hasOutline = true
        )
        1 -> CareerAccent(
            start = Color(0xFF2450B6),
            end = Color(0xFF4A84F4),
            line = Color(0xFF3470EE)
        )
        2 -> CareerAccent(
            start = Color(0xFF126A39),
            end = Color(0xFF35B66E),
            line = Color(0xFF25A65B)
        )
        3 -> CareerAccent(
            start = Color(0xFF0B6361),
            end = Color(0xFF2AAEAA),
            line = Color(0xFF169692)
        )
        4 -> CareerAccent(
            start = Color(0xFFB64B1D),
            end = Color(0xFFF07222),
            line = Color(0xFFE56220)
        )
        5 -> CareerAccent(
            start = Color(0xFF3B66E1),
            end = Color(0xFF79B7FF),
            line = Color(0xFF5A9EF5)
        )
        else -> CareerAccent(
            start = Color(0xFF5120A9),
            end = Color(0xFF9545ED),
            line = Color(0xFF7F39DA)
        )
    }
}

@Composable
private fun CareerLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CareerMessageState(
    title: String,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onRetry) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

@Composable
private fun NoCareerResultsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No se encontraron carreras",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Prueba con otro nombre, codigo o campus.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
