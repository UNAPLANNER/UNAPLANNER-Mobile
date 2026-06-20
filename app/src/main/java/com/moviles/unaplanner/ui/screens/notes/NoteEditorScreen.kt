package com.moviles.unaplanner.ui.screens.notes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.data.remote.model.CourseDto
import com.moviles.unaplanner.data.remote.model.StudentCourseProgressDto
import com.moviles.unaplanner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: String?,
    preselectedCourseId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: NotesViewModel
) {
    val context = LocalContext.current
    val editorUiState by viewModel.editorState.collectAsStateWithLifecycle()
    val studentCourses by viewModel.studentCourses.collectAsStateWithLifecycle()

    // Estados del formulario
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf<CourseDto?>(null) }
    var isReadOnly by remember { mutableStateOf(noteId != null && noteId != "new") }
    var titleError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteCourseName by remember { mutableStateOf("General") }

    // Cargar datos de la nota
    LaunchedEffect(noteId) {
        viewModel.loadStudentCourses()
        if (noteId != null && noteId != "new") {
            val note = viewModel.getNoteById(noteId.toInt())
            note?.let {
                title = it.title
                content = it.content ?: ""
                selectedCourse = it.course
                noteCourseName = it.displayCourseName
            }
        }
    }

    // Pre-seleccionar el curso cuando viene desde el detalle de un curso
    LaunchedEffect(studentCourses, preselectedCourseId) {
        if (preselectedCourseId != null && selectedCourse == null && studentCourses.isNotEmpty()) {
            val course = studentCourses.find { it.courseId == preselectedCourseId }
            if (course != null) {
                selectedCourse = CourseDto(id = course.courseId, code = course.code, name = course.name)
            }
        }
    }

    // Manejo de resultados de la API
    LaunchedEffect(editorUiState) {
        when (val state = editorUiState) {
            is NoteEditorUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetEditorState()
                onNavigateBack()
            }
            is NoteEditorUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetEditorState()
            }
            else -> {}
        }
    }

    val isSaving = editorUiState is NoteEditorUiState.Saving

    // Colores personalizados para máxima legibilidad
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        disabledTextColor = TextPrimary,
        errorTextColor = CrimsonRed,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        cursorColor = NavyBlue,
        focusedBorderColor = NavyBlue,
        unfocusedBorderColor = AppDivider,
        disabledBorderColor = AppDivider,
        errorBorderColor = CrimsonRed,
        focusedTrailingIconColor = NavyBlue,
        unfocusedTrailingIconColor = NavyBlue.copy(alpha = 0.7f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = NavyBlue)
                }
                
                Text(
                    text = when {
                        noteId == "new" -> "Nueva Nota"
                        isReadOnly -> "Detalles de Nota"
                        else -> "Editar Nota"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = NavyBlue,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )

                if (noteId != "new" && isReadOnly) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = CrimsonRed)
                    }
                    IconButton(onClick = { isReadOnly = false }) {
                        Icon(Icons.Default.Edit, "Editar", tint = NavyBlue)
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar Nota", color = NavyBlue, fontWeight = FontWeight.Bold) },
                text = { Text("¿Estás seguro de que deseas eliminar esta nota? Esta acción no se puede deshacer.", color = TextPrimary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteNote(noteId!!.toInt())
                        }
                    ) {
                        Text("Eliminar", color = CrimsonRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = NavyBlue)
                    }
                },
                containerColor = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Selector de Curso (ComboBox)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Curso Asociado", style = MaterialTheme.typography.labelMedium, color = NavyBlue, fontWeight = FontWeight.Bold)
                
                ExposedDropdownMenuBox(
                    expanded = expanded && !isReadOnly,
                    onExpandedChange = { if (!isReadOnly) expanded = it }
                ) {
                    OutlinedTextField(
                        value = if (isReadOnly) noteCourseName else (selectedCourse?.name ?: "General (Sin curso)"),
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Selecciona un curso", color = Color.Gray.copy(alpha = 0.5f)) },
                        trailingIcon = {
                            if (!isReadOnly) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                        enabled = true,
                        supportingText = {
                            selectedCourse?.let {
                                Text("Código: ${it.code}", style = TextStyle(fontSize = 12.sp, color = NavyBlue.copy(alpha = 0.6f)))
                            }
                        }
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                            .exposedDropdownSize(true)
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text("General (Sin curso)", color = TextPrimary, fontWeight = FontWeight.Bold) 
                            },
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
                                    selectedCourse = CourseDto(id = course.courseId, code = course.code, name = course.name)
                                    expanded = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Título
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Título *", style = MaterialTheme.typography.labelMedium, color = NavyBlue, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = titleError,
                    readOnly = isReadOnly,
                    enabled = !isSaving,
                    colors = textFieldColors,
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
                    placeholder = { Text("Ej: Resumen de Algoritmos", color = Color.Gray.copy(alpha = 0.6f)) }
                )
            }

            // Contenido
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Contenido", style = MaterialTheme.typography.labelMedium, color = NavyBlue, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 250.dp),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = isReadOnly,
                    enabled = !isSaving,
                    colors = textFieldColors,
                    textStyle = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, color = TextPrimary),
                    placeholder = { Text("Escribe aquí tus apuntes...", color = Color.Gray.copy(alpha = 0.6f)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Acción
            if (!isReadOnly) {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                        } else {
                            if (noteId == "new") {
                                viewModel.createNote(title, content, selectedCourse?.id)
                            } else {
                                viewModel.updateNote(noteId!!.toInt(), title, content, selectedCourse?.id)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Save, null, modifier = Modifier.padding(end = 8.dp))
                        Text(if (noteId == "new") "Guardar Nota" else "Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
