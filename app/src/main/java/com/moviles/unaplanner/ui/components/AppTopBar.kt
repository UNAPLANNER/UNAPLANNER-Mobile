package com.moviles.unaplanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            // Usa el color primario definido en Theme.kt (NavyBlue)
            containerColor = MaterialTheme.colorScheme.primary,
            // Asegura que los títulos usen el color de contraste (TextOnDark)
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    // Usa correctamente displayLarge de Type.kt
                    style = MaterialTheme.typography.displayLarge,
                    // Usamos onPrimary en lugar de importar el color fijo
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        // Usa correctamente titleMedium de Type.kt
                        style = MaterialTheme.typography.titleMedium,
                        // Aplicamos transparencia al color onPrimary para el subtítulo
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    // Es importante envolver la Preview en tu tema para ver los colores reales
    UNAPLANNERTheme {
        AppTopBar(
            title = "Calendario",
            subtitle = "Marzo 2026"
        )
    }
}