package com.moviles.unaplanner.ui.screens.inicio

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.R
import com.moviles.unaplanner.ui.theme.*

@Composable
fun InicioPlaceholderScreen(
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val quickAccessItems = listOf(
        QuickAccessData("Malla", "Cursos y requisitos", Icons.Default.FolderOpen, Color(0xFFF3E5F5), Color(0xFF7B1FA2), 2),
        QuickAccessData("Proyección", "Próximo ciclo", Icons.Default.Timeline, Color(0xFFE8F5E9), Color(0xFF2E7D32), 1),
        QuickAccessData("Notas", "Apuntes de cursos", Icons.Default.Description, Color(0xFFFFF3E0), Color(0xFFF57C00), 3),
        QuickAccessData("Contactos", "Directorio", Icons.Default.ListAlt, Color(0xFFE3F2FD), Color(0xFF1976D2), 4)
    )

    val portalItems = listOf(
        PortalData("Aula Virtual", "https://aulavirtual.una.ac.cr/login/index.php", R.drawable.logo_aula_virtual),
        PortalData("BANNER", "https://studentssb.una.ac.cr/StudentRegistrationSsb/ssb/registration", R.drawable.logo_banner),
        PortalData("SIBEUNA", "https://sibeuna.una.ac.cr/sibeuna-web/becas.jsf", R.drawable.logo_sibeuna),
        PortalData("Ventanilla Digital", "https://servicios.una.ac.cr/ASMSCustomer/index.html#/", R.drawable.logo_ventanilla),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- ACCESO RÁPIDO ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = EventOrange,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ACCESO RÁPIDO",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
        }

        // Usamos Column + Rows en lugar de LazyGrid dentro de ScrollView
        quickAccessItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    QuickAccessCard(
                        data = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(item.tabIndex) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- PORTALES UNA ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = NavyBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PORTALES UNA",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = NavyBlue,
                letterSpacing = 0.5.sp
            )
        }

        portalItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    PortalCard(
                        data = item,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                            context.startActivity(intent)
                        }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun QuickAccessCard(data: QuickAccessData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(data.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = data.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 15.sp
            )
            Text(
                text = data.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun PortalCard(data: PortalData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = data.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

data class QuickAccessData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color,
    val tabIndex: Int
)

data class PortalData(
    val name: String,
    val url: String,
    val imageRes: Int
)
