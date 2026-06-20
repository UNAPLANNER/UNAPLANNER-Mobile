package com.moviles.unaplanner.ui.screens.admin.profile.studyPlanAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ModalBottomSheet
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
import com.moviles.unaplanner.data.remote.model.StudyPlanCourseDetail
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.ui.theme.CrimsonRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStudyPlanCourseSheet(
    studyPlan: StudyPlanDetail,
    onDismiss: () -> Unit,
    onCourseCreated: (StudyPlanDetail) -> Unit,
    viewModel: CreateStudyPlanCourseViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val availablePrerequisites = studyPlan.levels
        .flatMap { level -> level.semesters }
        .flatMap { semester -> semester.courses }
        .distinctBy { course -> course.id }
        .sortedBy { course -> course.code }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.reset()
            onDismiss()
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        CreateStudyPlanCourseForm(
            studyPlan = studyPlan,
            availablePrerequisites = availablePrerequisites,
            uiState = uiState,
            levelOptions = viewModel.levelOptions,
            termOptions = viewModel.termOptions,
            electiveTypeOptions = viewModel.electiveTypeOptions,
            prerequisiteModeOptions = viewModel.prerequisiteModeOptions,
            onCodeChange = viewModel::onCodeChange,
            onNameChange = viewModel::onNameChange,
            onCreditsChange = viewModel::onCreditsChange,
            onTheoryHoursChange = viewModel::onTheoryHoursChange,
            onPracticeHoursChange = viewModel::onPracticeHoursChange,
            onLabHoursChange = viewModel::onLabHoursChange,
            onLevelChange = viewModel::onLevelChange,
            onTermChange = viewModel::onTermChange,
            onElectiveTypeChange = viewModel::onElectiveTypeChange,
            onActiveChange = viewModel::onActiveChange,
            onPrerequisiteModeChange = viewModel::onPrerequisiteModeChange,
            onPrerequisiteSearchChange = viewModel::onPrerequisiteSearchChange,
            onPrerequisiteToggle = viewModel::onPrerequisiteToggle,
            onCreateClick = { viewModel.createCourse(studyPlan.id) },
            onCancelClick = {
                viewModel.reset()
                onDismiss()
            },
            onClearError = viewModel::clearError
        )
    }

    if (uiState.isSuccess && viewModel.createdStudyPlan != null) {
        CourseCreatedDialog(
            onAccept = {
                viewModel.createdStudyPlan?.let { updatedStudyPlan ->
                    viewModel.reset()
                    onCourseCreated(updatedStudyPlan)
                }
            }
        )
    }
}

