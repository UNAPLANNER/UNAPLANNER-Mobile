package com.moviles.unaplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// NOTA: gpa y lastCycleGpa vienen en escala 0-100 (igual que las notas
// individuales de los cursos), no en escala 0-10. Los umbrales de abajo
// están ajustados a esa escala.
@Composable
fun GpaCard(
    gpa: Double,
    lastCycleGpa: Double,
    modifier: Modifier = Modifier
) {
    val gpaLabel = when {
        gpa >= 90.0 -> "Excelente"
        gpa >= 80.0 -> "Muy bueno"
        gpa >= 70.0 -> "Bueno"
        else        -> "En riesgo"
    }
    val labelColor = when {
        gpa >= 90.0 -> Color(0xFF4ECFA3)
        gpa >= 80.0 -> Color(0xFF4ECFA3)
        gpa >= 70.0 -> Color(0xFFF5C842)
        else        -> Color(0xFFE84040)
    }
    val labelBg = when {
        gpa >= 70.0 -> Color(0xFF0F3D2E)
        else        -> Color(0xFF3D0F0F)
    }
    val trend = lastCycleGpa - gpa
    val trendText = if (trend >= 0) "▲ ${"%.1f".format(lastCycleGpa)}"
    else "▼ ${"%.1f".format(lastCycleGpa)}"
    val trendColor = if (trend >= 0) Color(0xFF4ECFA3) else Color(0xFFE84040)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141E40)),
        border = BorderStroke(0.5.dp, Color(0xFF534AB7))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "PROMEDIO GPA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8899BB),
                    letterSpacing = 0.08.sp
                )
                Text(
                    text = "%.1f".format(gpa),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "/ 100 puntos",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8899BB)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .background(labelBg, RoundedCornerShape(99.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(text = gpaLabel, color = labelColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Último ciclo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8899BB))
                    Text(text = trendText, color = trendColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}