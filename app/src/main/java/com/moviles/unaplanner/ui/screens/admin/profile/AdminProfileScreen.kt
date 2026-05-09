package com.moviles.unaplanner.ui.screens.admin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.UserDto
import com.moviles.unaplanner.ui.components.*

@Composable
fun AdminProfileScreen(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    isInsideTab: Boolean = false,
    viewModel: AdminProfileViewModel = viewModel(factory = AdminProfileViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    if (isInsideTab) {
        AdminProfileContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onLogoutClick = onLogoutClick,
            onEditClick = { viewModel.setShowEditModal(true) },
            padding = PaddingValues(0.dp)
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                AdminAppBottomNavBar(
                    selectedIndex = 3,
                    onItemSelected = {}
                )
            }
        ) { padding ->
            AdminProfileContent(
                uiState = uiState,
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick,
                onEditClick = { viewModel.setShowEditModal(true) },
                padding = padding
            )
        }
    }

    if (uiState.showEditModal) {
        EditProfileModal(
            user = uiState.user,
            onDismiss = { viewModel.setShowEditModal(false) },
            onSave = { name, phone, dept -> viewModel.updateProfile(name, phone, dept) },
            onChangePassword = { current, new -> viewModel.changePassword(current, new) },
            isUpdating = uiState.isUpdating
        )
    }
}

@Composable
private fun AdminProfileContent(
    uiState: AdminProfileUiState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditClick: () -> Unit,
    padding: PaddingValues
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF020B63), Color(0xFF081A8C))
    )

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(bottom = padding.calculateBottomPadding())
    ) {
        // Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White
                )
            }
        }

        // Profile info top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = uiState.user?.fullName ?: "Administrador",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = uiState.user?.department ?: "Departamento No Asignado",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFF2E7D32).copy(alpha = 0.9f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.White, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.user?.role ?: "Administrador del Sistema",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content area with gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Fondo azul
            Box(
                Modifier
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

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    ProfileInfoCard(title = "Información personal") {
                        ProfileInfoRow(
                            icon = Icons.Default.PersonOutline,
                            label = "Nombre",
                            value = uiState.user?.fullName ?: "-"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                        value = uiState.user?.email ?: "-"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = uiState.user?.phone ?: "-"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileInfoRow(
                        icon = Icons.Default.Storefront,
                        label = "Departamento",
                        value = uiState.user?.department ?: "-"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                ProfileInfoCard(title = "Estadísticas de gestión") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "próximamente visualización",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                ProfileInfoCard(title = "Configuración") {
                    ProfileOptionItem(
                        icon = Icons.Default.Lock,
                        label = "Cambiar contraseña",
                        onClick = onEditClick,
                        iconContainerColor = Color(0xFFE3F2FD)
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Notifications,
                        label = "Notificaciones",
                        onClick = { /* TODO */ },
                        iconContainerColor = Color(0xFFFFF8E1)
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Info,
                        label = "Acerca de UNAPlanner",
                        subtitle = "Versión 1.0 · I Ciclo 2026",
                        onClick = { /* TODO */ },
                        iconContainerColor = Color(0xFFF1F8E9)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFF795548)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesión",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileModal(
    user: UserDto?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    onChangePassword: (String, String) -> Unit,
    isUpdating: Boolean
) {
    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var department by remember { mutableStateOf(user?.department ?: "") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✏️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Actualiza tu información personal",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = fullName,
                label = "Nombre completo",
                placeholder = "Nombre",
                onValueChange = { fullName = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = user?.email ?: "",
                label = "Email (Solo lectura)",
                placeholder = "Email",
                onValueChange = {},
                readOnly = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = phone,
                label = "Teléfono",
                placeholder = "Teléfono",
                onValueChange = { phone = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = department,
                label = "Departamento",
                placeholder = "Departamento",
                onValueChange = { department = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Cambiar contraseña",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            AppTextField(
                value = currentPassword,
                label = "Contraseña actual",
                placeholder = "********",
                onValueChange = {
                    currentPassword = it
                    passwordError = null
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                AppTextField(
                    value = newPassword,
                    label = "Nueva contraseña",
                    placeholder = "********",
                    onValueChange = {
                        newPassword = it
                        passwordError = null
                    },
                    modifier = Modifier.weight(1f),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AppTextField(
                    value = confirmPassword,
                    label = "Confirmar",
                    placeholder = "********",
                    onValueChange = {
                        confirmPassword = it
                        passwordError = null
                    },
                    modifier = Modifier.weight(1f),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            passwordError?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isUpdating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isUpdating
                ) {
                    Text(text = "Cancelar")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        if (newPassword.isNotEmpty()) {
                            when {
                                currentPassword.isBlank() -> {
                                    passwordError = "Ingresa la contrasena actual"
                                    return@Button
                                }
                                newPassword != confirmPassword -> {
                                    passwordError = "La nueva contrasena no coincide"
                                    return@Button
                                }
                                else -> onChangePassword(currentPassword, newPassword)
                            }
                        }
                        onSave(fullName, phone, department)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = !isUpdating
                ) {
                    Text(text = "Guardar cambios")
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
