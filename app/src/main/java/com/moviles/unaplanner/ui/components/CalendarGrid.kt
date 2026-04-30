package com.moviles.unaplanner.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.theme.*

data class DayHighlight(
    val day: Int,
    val color: Color
)

private val weekDayLabels = listOf("L", "M", "X", "J", "V", "S", "D")

@Composable
fun CalendarGrid(
    month: String,
    year: Int,
    startDayOfWeek: Int,
    totalDays: Int,
    selectedDay: Int?,
    highlights: List<DayHighlight> = emptyList(),
    onDaySelected: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$month $year".uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            Row(modifier = Modifier.fillMaxWidth()) {
                weekDayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        // Usa bodySmall de Type.kt (11sp)
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            val totalCells = startDayOfWeek + totalDays
            val rows = (totalCells + 6) / 7

            repeat(rows) { rowIndex ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { colIndex ->
                        val cellIndex = rowIndex * 7 + colIndex
                        val day = cellIndex - startDayOfWeek + 1

                        if (day in 1..totalDays) {
                            val highlight = highlights.firstOrNull { it.day == day }
                            val isSelected = day == selectedDay

                            DayCell(
                                day = day,
                                isSelected = isSelected,
                                highlightColor = highlight?.color,
                                modifier = Modifier.weight(1f),
                                onClick = { onDaySelected(day) }
                            )
                        } else {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    highlightColor: Color?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val background = when {
        isSelected -> MaterialTheme.colorScheme.primary
        highlightColor != null -> highlightColor
        else -> Color.Transparent
    }


    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        highlightColor != null -> TextOnDark
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(CircleShape)
            .background(background)
            .clickable { onClick() }
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, name = "Modo Claro")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Modo Oscuro")
@Composable
fun CalendarGridPreview() {
    UNAPLANNERTheme {
        var selected by remember { mutableIntStateOf(15) }
        Surface(color = MaterialTheme.colorScheme.background) {
            CalendarGrid(
                month = "Marzo",
                year = 2026,
                startDayOfWeek = 6,
                totalDays = 31,
                selectedDay = selected,
                highlights = listOf(
                    DayHighlight(3,  EventBlue),
                    DayHighlight(5,  EventOrange),
                    DayHighlight(7,  EventBlue),
                    DayHighlight(13, EventGreen)
                ),
                onDaySelected = { selected = it },
                onPreviousMonth = {},
                onNextMonth = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}