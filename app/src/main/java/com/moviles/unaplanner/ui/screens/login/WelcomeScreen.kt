package com.moviles.unaplanner.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.components.AppButton
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.NavyBlue
import com.moviles.unaplanner.ui.theme.NavyBlueDark
import com.moviles.unaplanner.ui.theme.NavyBlueLight

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(NavyBlueDark, NavyBlue, NavyBlueLight)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // --- LOGO SUPERIOR ---
            Image(
                painter = painterResource(id = com.moviles.unaplanner.R.drawable.logo_rectangular),
                contentDescription = "UNAPLANNER Logo Principal",
                modifier = Modifier
                    .fillMaxWidth(1f)   // Ancho
                    .height(300.dp)     // Altura
                    .padding(0.dp),     // Sin padding vertical
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(0.dp))

            // --- BOTÓN INICIAR SESIÓN ---
            AppButton(
                text = "Iniciar Sesión",
                onClick = onNavigateToLogin,
                containerColor = CrimsonRed,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÓN CREAR CUENTA ---
            AppButton(
                text = "Crear Cuenta",
                onClick = onNavigateToRegister,
                containerColor = NavyBlueLight.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onNavigateToLogin = {},
        onNavigateToRegister = {}
    )
}

