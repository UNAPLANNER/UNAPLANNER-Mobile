package com.moviles.unaplanner.ui.screens.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moviles.unaplanner.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun AcademicProgressScreen(
    viewModel: ProgressViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            val careerName = (uiState as? ProgressUiState.Success)?.data?.careerName ?: ""
            ProgressTopBar(onBack = onBack, careerName = careerName)
        },
        containerColor = BackgroundLight
    ) { padding ->
        when (val state = uiState) {
            is ProgressUiState.Idle, is ProgressUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CrimsonRed)
                }
            }

            is ProgressUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = viewModel::refresh,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }

            is ProgressUiState.Success -> {
                ProgressContent(
                    data = state.data,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ProgressTopBar(onBack: () -> Unit, careerName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(HeaderGradientStart, HeaderGradientEnd)))
            .statusBarsPadding()
            .padding(bottom = 20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Inicio",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Progreso Académico",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )
                if (careerName.isNotEmpty()) {
                    Text(
                        text = careerName,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressContent(data: ProgressData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Circular progress + stats card
        ProgressSummaryCard(data = data)

        // Area progress section
        Text(
            text = "PROGRESO POR ÁREA",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                data.areaProgress.forEach { area ->
                    AreaProgressRow(area = area)
                }
                if (data.areaProgress.isEmpty()) {
                    Text(
                        text = "No hay datos de áreas disponibles",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Graduation estimate card
        GraduationCard(estimate = data.graduationEstimate)

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ProgressSummaryCard(data: ProgressData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DonutChart(
                percent = data.percentCompleted,
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = data.approved.toString(),
                    label = "Aprobados",
                    color = EventGreen
                )
                StatDivider()
                StatItem(
                    value = data.inProgress.toString(),
                    label = "En curso",
                    color = CrimsonRed
                )
                StatDivider()
                StatItem(
                    value = data.pending.toString(),
                    label = "Pendientes",
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DonutChart(percent: Float, modifier: Modifier = Modifier) {
    val animatedPercent by animateFloatAsState(
        targetValue = percent,
        animationSpec = tween(durationMillis = 1000),
        label = "donut_anim"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 22.dp.toPx()
            val halfStroke = strokeWidth / 2f
            val arcTopLeft = Offset(halfStroke, halfStroke)
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val sweepAngle = 360f * animatedPercent

            // Background dashed arc (remaining)
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = -90f + sweepAngle,
                sweepAngle = 360f - sweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                )
            )

            // Progress arc (filled, red)
            if (sweepAngle > 0f) {
                drawArc(
                    color = Color(0xFFC0392B),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedPercent * 100).roundToInt()}%",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "completado",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Divider)
    )
}

@Composable
private fun AreaProgressRow(area: AreaProgress) {
    val animatedProgress by animateFloatAsState(
        targetValue = area.progress,
        animationSpec = tween(durationMillis = 800),
        label = "area_progress_${area.name}"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = area.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "${(area.progress * 100).roundToInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CrimsonRed
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Divider)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(CrimsonRed)
            )
        }
    }
}

@Composable
private fun GraduationCard(estimate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                text = "ESTIMADO DE GRADUACIÓN",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = estimate,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Basado en tu ritmo actual",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }
    }
}
