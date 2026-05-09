package com.moviles.unaplanner.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.screens.admin.profile.AdminProfileScreen
import com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin.CareerAdminContent
import com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin.ContactAdminScreen
import com.moviles.unaplanner.ui.screens.admin.profile.homeAdmin.HomeAdminContent
import com.moviles.unaplanner.ui.theme.BackgroundLight

@Composable
fun AdminMainScreen(
    onLogout: () -> Unit
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val titles = listOf("UNAPlanner Admin", "Carreras", "Contactos", "Mi Perfil")
    val subtitles = listOf(
        null,
        "7 carreras registradas · Campus Sarapiquí",
        "Directorio de la Sede",
        null
    )

    Scaffold(
        topBar = {
            if (selectedIndex < 3) { // No mostramos AdminTopBar en perfil porque tiene su propio diseño
                AdminTopBar(
                    title = titles[selectedIndex],
                    subtitle = subtitles[selectedIndex],
                    isHome = selectedIndex == 0,
                    showBackButton = selectedIndex != 0,
                    onBackClick = { selectedIndex = 0 },
                    showAddButton = selectedIndex == 1 || selectedIndex == 2
                )
            }
        },
        bottomBar = {
            AdminAppBottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        },
        containerColor = BackgroundLight,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (selectedIndex) {
                0 -> Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    HomeAdminContent()
                }
                1 -> Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    CareerAdminContent()
                }
                2 -> Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    ContactAdminScreen(
                        isInsideTab = true,
                        onBackClick = { selectedIndex = 0 }
                    )
                }
                3 -> AdminProfileScreen(
                    onLogoutClick = onLogout,
                    isInsideTab = true
                )
            }
        }
    }
}
