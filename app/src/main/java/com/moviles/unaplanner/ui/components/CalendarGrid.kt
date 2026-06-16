package com.moviles.unaplanner.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.ui.theme.*

data class DayHighlight(
    val day: Int,
    val color: Color
)

private val weekDayLabels = listOf("L", "M", "X", "J", "V", "S", "D")

@Composable
fun CalendarGrid(
    monthName: String,
    year: Int,
    startDayOfWeek: Int, // 0 for Monday, 1 for Tuesday, etc.
    totalDays: Int,
    selectedDay: Int?,
    today: Int? = null,
    highlights: List<DayHighlight> = emptyList(),
    onDaySelected: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: <  MARZO 2026  >
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "$monthName $year".uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = NavyBlue
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Weekday labels
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }

            // Days grid
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
                            val isToday = day == today

                            DayCell(
                                day = day,
                                isSelected = isSelected,
                                isToday = isToday,
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
    isToday: Boolean = false,
    highlightColor: Color?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val background = when {
        isSelected -> NavyBlue
        highlightColor != null -> highlightColor.copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        highlightColor != null -> highlightColor
        else -> Color.Black
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(2.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(1.5.dp, NavyBlue, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .background(background)
            .clickable { onClick() }
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
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
                monthName = "Marzo",
                year = 2026,
                startDayOfWeek = 6,
                totalDays = 31,
                selectedDay = selected,
                today = 10,
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
