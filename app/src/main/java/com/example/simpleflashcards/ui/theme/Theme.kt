package com.example.simpleflashcards.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

@Composable
fun SimpleFlashcardsTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    colorTheme: AppColorTheme = AppColorTheme.OCEAN,
    appFontScale: Float = 1.10f,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colors = if (darkTheme) {
        val cardSurface = colorTheme.darkPrimary
            .copy(alpha = 0.16f)
            .compositeOver(colorTheme.darkSurface)

        darkColorScheme(
            primary = colorTheme.darkPrimary,
            secondary = colorTheme.darkSecondary,
            background = colorTheme.darkBackground,
            surface = colorTheme.darkSurface,
            surfaceVariant = cardSurface,
            onPrimary = Color(0xFF172126),
            onSecondary = Color(0xFF172126),
            onBackground = colorTheme.darkText,
            onSurface = colorTheme.darkText,
            onSurfaceVariant = colorTheme.darkText,
            error = Color(0xFFFF8A80)
        )
    } else {
        val cardSurface = colorTheme.lightPrimary
            .copy(alpha = 0.09f)
            .compositeOver(colorTheme.lightSurface)

        lightColorScheme(
            primary = colorTheme.lightPrimary,
            secondary = colorTheme.lightSecondary,
            background = colorTheme.lightBackground,
            surface = colorTheme.lightSurface,
            surfaceVariant = cardSurface,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = colorTheme.lightText,
            onSurface = colorTheme.lightText,
            onSurfaceVariant = colorTheme.lightText,
            error = Color(0xFFD32F2F)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography.scaled(appFontScale),
        content = content
    )
}