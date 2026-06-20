package com.moviles.unaplanner.ui.screens.admin.profile.studyPlanAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.data.remote.model.StudyPlanCourseDetail
import com.moviles.unaplanner.data.remote.model.StudyPlanDetail
import com.moviles.unaplanner.ui.theme.CrimsonRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailReadOnlySheet(
    studyPlan: StudyPlanDetail,
    course: StudyPlanCourseDetail,
    onDismiss: () -> Unit
) {
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
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Detalle del Curso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF07134B)
                )
                Text(
                    text = "Informacion registrada en el plan de estudios",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A98AF)
                )
            }

            ReadOnlyCourseField(value = studyPlan.careerName, label = "Carrera")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyCourseField(value = course.code, label = "Codigo", modifier = Modifier.weight(1f))
                ReadOnlyCourseField(
                    value = course.credits.toString(),
                    label = "Creditos",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
            ReadOnlyCourseField(value = course.name, label = "Nombre del curso")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReadOnlyCourseField(value = course.theoryHours.toString(), label = "H. teoria", modifier = Modifier.weight(1f))
                ReadOnlyCourseField(value = course.practiceHours.toString(), label = "H. practica", modifier = Modifier.weight(1f))
                ReadOnlyCourseField(value = course.labHours.toString(), label = "H. lab", modifier = Modifier.weight(1f))
            }
            ReadOnlyCourseField(
                value = if (course.isElective) course.electiveType ?: "Optativo" else "Obligatorio",
                label = "Tipo de curso"
            )
            ReadOnlyCourseField(
                value = course.prerequisites.takeIf { it.isNotEmpty() }
                    ?.joinToString { "${it.code} - ${it.name}" }
                    ?: "No presenta",
                label = "Requisitos"
            )

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Cancelar", fontWeight = FontWeight.Bold, color = Color(0xFF344256))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ReadOnlyCourseField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
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
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color(0xFFE1E7F0),
                disabledContainerColor = Color(0xFFF5F7FB),
                disabledTextColor = Color(0xFF07134B),
                focusedBorderColor = CrimsonRed
            )
        )
    }
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
