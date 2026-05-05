package com.moviles.unaplanner

import androidx.compose.runtime.Composable
import com.moviles.unaplanner.navigation.AppNavHost
import com.moviles.unaplanner.ui.theme.UNAPLANNERTheme

@Composable
fun UnaPlannerApp() {
    UNAPLANNERTheme{
        AppNavHost()
    }
}

