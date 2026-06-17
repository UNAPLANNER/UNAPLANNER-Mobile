package com.moviles.unaplanner.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.unaplanner.data.remote.model.CampusContact
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.components.AdminAppBottomNavBar
import com.moviles.unaplanner.ui.components.AppBottomNavBar
import com.moviles.unaplanner.ui.screens.admin.profile.AdminProfileScreen
import com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin.CareerAdminContent
import com.moviles.unaplanner.ui.screens.admin.profile.careerAdmin.CareerAdminViewModel
import com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin.ContactAdminScreen
import com.moviles.unaplanner.ui.screens.admin.profile.homeAdmin.HomeAdminContent
import com.moviles.unaplanner.ui.theme.BackgroundLight

@Composable
fun AdminMainScreen(
    onLogout: () -> Unit,
    onNavigateToCreateContact: () -> Unit = {},
    onNavigateToEditContact: (CampusContact) -> Unit = {},
    navController: NavController? = null
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val careerViewModel: CareerAdminViewModel = viewModel(factory = CareerAdminViewModel.Factory)
    val careers = careerViewModel.uiState.careers
    val careersCampusLabel = careers
        .mapNotNull { it.campusName }
        .distinct()
        .singleOrNull() ?: "Campus institucional"
    val careersSubtitle = if (careers.isEmpty()) {
        "Carreras registradas"
    } else {
        "${careers.size} carreras registradas - $careersCampusLabel"
    }

    val titles = listOf("UNAPlanner Admin", "Carreras", "Contactos", "Mi Perfil")
    val subtitles = listOf(
        null,
        careersSubtitle,
        "Directorio de la Sede",
        null
    )

    Scaffold(
        topBar = {
            if (selectedIndex < 3) { // We do not show AdminTopBar on the profile because it has its own design
                AdminTopBar(
                    title = titles[selectedIndex],
                    subtitle = subtitles[selectedIndex],
                    isHome = selectedIndex == 0,
                    showBackButton = selectedIndex != 0,
                    onBackClick = { selectedIndex = 0 },
                    showAddButton = selectedIndex == 1 || selectedIndex == 2,
                    onAddClick = {
                        if (selectedIndex == 2) {
                            onNavigateToCreateContact()
                        }
                    }
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
                    CareerAdminContent(viewModel = careerViewModel)
                }
                2 -> Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    ContactAdminScreen(
                        isInsideTab = true,
                        onBackClick = { selectedIndex = 0 },
                        onAddClick = onNavigateToCreateContact,
                        onEditClick = onNavigateToEditContact,
                        navController = navController
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
