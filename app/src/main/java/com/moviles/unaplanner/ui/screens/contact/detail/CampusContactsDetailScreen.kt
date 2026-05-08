package com.moviles.unaplanner.ui.screens.contact.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.theme.*

@Composable
fun CampusContactsDetailScreen(
    contactId: Int,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSection: (Int) -> Unit, // Nuevo callback
    viewModel: CampusContactsDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(contactId) {
        viewModel.fetchContactDetail(contactId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Directorio",
                subtitle = "Detalle del contacto",
                onLogout = onLogout
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedIndex = 4,
                onItemSelected = { index ->
                    if (index == 4) {
                        onBack() // Si presiona Directorio estando en detalle, vuelve a la lista
                    } else {
                        onNavigateToSection(index)
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ContactDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = CrimsonRed
                    )
                }
                is ContactDetailUiState.Success -> {
                    ContactDetailContent(state.contact)
                }
                is ContactDetailUiState.Error -> {
                    ErrorMessageDetail(state.message) { viewModel.fetchContactDetail(contactId) }
                }
            }
        }
    }
}

@Composable
fun ContactDetailContent(contact: CampusContact) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 24.dp), // Aumentamos padding superior al quitar el botón
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono Principal
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F2F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = NavyBlue,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = contact.departmentName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = contact.description ?: "Información no disponible",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        InfoItem(
            icon = Icons.Default.Phone,
            label = "Teléfono",
            value = contact.phone ?: "No disponible",
            onClick = {
                contact.phone?.let { phone ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    context.startActivity(intent)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoItem(
            icon = Icons.Default.Email,
            label = "Correo Electrónico",
            value = contact.email ?: "No disponible",
            onClick = {
                contact.email?.let { email ->
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    context.startActivity(intent)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoItem(
            icon = Icons.Default.LocationOn,
            label = "Sede",
            value = "Campus Sarapiquí",
            onClick = null
        )
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White // Fix para que la Sede no se vea negra
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F2F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CrimsonRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyBlue
                )
            }
        }
    }
}

@Composable
fun ErrorMessageDetail(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = CrimsonRed, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)) {
            Text("Reintentar")
        }
    }
}
