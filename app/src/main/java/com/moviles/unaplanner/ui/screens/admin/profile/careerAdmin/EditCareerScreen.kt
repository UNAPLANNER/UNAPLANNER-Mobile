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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        degreeOptions = viewModel.degreeOptions,
        onNameChange = viewModel::onNameChange,
        onDegreeChange = viewModel::onDegreeChange,
        onPlanYearChange = viewModel::onPlanYearChange,
        onSchoolChange = viewModel::onSchoolChange,
        onBachelorCreditsChange = viewModel::onBachelorCreditsChange,
        onDiplomaCreditsChange = viewModel::onDiplomaCreditsChange,
        onDegreeCreditsChange = viewModel::onDegreeCreditsChange,
        onOfficialResolutionChange = viewModel::onOfficialResolutionChange,
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
    degreeOptions: List<String>,
    onNameChange: (String) -> Unit,
    onDegreeChange: (String) -> Unit,
    onPlanYearChange: (String) -> Unit,
    onSchoolChange: (String) -> Unit,
    onBachelorCreditsChange: (String) -> Unit,
    onDiplomaCreditsChange: (String) -> Unit,
    onDegreeCreditsChange: (String) -> Unit,
    onOfficialResolutionChange: (String) -> Unit,
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
        EditFormHandle()

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
            EditCareerErrorCard(message = uiState.error, onDismiss = onClearError)
        }

        EditCareerField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = "Nombre de la carrera *",
            placeholder = "Ej: Ingenieria en Sistemas...",
            error = uiState.fieldErrors["Name"]
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EditDegreeSelector(
                value = uiState.degree,
                onValueChange = onDegreeChange,
                options = degreeOptions,
                label = "Grado *",
                error = uiState.fieldErrors["Degree"],
                modifier = Modifier.weight(1f)
            )
            EditCareerField(
                value = uiState.planYear,
                onValueChange = onPlanYearChange,
                label = "Año del plan",
                placeholder = "2022",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["PlanYear"],
                modifier = Modifier.weight(1f)
            )
        }

        EditCareerField(
            value = uiState.school,
            onValueChange = onSchoolChange,
            label = "Escuela *",
            placeholder = "Escuela de Informatica",
            error = uiState.fieldErrors["School"]
        )

        if (requiresSpecificDegreeCredits(uiState.degree)) {
            EditCareerField(
                value = uiState.degreeCredits,
                onValueChange = onDegreeCreditsChange,
                label = "Cr. ${uiState.degree}",
                placeholder = "140",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["DegreeCredits"]
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EditCareerField(
                    value = uiState.bachelorCredits,
                    onValueChange = onBachelorCreditsChange,
                    label = "Cr. Bachillerato",
                    placeholder = "140",
                    keyboardType = KeyboardType.Number,
                    error = uiState.fieldErrors["BachelorCredits"],
                    modifier = Modifier.weight(1f)
                )
                EditCareerField(
                    value = uiState.diplomaCredits,
                    onValueChange = onDiplomaCreditsChange,
                    label = "Cr. Diplomado",
                    placeholder = "88",
                    keyboardType = KeyboardType.Number,
                    error = uiState.fieldErrors["DiplomaCredits"],
                    modifier = Modifier.weight(1f)
                )
            }
        }

        EditCareerField(
            value = uiState.officialResolution,
            onValueChange = onOfficialResolutionChange,
            label = "Resolucion oficial *",
            placeholder = "P.UNA-0000-00",
            error = uiState.fieldErrors["OfficialResolution"]
        )

        EditActiveToggle(
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
private fun EditFormHandle() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Spacer(
            modifier = Modifier
                .width(34.dp)
                .height(4.dp)
                .background(Color(0xFFD8DDE8), RoundedCornerShape(50))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDegreeSelector(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        EditCareerLabel(label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF5F6F8E)
                    )
                },
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = editTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
        EditCareerError(error)
    }
}

@Composable
private fun EditActiveToggle(
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    FilterChip(
        selected = isActive,
        onClick = { onActiveChange(!isActive) },
        label = {
            Text(text = if (isActive) "Esta activa" else "Inactiva", fontWeight = FontWeight.Bold)
        },
        leadingIcon = if (isActive) {
            {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            null
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
private fun EditCareerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        EditCareerLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFF9AA8BE)) },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = editTextFieldColors()
        )
        EditCareerError(error)
    }
}

@Composable
private fun EditCareerLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF26324D)
    )
}

@Composable
private fun EditCareerError(error: String?) {
    if (error != null) {
        Text(
            text = error,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CrimsonRed,
    unfocusedBorderColor = Color(0xFFE1E7F0),
    cursorColor = CrimsonRed,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedTextColor = Color(0xFF07134B),
    unfocusedTextColor = Color(0xFF07134B)
)

@Composable
private fun EditCareerErrorCard(message: String, onDismiss: () -> Unit) {
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

private fun requiresSpecificDegreeCredits(degree: String): Boolean {
    return degree in setOf("Licenciatura", "Maestría", "Doctorado")
}
