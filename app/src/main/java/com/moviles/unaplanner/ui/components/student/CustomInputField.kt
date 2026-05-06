package com.moviles.unaplanner.ui.components.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.ui.theme.AppDivider
import com.moviles.unaplanner.ui.theme.NavyBlue
import com.moviles.unaplanner.ui.theme.SurfaceLight
import com.moviles.unaplanner.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Etiqueta en la parte superior
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A4A4A),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Campo de texto estilizado
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = Color.LightGray, fontSize = 14.sp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceLight,
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = AppDivider,
                cursorColor = NavyBlue
            ),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}
