package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val NapDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = PureBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldLight,
    onSecondary = PureBlack,
    tertiary = EmeraldAccent,
    onTertiary = PureWhite,
    background = PureBlack,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextMuted,
    outline = GoldPrimary.copy(alpha = 0.5f),
    outlineVariant = DarkCardBorder
)

@Composable
fun FitLookStudioTheme(
    isWeddingMode: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NapDarkColorScheme,
        typography = Typography,
        content = content
    )
}
