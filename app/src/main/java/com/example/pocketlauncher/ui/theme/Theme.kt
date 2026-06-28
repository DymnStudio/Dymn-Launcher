package com.example.pocketlauncher.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LauncherColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF0A84FF),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF64D2FF),
    onSecondary = Color(0xFF001D2B),
    background = Color(0xFF0B0B10),
    onBackground = Color(0xFFF7F7FA),
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF7F7FA),
    onSurfaceVariant = Color(0xFFD1D1D6),
)

@Composable
fun PocketLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LauncherColors,
        content = content,
    )
}
