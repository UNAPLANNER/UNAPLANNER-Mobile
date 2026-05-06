package com.moviles.unaplanner.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.ui.components.student.CustomDropdownField
import com.moviles.unaplanner.ui.components.student.CustomInputField
import com.moviles.unaplanner.ui.theme.NavyBlue
import com.moviles.unaplanner.ui.theme.NavyBlueDark
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.unaplanner.ui.components.AppButton
import com.moviles.unaplanner.ui.screens.login.LoginScreen
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme

@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    // Estados para los campos (Idealmente mover a un ViewModel más adelante)
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedCampus by remember { mutableStateOf("") }
    var selectedCarrera by remember { mutableStateOf("") }
    var selectedDobleCarrera by remember { mutableStateOf("") }
    var cicloActual by remember { mutableStateOf("") }

    val topGradient = Brush.verticalGradient(
        colors = listOf(NavyBlueDark, NavyBlue)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // --- CABECERA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(topGradient)
                .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "< Volver",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { onNavigateBack() }
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Crear cuenta",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Completa tus datos para registrarte",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                // Logo circular
                Image(
                    painter = painterResource(id = com.moviles.unaplanner.R.drawable.logo_circular),
                    contentDescription = "Logo UNAPLANNER",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- FORMULARIO ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Campos de Texto
            item {
                CustomInputField("NOMBRE COMPLETO", "Ej: María Juarez Pereira", value = name,
                    onValueChange = { name = it }
                )
            }
            item {
                CustomInputField("CORREO ELECTRÓNICO", "usuario@una.ac.cr", value = email,
                    onValueChange = { email = it }
                )
            }
            item {
                CustomInputField("CONTRASEÑA", "Mínimo 8 caracteres", password, { password = it }, isPassword = true)
            }
            item {
                CustomInputField("CONFIRMAR CONTRASEÑA", "Repite tu contraseña", confirmPassword, { confirmPassword = it }, isPassword = true)
            }

            // Selectores Dropdown
            item {
                CustomDropdownField(
                    label = "CAMPUS",
                    options = listOf("Campus Sarapiquí", "Campus Omar Dengo", "Campus Pérez Zeledón"),
                    selectedOption = selectedCampus,
                    onOptionSelected = { selectedCampus = it }
                )
            }
            item {
                CustomDropdownField(
                    label = "CARRERA PRINCIPAL",
                    options = listOf("Ingeniería en Sistemas de Información", "Administración de Oficinas", "Educación Rural"),
                    selectedOption = selectedCarrera,
                    onOptionSelected = { selectedCarrera = it }
                )
            }
            item {
                CustomDropdownField(
                    label = "¿LLEVAS DOBLE CARRERA? (OPCIONAL)",
                    options = listOf("No", "Ingeniería en Sistemas", "Administración"),
                    selectedOption = selectedDobleCarrera,
                    onOptionSelected = { selectedDobleCarrera = it }
                )
            }

            item {
                CustomInputField(
                    label = "CICLO ACTUAL",
                    placeholder = "I Ciclo 2026",
                    value = cicloActual,
                    onValueChange = { cicloActual = it }
                )
            }

            // Botón de Acción
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AppButton(
                    text = "Registrarme",
                    onClick = {
                        // Aquí llamarías a viewModel.register(name, email, password, ...)
                    },
                    containerColor = CrimsonRed,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onNavigateBack = {}
    )
}