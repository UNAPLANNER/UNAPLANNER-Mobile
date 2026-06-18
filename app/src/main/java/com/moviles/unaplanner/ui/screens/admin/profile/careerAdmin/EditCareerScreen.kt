package com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.Career
import com.moviles.unaplanner.ui.theme.CrimsonRed

@Composable
fun EditCareerScreen(
    career: Career,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EditCareerViewModel = viewModel(
        key = "edit-career-${career.id}",
        factory = EditCareerViewModel.Factory(career)
    )
) {
    val uiState = viewModel.uiState

    EditCareerForm(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onCodeChange = viewModel::onCodeChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onTotalCreditsChange = viewModel::onTotalCreditsChange,
        onActiveChange = viewModel::onActiveChange,
        onUpdateClick = viewModel::updateCareer,
        onCancelClick = onBackClick,
        onClearError = viewModel::clearError
    )

    if (uiState.isSuccess) {
        CareerUpdatedDialog(
            onAccept = {
                viewModel.clearSuccess()
                onSuccess()
            }
        )
    }
}

@Composable
private fun EditCareerForm(
    uiState: EditCareerUiState,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTotalCreditsChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onUpdateClick: () -> Unit,
    onCancelClick: () -> Unit,
    onClearError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FormHandle()

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "Editar Carrera",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF07134B)
            )
            Text(
                text = "Actualiza la informacion registrada",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A98AF)
            )
        }

        if (uiState.error != null) {
            CareerFormErrorCard(message = uiState.error, onDismiss = onClearError)
        }

        CareerEditField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = "Nombre de la carrera *",
            placeholder = "Ej: Ingenieria en Sistemas",
            error = uiState.fieldErrors["Name"]
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CareerEditField(
                value = uiState.code,
                onValueChange = onCodeChange,
                label = "Codigo *",
                placeholder = "LIC",
                error = uiState.fieldErrors["Code"],
                modifier = Modifier.weight(1f)
            )
            CareerEditField(
                value = uiState.totalCredits,
                onValueChange = onTotalCreditsChange,
                label = "Creditos *",
                placeholder = "140",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["TotalCredits"],
                modifier = Modifier.weight(1f)
            )
        }

        CareerEditField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = "Descripcion",
            placeholder = "Informacion general de la carrera",
            error = uiState.fieldErrors["Description"]
        )

        CareerEditStatusToggle(
            isActive = uiState.isActive,
            onActiveChange = onActiveChange
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Cancelar", fontWeight = FontWeight.Bold, color = Color(0xFF344256))
            }

            Button(
                onClick = onUpdateClick,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Actualizar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FormHandle() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Spacer(
            modifier = Modifier
                .width(34.dp)
                .height(4.dp)
                .background(Color(0xFFD8DDE8), RoundedCornerShape(50))
        )
    }
}

@Composable
private fun CareerEditStatusToggle(
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    FilterChip(
        selected = isActive,
        onClick = { onActiveChange(!isActive) },
        label = {
            Text(text = if (isActive) "Esta activa" else "Inactiva", fontWeight = FontWeight.Bold)
        },
        leadingIcon = {
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFD46A00), CircleShape)
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFD8FAD8),
            selectedLabelColor = Color(0xFF079545),
            selectedLeadingIconColor = Color(0xFF079545),
            containerColor = Color(0xFFFFEDD3),
            labelColor = Color(0xFFD46A00)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isActive,
            borderColor = Color(0xFFE1E7F0),
            selectedBorderColor = Color(0xFF62D887)
        )
    )
}

@Composable
private fun CareerEditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF26324D)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFF9AA8BE)) },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CrimsonRed,
                unfocusedBorderColor = Color(0xFFE1E7F0),
                cursorColor = CrimsonRed,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF07134B),
                unfocusedTextColor = Color(0xFF07134B)
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun CareerFormErrorCard(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CareerUpdatedDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF079545)
            )
        },
        title = {
            Text(text = "Carrera actualizada", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "La carrera se actualizo correctamente.")
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(text = "Aceptar", fontWeight = FontWeight.Bold, color = CrimsonRed)
            }
        }
    )
}
