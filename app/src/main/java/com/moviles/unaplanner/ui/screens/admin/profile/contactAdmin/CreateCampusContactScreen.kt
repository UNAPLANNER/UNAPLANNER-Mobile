package com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.CrimsonRed

@Composable
fun CreateCampusContactScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateCampusContactViewModel = viewModel(factory = CreateCampusContactViewModel.Factory)
) {
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Nuevo Contacto",
                subtitle = "Agregar oficina al directorio",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.error != null) {
                ErrorMessageCard(message = uiState.error, onDismiss = { viewModel.clearError() })
            }

            // Card del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Información del Departamento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ContactField(
                        value = uiState.departmentName,
                        onValueChange = viewModel::onDepartmentNameChange,
                        label = "Nombre del Departamento",
                        placeholder = "Ej: Registro Académico",
                        icon = Icons.Default.Business,
                        error = uiState.fieldErrors["DepartmentName"] ?: uiState.fieldErrors["DepartamentName"]
                    )

                    ContactField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = "Teléfono",
                        placeholder = "Ej: 2766-6001",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        error = uiState.fieldErrors["Phone"]
                    )

                    ContactField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Correo Electrónico (Opcional)",
                        placeholder = "Ej: registro@una.ac.cr",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        error = uiState.fieldErrors["Email"]
                    )

                    ContactField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Descripción (Opcional)",
                        placeholder = "Breve descripción de servicios...",
                        icon = Icons.Default.Description,
                        singleLine = false,
                        minLines = 3,
                        error = uiState.fieldErrors["Description"]
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.createContact() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Contacto", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ContactField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (error != null) CrimsonRed else Color.Black, // Forzamos negro para visibilidad
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = if (!error.isNullOrBlank()) CrimsonRed else Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            isError = !error.isNullOrBlank(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = CrimsonRed,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White
            )
        )
        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                color = CrimsonRed,
                style = MaterialTheme.typography.bodyMedium, // Aumentado para legibilidad
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun ErrorMessageCard(message: String, onDismiss: () -> Unit) {
    Surface(
        color = CrimsonRed, // Fondo rojo sólido para máxima visibilidad
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Error, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Color.White, // Texto blanco sobre fondo rojo
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }
    }
}
