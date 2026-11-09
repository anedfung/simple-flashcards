package com.example.simpleflashcards.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(

    primary = AppColors.Primary,
    secondary = AppColors.Secondary,

    background = AppColors.Background,
    surface = AppColors.Surface,

    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,

    onBackground = AppColors.TextPrimary,
    onSurface = AppColors.TextPrimary,

    error = AppColors.Danger
)

private val DarkColorScheme = darkColorScheme(

    primary = androidx.compose.ui.graphics.Color(0xFF90A4AE),
    secondary = androidx.compose.ui.graphics.Color(0xFFB0BEC5),

    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),

    onPrimary = androidx.compose.ui.graphics.Color(0xFF182126),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF182126),

    onBackground = androidx.compose.ui.graphics.Color(0xFFECEFF1),
    onSurface = androidx.compose.ui.graphics.Color(0xFFECEFF1),

    error = androidx.compose.ui.graphics.Color(0xFFEF9A9A)
)

@Composable
fun SimpleFlashcardsTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {

    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {

        AppThemeMode.SYSTEM -> systemDark

        AppThemeMode.LIGHT -> false

        AppThemeMode.DARK -> true
    }

    MaterialTheme(

        colorScheme = if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        },

        typography = AppTypography.styles,

        content = content
    )
}