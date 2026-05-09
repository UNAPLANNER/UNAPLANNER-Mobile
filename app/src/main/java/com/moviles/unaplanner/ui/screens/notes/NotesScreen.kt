package com.moviles.unaplanner.ui.screens.notes

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.data.remote.model.NoteDto
import com.moviles.unaplanner.ui.theme.*

@Composable
fun NotesScreen(
    onNavigateToEdit: (Int?) -> Unit,
    viewModel: NotesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val editorState by viewModel.editorState.collectAsState()
    val context = LocalContext.current
    var noteToDelete by remember { mutableStateOf<NoteDto?>(null) }

    // Manejo de mensajes de éxito/error (como al eliminar)
    LaunchedEffect(editorState) {
        when (val state = editorState) {
            is NoteEditorUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetEditorState()
            }
            is NoteEditorUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetEditorState()
            }
            else -> {}
        }
    }

    // Diálogo de confirmación para eliminar
    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Eliminar Nota", color = NavyBlue, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar esta nota? Esta acción no se puede deshacer.", color = TextPrimary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let { viewModel.deleteNote(it.id) }
                        noteToDelete = null
                    }
                ) {
                    Text("Eliminar", color = CrimsonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancelar", color = NavyBlue)
                }
            },
            containerColor = Color.White
        )
    }

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
                    onDeleteClick = { note -> noteToDelete = note },
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
    onDeleteClick: (NoteDto) -> Unit,
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
                NoteCard(
                    note = note, 
                    onClick = { onNoteClick(note) },
                    onDeleteClick = { onDeleteClick(note) }
                )
            }
            
            item {
                NewNotePlaceholder(onClick = onNewNoteClick)
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteDto, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    // Definición de colores según el curso (Verde claro para General)
    val (tagColor, tagBg) = when {
        note.course?.code == "EIF206" -> Color(0xFFD32F2F) to Color(0xFFFFEBEE)
        note.course?.code == "EIF207" -> Color(0xFFF57C00) to Color(0xFFFFF3E0)
        note.course?.code == "EIF205" -> Color(0xFF388E3C) to Color(0xFFE8F5E9)
        note.course == null || note.displayCourseName == "General" -> Color(0xFF2E7D32) to Color(0xFFC8E6C9) // Verde solicitado
        else -> Color(0xFF1976D2) to Color(0xFFE3F2FD) // Azul para otros cursos
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior con etiqueta y botón de eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Etiqueta del curso
                Surface(
                    color = tagBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = note.displayCourseName,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = tagColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Basurero para eliminar
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar nota",
                        tint = CrimsonRed.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Título
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Contenido preliminar
            Text(
                text = note.content ?: "Sin contenido",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            // Footer con fecha y icono
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
                        text = "Detalles",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(start = 4.dp)
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
