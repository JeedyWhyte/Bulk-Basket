package com.bulkbasket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Green600,
    onPrimary = Neutral50,
    secondary = Orange500,
    background = Neutral50,
    surface = Neutral50,
    error = Red500,
)

private val DarkColors = darkColorScheme(
    primary = Green600,
    onPrimary = Neutral50,
    secondary = Orange500,
    background = Neutral900,
    surface = Neutral900,
    error = Red500,
)

@Composable
fun BulkBasketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}