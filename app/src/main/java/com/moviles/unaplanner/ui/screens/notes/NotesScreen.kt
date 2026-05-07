package com.moviles.unaplanner.ui.screens.notes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.ui.theme.*

@Composable
fun NotesScreen(
    onNavigateToEdit: (Int?) -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        when (val state = uiState) {
            is NotesUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NavyBlue)
                }
            }
            is NotesUiState.Success -> {
                NotesContent(
                    courses = state.courses,
                    notes = state.filteredNotes,
                    onCourseSelected = { viewModel.filterByCourse(it) },
                    onNoteClick = { note -> onNavigateToEdit(note.id) },
                    onNewNoteClick = { onNavigateToEdit(null) }
                )
            }
            is NotesUiState.Empty -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    FilterSection(courses = listOf("Todas"), selectedCourse = "Todas", onCourseSelected = {})
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay notas disponibles", color = TextSecondary)
                        }
                    }
                }
            }
            is NotesUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = CrimsonRed)
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    courses: List<String>,
    selectedCourse: String,
    onCourseSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(courses) { course ->
                val isSelected = selectedCourse == course
                FilterChip(
                    selected = isSelected,
                    onClick = { onCourseSelected(course) },
                    label = { 
                        Text(
                            text = course,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF0F4F8),
                        labelColor = NavyBlue
                    ),
                    border = null,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
fun NotesContent(
    courses: List<String>,
    notes: List<NoteDto>,
    onCourseSelected: (String) -> Unit,
    onNoteClick: (NoteDto) -> Unit,
    onNewNoteClick: () -> Unit
) {
    var currentSelected by remember { mutableStateOf("Todas") }

    Column {
        FilterSection(
            courses = courses,
            selectedCourse = currentSelected,
            onCourseSelected = {
                currentSelected = it
                onCourseSelected(it)
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
        ) {
            items(notes) { note ->
                NoteCard(note = note, onClick = { onNoteClick(note) })
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteDto, onClick: () -> Unit) {
    val (tagColor, tagBg) = when (note.course?.code) {
        "EIF206" -> Color(0xFFE74C3C) to Color(0xFFFFEBEE)
        "EIF207" -> Color(0xFFF39C12) to Color(0xFFFFF3E0)
        "EIF205" -> Color(0xFF27AE60) to Color(0xFFE8F5E9)
        null -> Color(0xFF7F8C8D) to Color(0xFFF5F5F5)
        else -> Color(0xFF2980B9) to Color(0xFFE3F2FD)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = tagBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = note.course?.name ?: note.course?.code ?: "General",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = tagColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = note.content ?: "Sin contenido",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.lastUpdated,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "2 imgs",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
fun NewNotePlaceholder(onClick: () -> Unit) {
    val stroke = Stroke(
        width = 3f, 
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
    )
    val dashColor = NavyBlue.copy(alpha = 0.2f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = dashColor,
                style = stroke,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Add, 
                contentDescription = null, 
                tint = NavyBlue.copy(alpha = 0.4f),
                modifier = Modifier.size(28.dp)
            )
            Text(
                "Nueva nota", 
                color = NavyBlue.copy(alpha = 0.4f), 
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
