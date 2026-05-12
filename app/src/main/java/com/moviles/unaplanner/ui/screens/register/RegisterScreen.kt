package com.moviles.unaplanner.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.unaplanner.R
import com.moviles.unaplanner.core.UserMessages
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.BACK_BUTTON
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.BUTTON_REGISTER
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.HEADER_SUBTITLE
import com.moviles.unaplanner.core.UserMessages.RegisterStudent.HEADER_TITLE
import com.moviles.unaplanner.ui.components.CustomInputField
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.HeaderGradientEnd
import com.moviles.unaplanner.ui.theme.HeaderGradientStart
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState



/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7FA))
            .verticalScroll(scrollState)
    ) {
        // --- HEADER CON GRADIENTE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(listOf(HeaderGradientStart, HeaderGradientEnd)))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = UserMessages.RegisterStudent.BACK_BUTTON,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = UserMessages.RegisterStudent.HEADER_TITLE,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = UserMessages.RegisterStudent.HEADER_SUBTITLE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_circular),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- FORMULARIO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Datos Personales
            CustomInputField(label = UserMessages.RegisterStudent.NAME_LABEL, value = viewModel.name, onValueChange = { viewModel.name = it }, placeholder = UserMessages.RegisterStudent.NAME_PLACEHOLDER)

            CustomInputField(label = UserMessages.RegisterStudent.EMAIL_LABEL, value = viewModel.email, onValueChange = { viewModel.email = it }, placeholder = UserMessages.RegisterStudent.EMAIL_PLACEHOLDER)

            CustomInputField(label = UserMessages.RegisterStudent.PASSWORD_LABEL, value = viewModel.password, onValueChange = { viewModel.password = it }, placeholder = UserMessages.RegisterStudent.PASSWORD_PLACEHOLDER, isPassword = true)

            CustomInputField(label = UserMessages.RegisterStudent.CONFIRM_PASSWORD_LABEL, value = viewModel.confirmPassword, onValueChange = { viewModel.confirmPassword = it }, placeholder = UserMessages.RegisterStudent.CONFIRM_PASSWORD_PLACEHOLDER, isPassword = true)

            // Selectores Académicos
            //CAMPUS
            CustomInputField(
                label = UserMessages.RegisterStudent.CAMPUS_LABEL,
                value = viewModel.campus,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.CAMPUS
                    viewModel.showBottomSheet = true
                }
            )

            //MAJOR
            CustomInputField(
                label = UserMessages.RegisterStudent.MAJOR_LABEL,
                value = viewModel.major,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.MAJOR
                    viewModel.showBottomSheet = true
                }
            )

            //DOUBLE MAJOR
            CustomInputField(
                label = UserMessages.RegisterStudent.DOUBLE_MAJOR_LABEL,
                value = viewModel.secondMajor,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.DOUBLE_MAJOR
                    viewModel.showBottomSheet = true
                }
            )

            // Study Plan
            CustomInputField(
                label = "PLAN DE ESTUDIOS",
                value = viewModel.studyPlanSelectedName,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.STUDY_PLAN
                    viewModel.showBottomSheet = true
                }
            )
            CustomInputField(
                label = "AÑO DE INGRESO",
                value = viewModel.entryYear,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) viewModel.entryYear = it },
                placeholder = "Ej: 2024"
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.CYCLE_LABEL,
                value = viewModel.currentCycle,
                onValueChange = { viewModel.currentCycle = it },
                placeholder = "I Ciclo 2026"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = { viewModel.onRegisterClicked { onNavigateToHome() } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(16.dp),
                enabled = viewModel.uiState !is RegisterState.Loading
            ) {
                if (viewModel.uiState is RegisterState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = UserMessages.RegisterStudent.BUTTON_REGISTER, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            if (viewModel.uiState is RegisterState.Error) {
                Text(
                    text = (viewModel.uiState as RegisterState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

}*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7FA))
            .verticalScroll(scrollState)
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(listOf(HeaderGradientStart, HeaderGradientEnd)))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = UserMessages.RegisterStudent.BACK_BUTTON,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = UserMessages.RegisterStudent.HEADER_TITLE,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = UserMessages.RegisterStudent.HEADER_SUBTITLE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.logo_circular),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- FORMULARIO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campos de texto
            CustomInputField(
                label = UserMessages.RegisterStudent.NAME_LABEL,
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                placeholder = UserMessages.RegisterStudent.NAME_PLACEHOLDER
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.EMAIL_LABEL,
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                placeholder = UserMessages.RegisterStudent.EMAIL_PLACEHOLDER
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.PASSWORD_LABEL,
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                placeholder = UserMessages.RegisterStudent.PASSWORD_PLACEHOLDER,
                isPassword = true
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.CONFIRM_PASSWORD_LABEL,
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                placeholder = UserMessages.RegisterStudent.CONFIRM_PASSWORD_PLACEHOLDER,
                isPassword = true
            )

            // Selectores
            CustomInputField(
                label = UserMessages.RegisterStudent.CAMPUS_LABEL,
                value = viewModel.campus,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.CAMPUS
                    viewModel.showBottomSheet = true
                }
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.MAJOR_LABEL,
                value = viewModel.major,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.MAJOR
                    viewModel.showBottomSheet = true
                }
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.DOUBLE_MAJOR_LABEL,
                value = viewModel.secondMajor,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.DOUBLE_MAJOR
                    viewModel.showBottomSheet = true
                }
            )

            CustomInputField(
                label = "PLAN DE ESTUDIOS",
                value = viewModel.studyPlanSelectedName,
                onValueChange = {},
                readOnly = true,
                isSelector = true,
                onClick = {
                    viewModel.currentSelectionType = SelectionType.STUDY_PLAN
                    viewModel.showBottomSheet = true
                }
            )

            CustomInputField(
                label = "AÑO DE INGRESO",
                value = viewModel.entryYear,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) viewModel.entryYear = it
                },
                placeholder = "Ej: 2024"
            )

            CustomInputField(
                label = UserMessages.RegisterStudent.CYCLE_LABEL,
                value = viewModel.currentCycle,
                onValueChange = { viewModel.currentCycle = it },
                placeholder = "I Ciclo 2026"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = { viewModel.onRegisterClicked { onNavigateToHome() } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(16.dp),
                enabled = viewModel.uiState !is RegisterState.Loading
            ) {
                if (viewModel.uiState is RegisterState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = UserMessages.RegisterStudent.BUTTON_REGISTER,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (viewModel.uiState is RegisterState.Error) {
                Text(
                    text = (viewModel.uiState as RegisterState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- BottomSheet ---
    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                when (viewModel.currentSelectionType) {
                    SelectionType.CAMPUS -> {
                        items(viewModel.campusList) { campus ->
                            Text(
                                text = campus,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onItemSelected(campus)
                                        viewModel.showBottomSheet = false
                                    }
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    SelectionType.MAJOR, SelectionType.DOUBLE_MAJOR -> {
                        items(viewModel.majorsList) { major ->
                            Text(
                                text = major,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onItemSelected(major)
                                        viewModel.showBottomSheet = false
                                    }
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    SelectionType.STUDY_PLAN -> {
                        items(viewModel.studyPlansList) { plan ->
                            Text(
                                text = plan.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onItemSelected(plan)
                                        viewModel.showBottomSheet = false
                                    }
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    else -> {
                        item {
                            Text(
                                text = "No hay opciones disponibles",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    UNAPLANNERTheme(darkTheme = false) {
        RegisterScreen(
            onNavigateToHome = {  },
            onBack = {  }
        )
    }
}