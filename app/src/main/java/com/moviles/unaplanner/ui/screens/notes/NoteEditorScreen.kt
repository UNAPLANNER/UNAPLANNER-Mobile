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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.ui.theme.*

@Composable
fun NoteEditorScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: NotesViewModel
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }
    val editorUiState by viewModel.editorState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Manejo de estados y mensajes
    LaunchedEffect(editorUiState) {
        when (val currentState = editorUiState) {
            is NoteEditorUiState.Success -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetEditorState()
                onNavigateBack()
            }
            is NoteEditorUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetEditorState()
            }
            else -> {}
        }
    }

    val isCreating = noteId == "new"
    val isSaving = editorUiState is NoteEditorUiState.Saving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header con botón de atrás
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = NavyBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = if (isCreating) "Nueva Nota" else "Editar Nota",
                    style = MaterialTheme.typography.titleLarge,
                    color = NavyBlue,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Contenido del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Campo Título
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Título *",
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyBlue,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it
                        titleError = false
                    },
                    placeholder = { Text("Ingresa el título de la nota", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    isError = titleError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        errorBorderColor = CrimsonRed,
                        focusedTextColor = Color(0xFF1E293B),
                        unfocusedTextColor = Color(0xFF1E293B)
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
                
                if (titleError) {
                    Text(
                        text = "El título es requerido",
                        style = MaterialTheme.typography.labelSmall,
                        color = CrimsonRed
                    )
                }
            }

            // Campo Contenido
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Contenido",
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyBlue,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Escribe el contenido de la nota...", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = Color(0xFF1E293B),
                        unfocusedTextColor = Color(0xFF1E293B)
                    ),
                    enabled = !isSaving
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        viewModel.createNote(title, content)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isCreating) "Guardar Nota" else "Actualizar Nota",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}
