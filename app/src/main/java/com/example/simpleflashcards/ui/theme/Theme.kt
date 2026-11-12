
package com.example.simpleflashcards.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalFlashcardGradientColors =
    staticCompositionLocalOf {
        listOf(Color.White, Color.White)
    }

@Composable
fun SimpleFlashcardsTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    colorTheme: AppColorTheme = AppColorTheme.OCEAN,
    colorStyle: AppColorStyle = AppColorStyle.ZEN,
    appFontScale: Float = 1.10f,
    content: @Composable () -> Unit,
) {

    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val palette = colorTheme.palette(
        style = colorStyle,
        darkTheme = darkTheme
    )

    val colors = if (darkTheme) {

        darkColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.cardStart,
            onPrimary = palette.onPrimary,
            onSecondary = palette.onPrimary,
            onBackground = palette.text,
            onSurface = palette.text,
            onSurfaceVariant = palette.text,
            error = Color(0xFFFF8A80)
        )

    } else {

        lightColorScheme(
            primary = palette.primary,
            secondary = palette.secondary,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.cardStart,
            onPrimary = palette.onPrimary,
            onSecondary = palette.onPrimary,
            onBackground = palette.text,
            onSurface = palette.text,
            onSurfaceVariant = palette.text,
            error = Color(0xFFD32F2F)
        )
    }

    CompositionLocalProvider(
        LocalFlashcardGradientColors provides listOf(
            palette.cardStart,
            palette.cardEnd
        )
    ) {

        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography.scaled(appFontScale),
            content = content
        )
    }
}
