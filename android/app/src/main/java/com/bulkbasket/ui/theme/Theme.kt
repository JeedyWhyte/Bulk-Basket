package com.bulkbasket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Green600,
    onPrimary = White,
    primaryContainer = Green100,
    onPrimaryContainer = Green800,
    secondary = Orange500,
    onSecondary = White,
    secondaryContainer = Orange100,
    onSecondaryContainer = Neutral900,
    background = Neutral50,
    onBackground = Neutral900,
    surface = White,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral600,
    error = Red500,
    onError = White,
    errorContainer = Red50,
    onErrorContainer = Red500,
    outline = Neutral400,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green600,
    onPrimary = White,
    primaryContainer = Green800,
    onPrimaryContainer = Green100,
    secondary = Orange500,
    onSecondary = White,
    secondaryContainer = Neutral800,
    onSecondaryContainer = Orange100,
    background = Neutral900,
    onBackground = Neutral50,
    surface = Neutral800,
    onSurface = Neutral50,
    surfaceVariant = Neutral800,
    onSurfaceVariant = Neutral400,
    error = Red500,
    onError = White,
    errorContainer = Red50,
    onErrorContainer = Red500,
    outline = Neutral600,
)

@Composable
fun BulkBasketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}