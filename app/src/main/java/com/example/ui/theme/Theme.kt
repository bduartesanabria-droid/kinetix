package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.ui.viewmodel.ThemeMode

val KinetixLightColorScheme = lightColorScheme(
    primary = KinetixPrimary,
    onPrimary = KinetixOnPrimary,
    primaryContainer = KinetixPrimaryContainer,
    onPrimaryContainer = KinetixOnPrimaryContainer,
    secondary = KinetixSecondary,
    onSecondary = KinetixOnSecondary,
    secondaryContainer = KinetixSecondaryContainer,
    onSecondaryContainer = KinetixOnSecondaryContainer,
    tertiary = KinetixTertiary,
    onTertiary = KinetixOnTertiary,
    tertiaryContainer = KinetixTertiaryContainer,
    onTertiaryContainer = KinetixOnTertiaryContainer,
    background = KinetixBackground,
    onBackground = KinetixOnBackground,
    surface = KinetixSurface,
    onSurface = KinetixOnSurface,
    surfaceVariant = KinetixSurfaceVariant,
    onSurfaceVariant = KinetixOnSurfaceVariant,
    surfaceContainerLowest = KinetixSurfaceContainerLowest,
    surfaceContainerLow = KinetixSurfaceContainerLow,
    surfaceContainer = KinetixSurfaceContainer,
    surfaceContainerHigh = KinetixSurfaceContainerHigh,
    surfaceContainerHighest = KinetixSurfaceContainerHighest,
    outline = KinetixOutline,
    outlineVariant = KinetixOutlineVariant,
    error = KinetixError,
    onError = KinetixOnError,
    errorContainer = KinetixErrorContainer,
    onErrorContainer = KinetixOnErrorContainer
)

val KinetixDarkColorScheme = darkColorScheme(
    primary = KinetixDarkPrimary,
    onPrimary = KinetixDarkOnPrimary,
    primaryContainer = KinetixDarkPrimaryContainer,
    onPrimaryContainer = KinetixDarkOnPrimaryContainer,
    secondary = KinetixDarkSecondary,
    onSecondary = KinetixDarkOnSecondary,
    secondaryContainer = KinetixDarkSecondaryContainer,
    onSecondaryContainer = KinetixDarkOnSecondaryContainer,
    tertiary = KinetixDarkTertiary,
    onTertiary = KinetixDarkOnTertiary,
    tertiaryContainer = KinetixDarkTertiaryContainer,
    onTertiaryContainer = KinetixDarkOnTertiaryContainer,
    background = KinetixDarkBackground,
    onBackground = KinetixDarkOnBackground,
    surface = KinetixDarkSurface,
    onSurface = KinetixDarkOnSurface,
    surfaceVariant = KinetixDarkSurfaceVariant,
    onSurfaceVariant = KinetixDarkOnSurfaceVariant,
    surfaceContainerLowest = KinetixDarkSurfaceContainerLowest,
    surfaceContainerLow = KinetixDarkSurfaceContainerLow,
    surfaceContainer = KinetixDarkSurfaceContainer,
    surfaceContainerHigh = KinetixDarkSurfaceContainerHigh,
    surfaceContainerHighest = KinetixDarkSurfaceContainerHighest,
    outline = KinetixDarkOutline,
    outlineVariant = KinetixDarkOutlineVariant,
    error = KinetixDarkError,
    onError = KinetixDarkOnError,
    errorContainer = KinetixDarkErrorContainer,
    onErrorContainer = KinetixDarkOnErrorContainer
)

@Composable
fun KinetixTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    },
    dynamicColor: Boolean = false, // Keep brand aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KinetixDarkColorScheme else KinetixLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias for default template tests
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    KinetixTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
