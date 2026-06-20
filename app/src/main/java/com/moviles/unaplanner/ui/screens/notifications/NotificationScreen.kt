package com.moviles.unaplanner.ui.screens.notifications

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.data.remote.model.NotificationDto
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val dragOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.60f)
            .graphicsLayer { translationY = dragOffset.value }
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E4A8A), Color(0xFF1A2D5A))
                )
            )
            .statusBarsPadding()
            .clickable(enabled = false) { }
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Notificaciones",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color.Red,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                unreadCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Botón para eliminar todas
            IconButton(
                onClick = { viewModel.deleteAllNotifications() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Eliminar todas",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Filters
        val filtersList = listOf("Todas", "Exámenes", "Tareas", "Proyectos", "Eventos")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtersList) { filter ->
                FilterChipItem(
                    text = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { viewModel.setFilter(filter) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Notifications List
        Box(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
            if (isLoading && notifications.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationItemCard(
                            notification = notification,
                            onClick = { viewModel.markAsRead(notification.id) },
                            onDelete = { viewModel.deleteNotification(notification.id) }
                        )
                    }
                }
            }
        }

        // Mark all as read button
        TextButton(
            onClick = { viewModel.markAllAsRead() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            Text(
                "Marcar todas como leídas",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Drag handle — swipe up to dismiss
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragOffset.value < -80f) {
                                coroutineScope.launch {
                                    dragOffset.animateTo(
                                        targetValue = -3000f,
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                    onBack()
                                }
                            } else {
                                coroutineScope.launch {
                                    dragOffset.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring()
                                    )
                                }
                            }
                        },
                        onVerticalDrag = { _, dragAmount ->
                            if (dragAmount < 0) {
                                coroutineScope.launch {
                                    dragOffset.snapTo(dragOffset.value + dragAmount)
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.35f))
            )
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Red else Color.White.copy(alpha = 0.1f)
    val contentColor = Color.White

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (text) {
                "Exámenes" -> Text(" ", fontSize = 14.sp)
                "Tareas" -> Text(" ", fontSize = 14.sp)
                "Proyectos" -> Text(" ", fontSize = 14.sp)
            }
            Text(text, color = contentColor, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationDto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val type = notification.type.lowercase()
    val borderColor = when {
        type.contains("exam") || type.contains("examen") -> Color.Red
        type.contains("assignment") || type.contains("tarea") -> Color(0xFFFFB300)
        type.contains("project") || type.contains("proyecto") -> Color(0xFF4CAF50)
        type.contains("event") || type.contains("evento") -> Color(0xFF2196F3)
        type.contains("nota") || type.contains("grade") -> Color.Green
        else -> Color.Gray
    }

    val icon = when {
        type.contains("exam") || type.contains("examen") -> Icons.Default.Warning
        type.contains("assignment") || type.contains("tarea") -> Icons.Default.Edit
        type.contains("project") || type.contains("proyecto") -> Icons.AutoMirrored.Filled.Assignment
        type.contains("event") || type.contains("evento") -> Icons.Default.DateRange
        type.contains("nota") || type.contains("grade") -> Icons.Default.CheckCircle
        else -> Icons.Default.School
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(borderColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = notification.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!notification.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Gray.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "Hace ${formatTimeAgo(notification.createdDate)}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

fun formatTimeAgo(dateStr: String): String {
    return "5 min"
}
