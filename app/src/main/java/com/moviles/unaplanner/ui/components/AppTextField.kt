package com.moviles.unaplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.unaplanner.ui.theme.AppDivider
import com.moviles.unaplanner.ui.theme.CrimsonRed
import com.moviles.unaplanner.ui.theme.NavyBlue
import com.moviles.unaplanner.ui.theme.SurfaceLight
import com.moviles.unaplanner.ui.theme.TextPrimary
import com.moviles.unaplanner.ui.theme.TextSecondary


@Composable
fun AppTextField(
    value: String,
    label: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // Label
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 8.dp),
            style = TextStyle(
                color = NavyBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                readOnly = readOnly,
                singleLine = singleLine,
                maxLines = maxLines,
                enabled = true,

                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp
                ),

                placeholder = {
                    Text(
                        text = placeholder,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                },

                shape = RoundedCornerShape(12.dp),
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                trailingIcon = trailingIcon,


                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    disabledTextColor = TextPrimary,
                    errorTextColor = CrimsonRed,

                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,

                    cursorColor = NavyBlue,

                    focusedBorderColor = NavyBlue,
                    unfocusedBorderColor = AppDivider,
                    disabledBorderColor = AppDivider
                )
            )


            if (onClick != null) {
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onClick()
                        }
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    AppTextField(
        value = "",
        onValueChange = {},
        label = "CORREO ELECTRÓNICO",
        placeholder = "usuario@una.ac.cr"
    )
}