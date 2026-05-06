package com.moviles.unaplanner.ui.components

import android.content.res.Configuration // Importación necesaria para la preview modo oscuro
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    NavItem("Inicio",     Icons.Filled.Home,          Icons.Outlined.Home),
    NavItem("Calendario", Icons.Filled.CalendarMonth,  Icons.Outlined.CalendarMonth),
    NavItem("Malla",      Icons.AutoMirrored.Filled.Notes, Icons.AutoMirrored.Outlined.Notes),
    NavItem("Notas",      Icons.Filled.Folder,         Icons.Outlined.Folder),
    NavItem("Contactos",  Icons.Filled.Contacts,       Icons.Outlined.Contacts)
)

@Composable
fun AppBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.secondary,
                    selectedTextColor   = MaterialTheme.colorScheme.secondary,
                    indicatorColor      = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),

                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


@Preview(showBackground = true, name = "Modo Claro")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Modo Oscuro"
)
@Composable
fun AppBottomNavBarPreview() {
    UNAPLANNERTheme {
        var selected by remember { mutableIntStateOf(1) }
        AppBottomNavBar(
            selectedIndex = selected,
            onItemSelected = { selected = it }
        )
    }
}