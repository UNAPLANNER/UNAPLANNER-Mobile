package com.moviles.unaplanner.ui.screens.admin.profile.homeAdmin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.theme.BackgroundLight

@Composable
fun HomeAdminScreen(
    onNavigateToSection: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            AdminTopBar(
                title = "UNAPlanner Admin",
                isHome = true
            )
        },
        bottomBar = {
            AdminAppBottomNavBar(
                selectedIndex = 0,
                onItemSelected = onNavigateToSection
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeAdminContent()
        }
    }
}

@Composable
fun HomeAdminContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Dashboard Administrativo",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bienvenido al panel de control de UNAPlanner",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
