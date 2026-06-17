package com.moviles.unaplanner.ui.screens.student

import android.R.attr.duration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.data.remote.model.StudentProfileDto
import com.moviles.unaplanner.data.remote.model.UpdateProfileRequest
import com.moviles.unaplanner.data.remote.model.UpdateStudentProfileRequest
import com.moviles.unaplanner.ui.components.AppTextField
import com.moviles.unaplanner.ui.components.AppTopBar
import com.moviles.unaplanner.ui.theme.NavyBlue
import kotlinx.coroutines.launch
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.moviles.unaplanner.ui.theme.AppDivider
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import com.moviles.unaplanner.core.UserMessages

private val careers = listOf(
    Pair(1, "Ingeniería en Sistemas de Información"),
    Pair(2, "Ingeniería en Ciencia de Datos"),
    Pair(3, "Administración"),
    Pair(4, "Administración de Oficinas"),
    Pair(5, "Educación Comercial"),
    Pair(6, "Comercio y Negocios Internacionales"),
    Pair(7, "Inglés")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: Int,
    viewModel: ProfileStudentViewModel,
    initialProfile: StudentProfileDto,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf(initialProfile.fullName) }
    var enterYear by remember { mutableStateOf(initialProfile.enterYear?.toString() ?: "") }
    var email by remember { mutableStateOf(initialProfile.email) }

    var selectedCareerId by remember { mutableStateOf(initialProfile.careerId) }
    var selectedCareerName by remember {
        mutableStateOf(
            careers.find { it.first == initialProfile.careerId }?.second
                ?: initialProfile.careerName
        )
    }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.setInitialProfile(initialProfile)
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar(
                message = UserMessages.EditProfile.SUCCESS,
                duration = SnackbarDuration.Short
            )
            viewModel.resetSaveState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = UserMessages.EditProfile.TITLE,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = UserMessages.EditProfile.SUBTITLE,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1B3E)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (uiState) {

                is ProfileStudentViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ProfileStudentViewModel.UiState.Error -> {
                    Text(
                        text = (uiState as ProfileStudentViewModel.UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    //Email — read-only
                    AppTextField(
                        value = email,
                        onValueChange = {},
                        label = UserMessages.EditProfile.Labels.EMAIL,
                        placeholder = "",
                        readOnly = true
                    )

                    // Full Name
                    AppTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = UserMessages.EditProfile.Labels.FULL_NAME,
                        placeholder = UserMessages.EditProfile.Placeholders.FULL_NAME
                    )

                    //Major — dropdown
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = UserMessages.EditProfile.Labels.CAREER,
                            style = TextStyle(
                                color = NavyBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCareerName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NavyBlue,
                                    unfocusedBorderColor = AppDivider,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                careers.forEach { career ->
                                    DropdownMenuItem(
                                        text = { Text(career.second) },
                                        onClick = {
                                            selectedCareerId = career.first
                                            selectedCareerName = career.second
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Year of Enrollment
                    AppTextField(
                        value = enterYear,
                        onValueChange = { enterYear = it },
                        label = UserMessages.EditProfile.Labels.ENTER_YEAR,
                        placeholder = UserMessages.EditProfile.Placeholders.ENTER_YEAR,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            when {
                                fullName.isBlank() -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            UserMessages.EditProfile.Errors.EMPTY_FIELDS
                                        )
                                    }
                                }
                                enterYear.toIntOrNull() == null -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            UserMessages.EditProfile.Errors.INVALID_YEAR
                                        )
                                    }
                                }
                                else -> {
                                    viewModel.updateProfile(
                                        userId,
                                        UpdateStudentProfileRequest(
                                            fullName  = fullName,
                                            careerId  = selectedCareerId,
                                            enterYear = enterYear.toIntOrNull() ?: 0
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0D1B3E)
                        ),
                        enabled = uiState !is ProfileStudentViewModel.UiState.Loading
                    ) {
                        if (uiState is ProfileStudentViewModel.UiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = UserMessages.EditProfile.BTN_SAVE,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
