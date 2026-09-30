package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val PehnoColorScheme = lightColorScheme(
    primary = PehnoGreenPrimary,
    onPrimary = PehnoWhite,
    primaryContainer = PehnoGreenLight,
    onPrimaryContainer = PehnoGreenDark,
    secondary = PehnoGreenAccent,
    onSecondary = PehnoWhite,
    tertiary = PehnoBlack,
    onTertiary = PehnoWhite,
    background = PehnoWhite,
    onBackground = PehnoBlack,
    surface = PehnoWhite,
    onSurface = PehnoBlack,
    surfaceVariant = PehnoSurface,
    onSurfaceVariant = PehnoTextSecondary,
    outline = PehnoCardBorder
)

@Composable
fun FitLookStudioTheme(
    isWeddingMode: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PehnoColorScheme,
        typography = Typography,
        content = content
    )
}
