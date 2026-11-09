package com.example.simpleflashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(

    primary = AppColors.Primary,

    secondary = AppColors.Secondary,

    background = AppColors.Background,

    surface = AppColors.Surface,

    error = AppColors.Danger

)

@Composable
fun SimpleFlashcardsTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography.styles,
        content = content
    )

}