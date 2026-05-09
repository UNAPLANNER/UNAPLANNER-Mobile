package com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.NavyBlue
import kotlinx.coroutines.delay

@Composable
fun ContactAdminScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onContactClick: (CampusContact) -> Unit = {},
    isInsideTab: Boolean = false,
    navController: NavController? = null,
    viewModel: ContactAdminViewModel = viewModel(factory = ContactAdminViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    var showSuccessToast by remember { mutableStateOf(false) }

    // Observar resultado de creación de contacto
    val contactCreated by navController?.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("contact_created", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(contactCreated) {
        if (contactCreated) {
            showSuccessToast = true
            viewModel.loadContacts() // Forzar recarga inmediata de la lista
            // Limpiamos el estado para que no se repita
            navController?.currentBackStackEntry?.savedStateHandle?.set("contact_created", false)
        }
    }

    // Forzar recarga cada vez que la pantalla vuelve a estar activa
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadContacts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Lógica para el Toast de 7 segundos
    LaunchedEffect(showSuccessToast) {
        if (showSuccessToast) {
            delay(7000)
            showSuccessToast = false
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isInsideTab) {
            ContactAdminContent(
                uiState = uiState,
                padding = PaddingValues(0.dp),
                onRefresh = { viewModel.loadContacts() },
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onContactClick = onContactClick
            )
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    AdminTopBar(
                        title = "Contactos",
                        subtitle = "Directorio de la Sede",
                        showBackButton = true,
                        onBackClick = onBackClick,
                        showAddButton = true,
                        onAddClick = onAddClick
                    )
                },
                bottomBar = {
                    AppBottomNavBar(
                        selectedIndex = 2,
                        onItemSelected = {},
                        isAdmin = true
                    )
                },
                containerColor = BackgroundLight
            ) { innerPadding ->
                ContactAdminContent(
                    uiState = uiState,
                    padding = innerPadding,
                    onRefresh = { viewModel.loadContacts() },
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onContactClick = onContactClick
                )
            }
        }

        // Toast en la parte superior derecha (Cerca del buscador)
        AnimatedVisibility(
            visible = showSuccessToast,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = if (isInsideTab) 12.dp else 90.dp, end = 16.dp)
                .zIndex(100f)
        ) {
            SuccessToast(
                message = UserMessages.CampusContacts.CREATE_SUCCESS,
                onDismiss = { showSuccessToast = false }
            )
        }
    }
}

@Composable
fun SuccessToast(message: String, onDismiss: () -> Unit) {
    Surface(
        color = NavyBlue,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 12.dp,
        modifier = Modifier.widthIn(max = 280.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ContactAdminContent(
    uiState: ContactAdminUiState,
    padding: PaddingValues,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onContactClick: (CampusContact) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(BackgroundLight)
    ) {
        // Buscador
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar departamento o contacto...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        if (uiState.isLoading && uiState.contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.contacts.isEmpty()) {
            EmptyContactsState(onRefresh)
        } else if (uiState.filteredContacts.isEmpty()) {
            NoResultsState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredContacts) { contact ->
                    ContactItemCard(
                        contact = contact,
                        onViewClick = { onContactClick(contact) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoResultsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🔍", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Prueba con otros términos de búsqueda.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ContactItemCard(
    contact: CampusContact,
    onViewClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onViewClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.departmentName?.take(1)?.uppercase() ?: "?",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.departmentName ?: "Sin nombre",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    contact.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = contact.phone ?: "No disponible",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = contact.email ?: "No disponible",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun EmptyContactsState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "📇", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay contactos registrados",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Los contactos de tu sede aparecerán aquí.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}
