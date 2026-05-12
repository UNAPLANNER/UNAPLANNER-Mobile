package com.moviles.unaplanner.ui.screens.admin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.data.remote.model.UserDto
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AppTextField
import com.moviles.unaplanner.ui.components.ProfileInfoCard
import com.moviles.unaplanner.ui.components.ProfileInfoRow
import com.moviles.unaplanner.ui.components.ProfileOptionItem

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
        Box(modifier = Modifier.fillMaxSize()) {
            AdminProfileContent(
                uiState = uiState,
                onBackClick = onBackClick,
                onLogoutClick = onLogoutClick,
                onEditClick = { viewModel.setShowEditModal(true) },
                padding = PaddingValues(0.dp)
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            )
        }
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                AdminAppBottomNavBar(selectedIndex = 3, onItemSelected = {})
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
            onSave = { name, phone, department -> viewModel.updateProfile(name, phone, department) },
            onChangePassword = { current, new -> viewModel.changePassword(current, new) },
            isSavingProfile = uiState.isSavingProfile,
            isChangingPassword = uiState.isChangingPassword,
            passwordChangeSuccessVersion = uiState.passwordChangeSuccessVersion
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
    val headerBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF020B63), Color(0xFF081A8C))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(headerBrush)
            .padding(bottom = padding.calculateBottomPadding())
    ) {
        AdminProfileHeader(
            uiState = uiState,
            onBackClick = onBackClick,
            onEditClick = onEditClick
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    ProfileInfoCard(title = "Informacion personal") {
                        ProfileInfoRow(
                            icon = Icons.Outlined.PersonOutline,
                            label = "Nombre",
                            value = uiState.user?.fullName ?: "-"
                        )
                        ProfileDivider()
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = uiState.user?.email ?: "-"
                        )
                        ProfileDivider()
                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Telefono",
                            value = uiState.user?.phone ?: "-"
                        )
                        ProfileDivider()
                        ProfileInfoRow(
                            icon = Icons.Default.Business,
                            label = "Departamento",
                            value = uiState.user?.department ?: "-"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileInfoCard(title = "Configuracion") {
                        ProfileOptionItem(
                            icon = Icons.Default.Edit,
                            label = "Editar informacion",
                            onClick = onEditClick,
                            iconContainerColor = Color(0xFFE3F2FD)
                        )
                        ProfileOptionItem(
                            icon = Icons.Default.Lock,
                            label = "Cambiar contrasena",
                            onClick = onEditClick,
                            iconContainerColor = Color(0xFFFFF8E1)
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFF795548)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesion",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }

                if (uiState.isInitialLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProfileHeader(
    uiState: AdminProfileUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = Color.White)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = uiState.user?.fullName ?: "Administrador",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = uiState.user?.department ?: "Departamento no asignado",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.76f))
        )
        Text(
            text = uiState.user?.role ?: "Admin",
            style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.9f))
        )
    }
}

@Composable
private fun ProfileDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileModal(
    user: UserDto?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    onChangePassword: (String, String) -> Unit,
    isSavingProfile: Boolean,
    isChangingPassword: Boolean,
    passwordChangeSuccessVersion: Int
) {
    var fullName by remember(user?.id) { mutableStateOf(user?.fullName.orEmpty()) }
    var phone by remember(user?.id) { mutableStateOf(user?.phone.orEmpty()) }
    var department by remember(user?.id) { mutableStateOf(user?.department.orEmpty()) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordSection by remember { mutableStateOf(false) }
    var profileError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val isBusy = isSavingProfile || isChangingPassword

    LaunchedEffect(passwordChangeSuccessVersion) {
        if (passwordChangeSuccessVersion > 0) {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
            passwordError = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Editar perfil",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "El email y el rol son de solo lectura.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppTextField(
                value = fullName,
                label = "Nombre completo",
                placeholder = "Nombre",
                onValueChange = {
                    fullName = it
                    profileError = null
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
            AppTextField(
                value = user?.email.orEmpty(),
                label = "Email",
                placeholder = "correo@una.ac.cr",
                onValueChange = {},
                readOnly = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            AppTextField(
                value = user?.role ?: "Admin",
                label = "Rol",
                placeholder = "Admin",
                onValueChange = {},
                readOnly = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            AppTextField(
                value = phone,
                label = "Telefono",
                placeholder = "2277-0000",
                onValueChange = {
                    phone = it
                    profileError = null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(14.dp))
            AppTextField(
                value = department,
                label = "Departamento",
                placeholder = "Departamento",
                onValueChange = {
                    department = it
                    profileError = null
                }
            )

            profileError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(22.dp))

            OutlinedButton(
                onClick = { showPasswordSection = !showPasswordSection },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBusy,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (showPasswordSection) "Ocultar cambio de contrasena" else "Cambiar contrasena")
            }

            if (showPasswordSection) {
                PasswordChangeSection(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    passwordError = passwordError,
                    isBusy = isBusy,
                    onCurrentPasswordChange = {
                        currentPassword = it
                        passwordError = null
                    },
                    onNewPasswordChange = {
                        newPassword = it
                        passwordError = null
                    },
                    onConfirmPasswordChange = {
                        confirmPassword = it
                        passwordError = null
                    },
                    onSubmit = {
                        when {
                            currentPassword.isBlank() -> passwordError = "Ingresa la contrasena actual"
                            newPassword.length < 8 -> passwordError = "La nueva contrasena debe tener al menos 8 caracteres"
                            newPassword != confirmPassword -> passwordError = "La nueva contrasena no coincide"
                            else -> onChangePassword(currentPassword, newPassword)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isBusy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isBusy
                ) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        when {
                            fullName.isBlank() -> profileError = "Ingresa el nombre completo"
                            department.isBlank() -> profileError = "Ingresa el departamento"
                            phone.isBlank() -> profileError = "Ingresa el telefono institucional"
                            phone.any { it.isLetter() } -> profileError = "El telefono no debe contener letras"
                            !Regex("^2277-\\d{4}$").matches(phone.trim()) -> {
                                profileError = "El telefono debe usar el formato 2277-XXXX"
                            }
                            else -> onSave(fullName, phone, department)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = !isBusy
                ) {
                    Text("Guardar cambios")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PasswordChangeSection(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    passwordError: String?,
    isBusy: Boolean,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    AppTextField(
        value = currentPassword,
        label = "Contrasena actual",
        placeholder = "********",
        onValueChange = onCurrentPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Spacer(modifier = Modifier.height(12.dp))
    AppTextField(
        value = newPassword,
        label = "Nueva contrasena",
        placeholder = "********",
        onValueChange = onNewPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Spacer(modifier = Modifier.height(12.dp))
    AppTextField(
        value = confirmPassword,
        label = "Confirmar contrasena",
        placeholder = "********",
        onValueChange = onConfirmPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )

    passwordError?.let {
        Spacer(modifier = Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }

    Spacer(modifier = Modifier.height(12.dp))
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = !isBusy
    ) {
        Text("Guardar contrasena")
    }
}
