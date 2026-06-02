package com.moviles.unaplanner.ui.screens.admin.profile.contactAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.ui.components.AdminTopBar
import com.moviles.unaplanner.ui.theme.BackgroundLight
import com.moviles.unaplanner.ui.theme.CrimsonRed

@Composable
fun CreateCampusContactScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateCampusContactViewModel = viewModel(factory = CreateCampusContactViewModel.Factory)
) {
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                title = UserMessages.CampusContacts.CREATE_TITLE,
                subtitle = UserMessages.CampusContacts.CREATE_SUBTITLE,
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.error != null) {
                ErrorMessageCard(message = uiState.error, onDismiss = { viewModel.clearError() })
            }

            // Card del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = UserMessages.CampusContacts.FORM_SECTION_TITLE,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ContactField(
                        value = uiState.departmentName,
                        onValueChange = viewModel::onDepartmentNameChange,
                        label = UserMessages.CampusContacts.Labels.DEPT_NAME,
                        placeholder = UserMessages.CampusContacts.Placeholders.DEPT_NAME,
                        icon = Icons.Default.Business,
                        error = uiState.fieldErrors["DepartmentName"] ?: uiState.fieldErrors["DepartamentName"]
                    )

                    ContactField(
                        value = uiState.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = UserMessages.CampusContacts.Labels.PHONE,
                        placeholder = UserMessages.CampusContacts.Placeholders.PHONE,
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        error = uiState.fieldErrors["Phone"]
                    )

                    ContactField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = UserMessages.CampusContacts.Labels.EMAIL,
                        placeholder = UserMessages.CampusContacts.Placeholders.EMAIL,
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        error = uiState.fieldErrors["Email"]
                    )

                    ContactField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = UserMessages.CampusContacts.Labels.DESCRIPTION,
                        placeholder = UserMessages.CampusContacts.Placeholders.DESCRIPTION,
                        icon = Icons.Default.Description,
                        singleLine = false,
                        minLines = 3,
                        error = uiState.fieldErrors["Description"]
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.createContact() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(UserMessages.CampusContacts.BTN_SAVE, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

