package com.moviles.unaplanner.ui.screens.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.ui.theme.*

@Composable
fun CampusContactsListScreen(
    modifier: Modifier = Modifier,
    viewModel: CampusContactsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf(
        "Todos", "Dirección", "Administrativo", "Estudiantes", 
        "Biblioteca", "Orientación", "Psicología", "Recepción", 
        "Redes", "Registro", "Seguridad", "Bienestar", 
        "Promoción", "Ejecutiva"
    )
    var selectedCategory by remember { mutableStateOf("Todos") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // --- Barra de Búsqueda ---
        SearchBar(
            query = searchQuery,
            onQueryChange = { 
                searchQuery = it
                viewModel.updateSearchQuery(it)
            }
        )

        // --- Filtros (Chips) ---
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    label = category,
                    isSelected = selectedCategory == category,
                    onClick = { 
                        selectedCategory = category
                        viewModel.updateCategory(category)
                    }
                )
            }
        }

        // --- Contenido Principal ---
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is ContactsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = CrimsonRed
                    )
                }
                is ContactsUiState.Success -> {
                    ContactsList(contacts = state.contacts)
                }
                is ContactsUiState.Error -> {
                    ErrorMessage(message = state.message, onRetry = { viewModel.fetchContacts() })
                }
                is ContactsUiState.Empty -> {
                    EmptyState()
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5E9F2)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar oficina o nombre...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) NavyBlue else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Divider)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else TextPrimary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun ContactsList(contacts: List<CampusContact>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // En la imagen se ve una sección con título, aquí podrías agrupar por categoría si el API lo permitiera
        item {
            Text(
                text = "DIRECTORIO",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(contacts) { contact ->
            ContactCard(contact)
        }
    }
}

@Composable
fun ContactCard(contact: CampusContact) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Izquierda
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F2F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.departmentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = contact.description ?: "Información no disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Botones de Acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Botón de Llamada
                ActionButton(
                    icon = Icons.Default.Phone,
                    color = Color(0xFFE8F5E9)
                ) {
                    contact.phone?.let { phone ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        context.startActivity(intent)
                    }
                }

                // Botón de Correo
                ActionButton(
                    icon = Icons.Default.Email,
                    color = Color(0xFFF3F4F6)
                ) {
                    contact.email?.let { email ->
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, color: Color, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Disabled)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No se encontraron contactos", color = TextSecondary)
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = CrimsonRed, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)) {
            Text("Reintentar")
        }
    }
}