@Composable
private fun CreateStudyPlanCourseForm(
    studyPlan: StudyPlanDetail,
    availablePrerequisites: List<StudyPlanCourseDetail>,
    uiState: CreateStudyPlanCourseUiState,
    levelOptions: List<String>,
    termOptions: List<String>,
    electiveTypeOptions: List<String>,
    prerequisiteModeOptions: List<String>,
    onCodeChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onCreditsChange: (String) -> Unit,
    onTheoryHoursChange: (String) -> Unit,
    onPracticeHoursChange: (String) -> Unit,
    onLabHoursChange: (String) -> Unit,
    onLevelChange: (String) -> Unit,
    onTermChange: (String) -> Unit,
    onElectiveTypeChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onPrerequisiteModeChange: (String) -> Unit,
    onPrerequisiteSearchChange: (String) -> Unit,
    onPrerequisiteToggle: (Int) -> Unit,
    onCreateClick: () -> Unit,
    onCancelClick: () -> Unit,
    onClearError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SheetHandle()
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "+ Nuevo Curso",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF07134B)
            )
            Text(
                text = "Registra un curso en el plan de estudios",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A98AF)
            )
        }

        if (uiState.error != null) {
            CourseErrorCard(message = uiState.error, onDismiss = onClearError)
        }

        CourseFormField(
            value = studyPlan.careerName,
            onValueChange = {},
            label = "Carrera",
            placeholder = "",
            enabled = false
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CourseFormField(
                value = uiState.code,
                onValueChange = onCodeChange,
                label = "Codigo *",
                placeholder = "EIF200",
                error = uiState.fieldErrors["Code"],
                modifier = Modifier.weight(1f)
            )
            CourseFormField(
                value = uiState.credits,
                onValueChange = onCreditsChange,
                label = "Creditos *",
                placeholder = "3",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["Credits"],
                modifier = Modifier.weight(1f)
            )
        }

        CourseFormField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = "Nombre del curso *",
            placeholder = "Ej: Fundamentos de Informatica",
            error = uiState.fieldErrors["Name"]
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CourseSelector(
                value = uiState.level,
                onValueChange = onLevelChange,
                options = levelOptions,
                label = "Nivel *",
                modifier = Modifier.weight(1f)
            )
            CourseSelector(
                value = uiState.term,
                onValueChange = onTermChange,
                options = termOptions,
                label = "Ciclo *",
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CourseFormField(
                value = uiState.theoryHours,
                onValueChange = onTheoryHoursChange,
                label = "H. teoria *",
                placeholder = "2",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["TheoryHours"],
                modifier = Modifier.weight(1f)
            )
            CourseFormField(
                value = uiState.practiceHours,
                onValueChange = onPracticeHoursChange,
                label = "H. practica *",
                placeholder = "0",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["PracticeHours"],
                modifier = Modifier.weight(1f)
            )
            CourseFormField(
                value = uiState.labHours,
                onValueChange = onLabHoursChange,
                label = "H. lab *",
                placeholder = "2",
                keyboardType = KeyboardType.Number,
                error = uiState.fieldErrors["LabHours"],
                modifier = Modifier.weight(1f)
            )
        }

        CourseSelector(
            value = uiState.electiveType,
            onValueChange = onElectiveTypeChange,
            options = electiveTypeOptions,
            label = "Tipo de curso *",
            error = uiState.fieldErrors["ElectiveType"]
        )

        CourseActiveToggle(isActive = uiState.isActive, onActiveChange = onActiveChange)

        PrerequisiteSelector(
            courses = availablePrerequisites,
            mode = uiState.prerequisiteMode,
            modeOptions = prerequisiteModeOptions,
            search = uiState.prerequisiteSearch,
            selectedIds = uiState.selectedPrerequisiteIds,
            onModeChange = onPrerequisiteModeChange,
            onSearchChange = onPrerequisiteSearchChange,
            onToggle = onPrerequisiteToggle
        )

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
                onClick = onCreateClick,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Crear curso", fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseSelector(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        CourseFieldLabel(label)
        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = !expanded }
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
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                colors = courseTextFieldColors()
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
        CourseFieldError(error)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrerequisiteSelector(
    courses: List<StudyPlanCourseDetail>,
    mode: String,
    modeOptions: List<String>,
    search: String,
    selectedIds: Set<Int>,
    onModeChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggle: (Int) -> Unit,
    enabled: Boolean = true
) {
    val filteredCourses = remember(courses, search) {
        val query = search.trim()
        if (query.isBlank()) {
            courses
        } else {
            courses.filter { course ->
                course.code.contains(query, ignoreCase = true) ||
                    course.name.contains(query, ignoreCase = true)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CourseSelector(
            value = mode,
            onValueChange = onModeChange,
            options = modeOptions,
            label = "Requisito *",
            enabled = enabled
        )

        when (mode) {
            "Ingreso" -> {
                Text(
                    text = "Curso de primer ingreso. No se registran cursos requisito.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A98AF)
                )
            }
            "No presenta" -> {
                Text(
                    text = "Este curso no presenta requisitos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A98AF)
                )
            }
            else -> CoursePrerequisitePicker(
                courses = courses,
                filteredCourses = filteredCourses,
                search = search,
                selectedIds = selectedIds,
                onSearchChange = onSearchChange,
                onToggle = onToggle,
                enabled = enabled
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CoursePrerequisitePicker(
    courses: List<StudyPlanCourseDetail>,
    filteredCourses: List<StudyPlanCourseDetail>,
    search: String,
    selectedIds: Set<Int>,
    onSearchChange: (String) -> Unit,
    onToggle: (Int) -> Unit,
    enabled: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CourseFormField(
            value = search,
            onValueChange = onSearchChange,
            label = "Buscar curso requisito",
            placeholder = "Codigo o nombre del curso",
            enabled = enabled
        )

        if (courses.isEmpty()) {
            Text(
                text = "Aun no hay cursos disponibles para seleccionar requisitos.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A98AF)
            )
        } else if (filteredCourses.isEmpty()) {
            Text(
                text = "No hay cursos que coincidan con la busqueda.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A98AF)
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filteredCourses.forEach { course ->
                    val selected = course.id in selectedIds
                    FilterChip(
                        selected = selected,
                        enabled = enabled,
                        onClick = { onToggle(course.id) },
                        label = { Text("${course.code} - ${course.name}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE8F1FF),
                            selectedLabelColor = Color(0xFF1E62D0),
                            containerColor = Color(0xFFF5F7FB),
                            labelColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }

        if (selectedIds.isNotEmpty()) {
            Text(
                text = "${selectedIds.size} requisito(s) seleccionado(s)",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1E62D0),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CourseActiveToggle(
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    FilterChip(
        selected = isActive,
        onClick = { onActiveChange(!isActive) },
        label = {
            Text(text = if (isActive) "Esta activo" else "Inactivo", fontWeight = FontWeight.Bold)
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
        )
    )
}

@Composable
private fun CourseFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        CourseFieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            placeholder = { Text(placeholder, color = Color(0xFF9AA8BE)) },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = courseTextFieldColors()
        )
        CourseFieldError(error)
    }
}

@Composable
private fun CourseFieldLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF26324D)
    )
}

@Composable
private fun CourseFieldError(error: String?) {
    if (error != null) {
        Text(
            text = error,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun courseTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CrimsonRed,
    unfocusedBorderColor = Color(0xFFE1E7F0),
    disabledBorderColor = Color(0xFFE1E7F0),
    cursorColor = CrimsonRed,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color(0xFFF5F7FB),
    focusedTextColor = Color(0xFF07134B),
    unfocusedTextColor = Color(0xFF07134B),
    disabledTextColor = Color(0xFF334155)
)

@Composable
private fun SheetHandle() {
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
private fun CourseErrorCard(message: String, onDismiss: () -> Unit) {
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
private fun CourseCreatedDialog(onAccept: () -> Unit) {
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
            Text(text = "Curso creado", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "El curso se registro correctamente.")
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(text = "Aceptar", fontWeight = FontWeight.Bold, color = CrimsonRed)
            }
        }
    )
}
