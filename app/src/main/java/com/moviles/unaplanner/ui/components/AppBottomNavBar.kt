package com.moviles.unaplanner.ui.components

import android.content.res.Configuration
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val studentNavItems = listOf(
    NavItem("Inicio",     Icons.Filled.Home,          Icons.Outlined.Home),
    NavItem("Calendario", Icons.Filled.CalendarMonth,  Icons.Outlined.CalendarMonth),
    NavItem("Malla",      Icons.AutoMirrored.Filled.Notes, Icons.AutoMirrored.Outlined.Notes),
    NavItem("Notas",      Icons.Filled.Folder,         Icons.Outlined.Folder),
    NavItem("Contactos",  Icons.Filled.Contacts,       Icons.Outlined.Contacts)
)

private val adminNavItems = listOf(
    NavItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem("Carrera", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    NavItem("Contactos", Icons.Filled.Contacts, Icons.Outlined.Contacts),
    NavItem("Perfil", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun AppBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    isAdmin: Boolean = false
) {
    val items = if (isAdmin) adminNavItems else studentNavItems
    
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
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
