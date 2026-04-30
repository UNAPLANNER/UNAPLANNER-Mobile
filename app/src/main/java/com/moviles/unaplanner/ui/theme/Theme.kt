package com.moviles.unaplanner.ui.theme


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NavyBlue,
    secondary = CrimsonRed,
    tertiary = EventOrange,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextOnDark,
    onSecondary = TextOnRed,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    outline = Divider
)

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    secondary = CrimsonRed,
    tertiary = EventOrange,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextOnDark,
    onSecondary = TextOnRed,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Divider

)

@Composable
fun UNAPLANNERTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}