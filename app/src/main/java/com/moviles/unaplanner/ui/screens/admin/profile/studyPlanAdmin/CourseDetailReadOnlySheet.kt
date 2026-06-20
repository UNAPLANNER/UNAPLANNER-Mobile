package com.moviles.unaplanner.ui.screens.admin.profile.studyPlanAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.data.remote.model.StudyPlanCourseDetail
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.data.remote.model.UpdateStudyPlanCourseRequest
import com.moviles.unaplanner.data.repository.AdminRepository
import com.moviles.unaplanner.data.repository.ApiResult
import com.moviles.unaplanner.ui.theme.CrimsonRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailReadOnlySheet(
    studyPlan: StudyPlanDetail,
    course: StudyPlanCourseDetail,
    onDismiss: () -> Unit,
    onCourseUpdated: (StudyPlanDetail) -> Unit = {}
) {
    val repository = remember { AdminRepository() }
    val coroutineScope = rememberCoroutineScope()
    var isEditing by remember(course.id) { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showDeleteSuccess by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var updatedStudyPlan by remember { mutableStateOf<StudyPlanDetail?>(null) }

    var code by remember(course.id) { mutableStateOf(course.code) }
    var name by remember(course.id) { mutableStateOf(course.name) }
    var credits by remember(course.id) { mutableStateOf(course.credits.toString()) }
    var theoryHours by remember(course.id) { mutableStateOf(course.theoryHours.toString()) }
    var practiceHours by remember(course.id) { mutableStateOf(course.practiceHours.toString()) }
    var labHours by remember(course.id) { mutableStateOf(course.labHours.toString()) }
    var level by remember(course.id) { mutableStateOf(course.level.toString()) }
    var term by remember(course.id) { mutableStateOf(course.term.toString()) }
    var electiveType by remember(course.id) { mutableStateOf(if (course.isElective) course.electiveType ?: "OptativoDisciplinario" else "Obligatorio") }
    var isActive by remember(course.id) { mutableStateOf(true) }
    var prerequisiteMode by remember(course.id) {
        mutableStateOf(if (course.prerequisites.isEmpty()) "No presenta" else "Cursos")
    }
    var prerequisiteSearch by remember(course.id) { mutableStateOf("") }
    var selectedPrerequisiteIds by remember(course.id) {
        mutableStateOf(course.prerequisites.map { it.courseId }.toSet())
    }
    val availablePrerequisites = remember(studyPlan, course.id) {
        studyPlan.levels
            .flatMap { studyPlanLevel -> studyPlanLevel.semesters }
            .flatMap { semester -> semester.courses }
            .filter { availableCourse -> availableCourse.id != course.id }
            .distinctBy { availableCourse -> availableCourse.id }
            .sortedBy { availableCourse -> availableCourse.code }
    }

    fun updateCourse() {
        val request = buildUpdateRequest(
            code = code,
            name = name,
            credits = credits,
            theoryHours = theoryHours,
            practiceHours = practiceHours,
            labHours = labHours,
            level = level,
            term = term,
            electiveType = electiveType,
            isActive = isActive,
            prerequisiteIds = if (prerequisiteMode == "Cursos") selectedPrerequisiteIds else emptySet()
        )

        if (request == null) {
            error = "Revisa los campos numericos y obligatorios."
            return
        }

        coroutineScope.launch {
            isLoading = true
            error = null
            when (val result = repository.updateStudyPlanCourse(studyPlan.id, course.id, request)) {
                is ApiResult.Success -> {
                    updatedStudyPlan = result.data
                    isLoading = false
                    isEditing = false
                    showSuccess = true
                }
                is ApiResult.Error -> {
                    isLoading = false
                    error = result.message ?: "No se pudo actualizar el curso."
                }
            }
        }
    }

    fun deleteCourse() {
        coroutineScope.launch {
            isDeleting = true
            error = null
            when (val result = repository.deleteStudyPlanCourse(studyPlan.id, course.id)) {
                is ApiResult.Success -> {
                    updatedStudyPlan = result.data
                    isDeleting = false
                    showDeleteConfirmation = false
                    showDeleteSuccess = true
                }
                is ApiResult.Error -> {
                    isDeleting = false
                    showDeleteConfirmation = false
                    error = result.message ?: "No se pudo eliminar el curso."
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SheetHandle()
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = if (isEditing) "Editar Curso" else "Detalle del Curso",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF07134B)
                    )
                    Text(
                        text = if (isEditing) "Actualiza la informacion registrada" else "Informacion registrada en el plan de estudios",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8A98AF)
                    )
                }
                if (!isEditing) {
                    Button(
                        onClick = { isEditing = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF061450))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            error?.let {
                Text(text = it.take(180), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            CourseFormField(value = studyPlan.careerName, onValueChange = {}, label = "Carrera", placeholder = "", enabled = false)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CourseFormField(value = code, onValueChange = { code = it.uppercase() }, label = "Codigo *", placeholder = "EIF200", enabled = isEditing, modifier = Modifier.weight(1f))
                CourseFormField(value = credits, onValueChange = { credits = digits(it) }, label = "Creditos *", placeholder = "3", enabled = isEditing, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
            }
            CourseFormField(value = name, onValueChange = { name = it }, label = "Nombre del curso *", placeholder = "Ej: Fundamentos de Informatica", enabled = isEditing)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CourseSelector(
                    value = level,
                    onValueChange = { level = it },
                    options = listOf("1", "2", "3", "4"),
                    label = "Nivel *",
                    enabled = isEditing,
                    modifier = Modifier.weight(1f)
                )
                CourseSelector(
                    value = term,
                    onValueChange = { term = it },
                    options = listOf("1", "2"),
                    label = "Ciclo *",
                    enabled = isEditing,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CourseFormField(value = theoryHours, onValueChange = { theoryHours = digits(it) }, label = "H. teoria *", placeholder = "2", enabled = isEditing, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                CourseFormField(value = practiceHours, onValueChange = { practiceHours = digits(it) }, label = "H. practica *", placeholder = "0", enabled = isEditing, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                CourseFormField(value = labHours, onValueChange = { labHours = digits(it) }, label = "H. lab *", placeholder = "2", enabled = isEditing, keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
            }
            CourseSelector(
                value = electiveType,
                onValueChange = { electiveType = it },
                options = listOf("Obligatorio", "OptativoDisciplinario", "OptativoLibre"),
                label = "Tipo de curso *",
                enabled = isEditing
            )
            CourseActiveToggle(isActive = isActive, enabled = isEditing, onActiveChange = { isActive = it })
            PrerequisiteSelector(
                courses = availablePrerequisites,
                mode = prerequisiteMode,
                modeOptions = listOf("Ingreso", "No presenta", "Cursos"),
                search = prerequisiteSearch,
                selectedIds = selectedPrerequisiteIds,
                onModeChange = { value ->
                    prerequisiteMode = value
                    if (value != "Cursos") selectedPrerequisiteIds = emptySet()
                },
                onSearchChange = { prerequisiteSearch = it },
                onToggle = { prerequisiteId ->
                    selectedPrerequisiteIds = if (prerequisiteId in selectedPrerequisiteIds) {
                        selectedPrerequisiteIds - prerequisiteId
                    } else {
                        selectedPrerequisiteIds + prerequisiteId
                    }
                },
                enabled = isEditing
            )

            if (isEditing) {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && !isDeleting,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = CrimsonRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Eliminar curso", fontWeight = FontWeight.Bold, color = CrimsonRed)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Cancelar", fontWeight = FontWeight.Bold, color = Color(0xFF344256))
                }
                if (isEditing) {
                    Button(
                        onClick = { updateCourse() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        enabled = !isLoading && !isDeleting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Actualizar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showSuccess) {
        CourseUpdatedDialog {
            showSuccess = false
            updatedStudyPlan?.let(onCourseUpdated)
            onDismiss()
        }
    }

    if (showDeleteConfirmation) {
        CourseDeleteConfirmationDialog(
            isDeleting = isDeleting,
            onConfirm = { deleteCourse() },
            onDismiss = { if (!isDeleting) showDeleteConfirmation = false }
        )
    }

    if (showDeleteSuccess) {
        CourseDeletedDialog {
            showDeleteSuccess = false
            updatedStudyPlan?.let(onCourseUpdated)
            onDismiss()
        }
    }
}

private fun buildUpdateRequest(
    code: String,
    name: String,
    credits: String,
    theoryHours: String,
    practiceHours: String,
    labHours: String,
    level: String,
    term: String,
    electiveType: String,
    isActive: Boolean,
    prerequisiteIds: Set<Int>
): UpdateStudyPlanCourseRequest? {
    val parsedCredits = credits.toIntOrNull()
    val parsedTheory = theoryHours.toIntOrNull()
    val parsedPractice = practiceHours.toIntOrNull()
    val parsedLab = labHours.toIntOrNull()
    val parsedLevel = level.toIntOrNull()
    val parsedTerm = term.toIntOrNull()

    if (code.isBlank() || name.isBlank()) return null
    if (parsedCredits == null || parsedCredits !in 1..20) return null
    if (parsedTheory == null || parsedTheory !in 0..20) return null
    if (parsedPractice == null || parsedPractice !in 0..20) return null
    if (parsedLab == null || parsedLab !in 0..20) return null
    if (parsedLevel == null || parsedLevel !in 1..4) return null
    if (parsedTerm == null || parsedTerm !in 1..2) return null

    val normalizedType = electiveType.ifBlank { "Obligatorio" }.trim()
    val prerequisiteCourseIds = prerequisiteIds
        .filter { value -> value > 0 }
        .distinct()

    return UpdateStudyPlanCourseRequest(
        code = code.trim(),
        name = name.trim(),
        credits = parsedCredits,
        theoryHours = parsedTheory,
        practiceHours = parsedPractice,
        labHours = parsedLab,
        level = parsedLevel,
        term = parsedTerm,
        isElective = normalizedType != "Obligatorio",
        electiveType = normalizedType,
        isStatus = isActive,
        prerequisiteCourseIds = prerequisiteCourseIds
    )
}

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
private fun CourseUpdatedDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF079545)
            )
        },
        title = { Text(text = "Curso actualizado", fontWeight = FontWeight.Bold) },
        text = { Text(text = "El curso se actualizo correctamente.") },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(text = "Aceptar", fontWeight = FontWeight.Bold, color = CrimsonRed)
            }
        }
    )
}

@Composable
private fun CourseDeleteConfirmationDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = CrimsonRed
            )
        },
        title = { Text(text = "Eliminar curso", fontWeight = FontWeight.Bold) },
        text = { Text(text = "Esta accion eliminara el curso del plan de estudios. Deseas continuar?") },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isDeleting) {
                if (isDeleting) {
                    CircularProgressIndicator(color = CrimsonRed, modifier = Modifier.size(18.dp))
                } else {
                    Text(text = "Eliminar", fontWeight = FontWeight.Bold, color = CrimsonRed)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) {
                Text(text = "Cancelar", fontWeight = FontWeight.Bold, color = Color(0xFF344256))
            }
        }
    )
}

@Composable
private fun CourseDeletedDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF079545)
            )
        },
        title = { Text(text = "Curso eliminado", fontWeight = FontWeight.Bold) },
        text = { Text(text = "El curso se elimino correctamente.") },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(text = "Aceptar", fontWeight = FontWeight.Bold, color = CrimsonRed)
            }
        }
    )
}

private fun digits(value: String): String = value.filter { it.isDigit() }.take(2)
