package com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.theme.BackgroundLight

@Composable
fun CareerAdminScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onNavigateToSection: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            AdminTopBar(
                title = "Carreras",
                subtitle = "7 carreras registradas · Campus Sarapiquí",
                showBackButton = true,
                onBackClick = onBackClick,
                showAddButton = true,
                onAddClick = onAddClick
            )
        },
        bottomBar = {
            AdminAppBottomNavBar(
                selectedIndex = 1,
                onItemSelected = onNavigateToSection
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            CareerAdminContent()
        }
    }
}

@Composable
fun CareerAdminContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gestión de Carreras",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aquí podrás administrar las carreras del campus",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
