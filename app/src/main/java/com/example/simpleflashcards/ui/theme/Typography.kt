
package com.example.simpleflashcards.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

object AppTypography {

    private fun TextStyle.scaled(scale: Float): TextStyle {
        return copy(
            fontSize = fontSize * scale,
            lineHeight = lineHeight * scale
        )
    }

    fun scaled(scale: Float): Typography {

        val base = Typography()

        return base.copy(
            displayLarge = base.displayLarge.scaled(scale),
            displayMedium = base.displayMedium.scaled(scale),
            displaySmall = base.displaySmall.scaled(scale),

            headlineLarge = base.headlineLarge.scaled(scale),
            headlineMedium = base.headlineMedium.scaled(scale),
            headlineSmall = base.headlineSmall.scaled(scale),

            titleLarge = base.titleLarge.scaled(scale),
            titleMedium = base.titleMedium.scaled(scale),
            titleSmall = base.titleSmall.scaled(scale),

            bodyLarge = base.bodyLarge.scaled(scale),
            bodyMedium = base.bodyMedium.scaled(scale),
            bodySmall = base.bodySmall.scaled(scale),

            labelLarge = base.labelLarge.scaled(scale),
            labelMedium = base.labelMedium.scaled(scale),
            labelSmall = base.labelSmall.scaled(scale)
        )
    }
}
