package com.moviles.unaplanner.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.NavyBlue
import com.moviles.unaplanner.ui.theme.NavyBlueDark
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

    Box {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(NavyBlueDark, NavyBlue)
                    )
                )
        )

        NavigationBar(
            containerColor = Color.Transparent,
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
                            contentDescription = item.label,
                            tint = if (isSelected) CrimsonRed else Color.White.copy(alpha = 0.7f)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) CrimsonRed else Color.White.copy(alpha = 0.7f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = CrimsonRed,
                        selectedTextColor   = CrimsonRed,
                        indicatorColor      = CrimsonRed.copy(alpha = 0.15f),
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun AdminAppBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Box {
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

        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            adminNavItems.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(index) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color.White.copy(alpha = 0.2f),
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
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
    UNAPLANNERTheme(darkTheme = false) {
        var selected by remember { mutableIntStateOf(1) }
        AppBottomNavBar(
            selectedIndex = selected,
            onItemSelected = { selected = it }
        )
    }
}

@Preview(showBackground = true, name = "Admin - Modo Claro", backgroundColor = 0xFF020B63)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Admin - Modo Oscuro",
    backgroundColor = 0xFF020B63
)
@Composable
fun AdminAppBottomNavBarPreview() {
    UNAPLANNERTheme(darkTheme = false) {
        var selected by remember { mutableIntStateOf(0) }
        AdminAppBottomNavBar(
            selectedIndex = selected,
            onItemSelected = { selected = it }
        )
    }
}
