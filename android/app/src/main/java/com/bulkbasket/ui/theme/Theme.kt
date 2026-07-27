package com.bulkbasket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary700,
    onPrimary = White,
    primaryContainer = Primary50,
    onPrimaryContainer = Primary800,
    secondary = Accent500,
    onSecondary = TextPrimary,
    secondaryContainer = Accent100,
    onSecondaryContainer = Accent700,
    background = Gray50,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = Gray100,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = White,
    errorContainer = ErrorLight,
    onErrorContainer = ErrorDark,
    outline = Gray200,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary500,
    onPrimary = White,
    primaryContainer = Primary800,
    onPrimaryContainer = Primary100,
    secondary = Accent500,
    onSecondary = TextPrimary,
    secondaryContainer = Primary800,
    onSecondaryContainer = Accent100,
    background = Primary900,
    onBackground = Gray50,
    surface = Primary800,
    onSurface = Gray50,
    surfaceVariant = Primary700,
    onSurfaceVariant = Gray300,
    error = Error,
    onError = White,
    errorContainer = ErrorDark,
    onErrorContainer = ErrorLight,
    outline = Primary700,
)

@Composable
fun BulkBasketTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
