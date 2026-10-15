package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AetherColorScheme = darkColorScheme(
    primary = StarGoldPrimary,
    onPrimary = OnPrimary,
    primaryContainer = StarGoldPrimaryContainer,
    onPrimaryContainer = OnPrimary,
    secondary = IrisPurpleSecondary,
    onSecondary = OnSecondary,
    secondaryContainer = IrisPurpleSecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = EmeraldTertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = EmeraldTertiary,
    background = SurfaceDark,
    onBackground = OnSurface,
    surface = SurfaceDark,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceBright = SurfaceBright,
    error = ErrorRed,
    onError = OnError,
    errorContainer = ErrorContainerRed
)

@Composable
fun AetherFocusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AetherColorScheme,
        typography = Typography,
        content = content
    )
}
