package com.moviles.unaplanner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.R
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme

@Composable
fun AdminTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showAddButton: Boolean = false,
    onAddClick: () -> Unit = {},
    isHome: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Fondo azul
        Box(
            Modifier
                .fillMaxWidth()
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF020B63),
                            Color(0xFF081A8C)
                        )
                    )
                )
        )

        // Glow rojo
        Box(
            Modifier
                .fillMaxWidth()
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x33FF005A),
                            Color.Transparent
                        ),
                        radius = 900f,
                        center = Offset(900f, 150f)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isHome) {
                    // Logo on the left for home
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_circular),
                            contentDescription = "Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isHome) {
                        Text(
                            text = "PANEL DE CONTROL",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (showAddButton) {
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White
                        )
                    }
                } else if (isHome) {
                    // Spacer for symmetry if home
                    Spacer(modifier = Modifier.size(40.dp))
                } else if (!showBackButton) {
                     // Empty space to balance
                     Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF020B63)
@Composable
fun AdminTopBarHomePreview() {
    UNAPLANNERTheme {
        AdminTopBar(
            title = "Juan Perez",
            isHome = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF020B63)
@Composable
fun AdminTopBarBackPreview() {
    UNAPLANNERTheme {
        AdminTopBar(
            title = "Configuración",
            subtitle = "Ajustes de la cuenta",
            showBackButton = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF020B63)
@Composable
fun AdminTopBarAddPreview() {
    UNAPLANNERTheme {
        AdminTopBar(
            title = "Usuarios",
            showAddButton = true
        )
    }
}
