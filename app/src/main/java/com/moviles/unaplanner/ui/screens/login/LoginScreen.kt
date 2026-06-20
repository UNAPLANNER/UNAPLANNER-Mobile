package com.moviles.unaplanner.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.R
import com.moviles.unaplanner.data.AuthSession
import com.moviles.unaplanner.ui.components.AppButton
import com.moviles.unaplanner.ui.components.AppTextField
import com.moviles.unaplanner.ui.components.CustomInputField
import com.moviles.unaplanner.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAdminHome: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            application = LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val savedEmail by viewModel.savedEmail.collectAsState()
    val savedPassword by viewModel.savedPassword.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()

    var email by remember(savedEmail) { mutableStateOf(savedEmail) }
    var password by remember(savedPassword) { mutableStateOf(savedPassword) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                val user = (uiState as LoginUiState.Success).user
                if (user.role?.lowercase() == "admin") {
                    onNavigateToAdminHome()
                } else {
                    onNavigateToHome()
                }
            }
            is LoginUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as LoginUiState.Error).message)
                viewModel.resetError()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color(0xFFC62828),
                    contentColor = Color.White,
                    snackbarData = data,
                    shape = RoundedCornerShape(18.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            LoginHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                EmailTextField(value = email, onValueChange = { email = it })

                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextField(value = password, onValueChange = { password = it })

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.setRememberMe(!rememberMe) }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { viewModel.setRememberMe(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFC62828),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Recordar mis datos",
                            color = Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color(0xFFC62828),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LoginButton(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Por favor completa los campos")
                            }
                        } else {
                            viewModel.login(email, password, rememberMe)
                        }
                    },
                    enabled = uiState !is LoginUiState.Loading
                )
                
                if (uiState is LoginUiState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFC62828))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LoginFooter(onNavigateToRegister = onNavigateToRegister)
            }
        }
    }
}

@Composable
private fun LoginHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(NavyBlue)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Volver",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bienvenido de nuevo",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingresa a tu cuenta para continuar",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.logo_circular),
                    contentDescription = "Logo UNAPLANNER",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    AppButton(
        text = "Iniciar Sesión",
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        containerColor = Color(0xFFC62828)
    )
}

@Composable
fun LoginFooter(onNavigateToRegister: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text("¿No tienes cuenta? ", color = TextSecondary)
        Text(
            text = "Regístrate",
            color = CrimsonRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )
    }
}

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppTextField(
        value = value,
        label = "CORREO ELECTRÓNICO",
        placeholder = "usuario@una.ac.cr",
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    CustomInputField(
        label = "CONTRASEÑA",
        value = value,
        onValueChange = onValueChange,
        placeholder = "********",
        isPassword = true
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToRegister = {},
        onBack = {},
        onNavigateToHome = {},
        onNavigateToAdminHome = {}
    )
}
