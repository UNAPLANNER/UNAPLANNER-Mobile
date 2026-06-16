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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.components.SuccessToast
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.NavyBlue
import kotlinx.coroutines.delay

@Composable
fun ContactAdminScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onEditClick: (CampusContact) -> Unit = {},
    onContactClick: (CampusContact) -> Unit = {},
    isInsideTab: Boolean = false,
    navController: NavController? = null,
    viewModel: ContactAdminViewModel = viewModel(factory = ContactAdminViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    var showSuccessToast by rememberSaveable { mutableStateOf(false) }
    var toastMessage by rememberSaveable { mutableStateOf("") }

    // Observar resultado de creación de contacto
    val contactCreated by navController?.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("contact_created", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(contactCreated) {
        if (contactCreated) {
            toastMessage = UserMessages.CampusContacts.CREATE_SUCCESS
            showSuccessToast = true
            viewModel.loadContacts() // Force immediate refresh
            navController?.currentBackStackEntry?.savedStateHandle?.set("contact_created", false)
        }
    }

    // Observe contact update result
    val contactUpdated by navController?.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("contact_updated", false)
        ?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    LaunchedEffect(contactUpdated) {
        if (contactUpdated) {
            toastMessage = UserMessages.CampusContacts.UPDATE_SUCCESS
            showSuccessToast = true
            viewModel.loadContacts() // Force immediate refresh
            navController?.currentBackStackEntry?.savedStateHandle?.set("contact_updated", false)
        }
    }

    // Force reload every time the screen becomes active again
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

    // Logic for the 7-second Toast
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

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            toastMessage = it
            showSuccessToast = true
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
                onContactClick = onContactClick,
                onEditClick = onEditClick,
                onDeleteClick = { viewModel.setContactToDelete(it) }
            )
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    AdminTopBar(
                        title = UserMessages.CampusContacts.TITLE,
                        subtitle = UserMessages.CampusContacts.SUBTITLE,
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
                    onContactClick = onContactClick,
                    onEditClick = onEditClick,
                    onDeleteClick = { viewModel.setContactToDelete(it) }
                )
            }
        }

        // Confirmation dialog to delete
        uiState.contactToDelete?.let { contact ->
            AlertDialog(
                onDismissRequest = { viewModel.setContactToDelete(null) },
                containerColor = Color.White,
                title = { 
                    Text(
                        textAlign = TextAlign.Center,
                        text = UserMessages.CampusContacts.DeleteDialog.TITLE,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyBlue,
                        fontSize = 20.sp
                    ) 
                },
                text = { 
                    Text(
                        text = UserMessages.CampusContacts.DeleteDialog.MESSAGE,
                        color = Color.Black.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) 
                },
                confirmButton = {
                    Button(
                        onClick = {
                            contact.id.let { viewModel.deleteContact(it) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(UserMessages.CampusContacts.DeleteDialog.CONFIRM, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.setContactToDelete(null) }
                    ) {
                        Text(
                            UserMessages.CampusContacts.DeleteDialog.CANCEL, 
                            color = NavyBlue, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        // Toast in the top right corner (Near the search bar)
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
                message = toastMessage,
                onDismiss = { showSuccessToast = false }
            )
        }
    }
}

@Composable
fun ContactAdminContent(
    uiState: ContactAdminUiState,
    padding: PaddingValues,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onContactClick: (CampusContact) -> Unit,
    onEditClick: (CampusContact) -> Unit = {},
    onDeleteClick: (CampusContact) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(BackgroundLight)
    ) {
        // search engine
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { 
                Text(
                    text = UserMessages.CampusContacts.SEARCH_PLACEHOLDER,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                ) 
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.Gray)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary
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
                        onViewClick = { onContactClick(contact) },
                        onEditClick = { onEditClick(contact) },
                        onDeleteClick = { onDeleteClick(contact) }
                    )
                }
            }
        }
        
        if (uiState.isLoading && uiState.contacts.isNotEmpty()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
            text = UserMessages.CampusContacts.States.NO_RESULTS_TITLE,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = UserMessages.CampusContacts.States.NO_RESULTS_SUBTITLE,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ContactItemCard(
    contact: CampusContact,
    onViewClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
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
                        text = contact.departmentName ?: UserMessages.CampusContacts.States.NO_NAME,
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

                // Edit Button
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar contacto",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }

                // Delete Button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar contacto",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
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
                    text = contact.phone ?: UserMessages.CampusContacts.States.NO_INFO,
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
                    text = contact.email ?: UserMessages.CampusContacts.States.NO_INFO,
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
            text = UserMessages.CampusContacts.States.EMPTY_TITLE,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = UserMessages.CampusContacts.States.EMPTY_SUBTITLE,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(UserMessages.CampusContacts.States.RETRY)
        }
    }
}
