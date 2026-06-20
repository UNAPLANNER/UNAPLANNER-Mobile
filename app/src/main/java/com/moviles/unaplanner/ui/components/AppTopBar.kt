package com.moviles.unaplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.ui.theme.*

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    onLogout: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    hasUnreadNotifications: Boolean = false, // Nuevo parámetro
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(HeaderGradientStart, HeaderGradientEnd)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(gradientBrush)
            .statusBarsPadding() // Respeta la barra de estado
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (action != null) {
                    action()
                    Spacer(modifier = Modifier.width(12.dp))
                }

                if (onNotificationsClick != null) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (hasUnreadNotifications) {
                                    Badge(
                                        containerColor = CrimsonRed,
                                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                if (onLogout != null) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Usuario",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {

                            if (onNavigateToEditProfile != null) {
                                DropdownMenuItem(
                                    text = { Text("Editar Perfil") },
                                    onClick = {
                                        showMenu = false
                                        onNavigateToEditProfile()
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión") },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    UNAPLANNERTheme {
        AppTopBar(
            title = "Calendario",
            subtitle = "Marzo 2026",
            onLogout = {}
        )
    }
}
