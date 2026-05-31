package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WoodHighlight,
    secondary = WoodMedium,
    tertiary = MetalRivet,
    background = Color(0xFF24150D), // Warm dark wood background
    surface = WoodDark,
    onPrimary = CartoonBlack,
    onSecondary = Color.White,
    onBackground = Color(0xFFF7E7CE), // Parchment/beige text color
    onSurface = Color(0xFFF7E7CE)
)

private val LightColorScheme = lightColorScheme(
    primary = WoodMedium,
    secondary = WoodDark,
    tertiary = MetalRivet,
    background = Color(0xFF382316),
    surface = WoodMedium,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF7E7CE),
    onSurface = Color(0xFFF7E7CE)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}