package com.moviles.unaplanner.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddActivityButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        // Usamos 14.dp para ser consistentes con tus ActivityCard
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            // MaterialTheme.colorScheme.secondary es CrimsonRed según tu Theme.kt
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(
            text = "+ Agregar actividad",
            // Usamos el estilo definido en tu Type.kt para botones
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}