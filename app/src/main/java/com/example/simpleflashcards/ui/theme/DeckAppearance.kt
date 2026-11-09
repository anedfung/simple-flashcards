package com.example.simpleflashcards.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

enum class DeckCardColor(
    val displayName: String,
    val background: Color?,
    val foreground: Color?
) {
    DEFAULT("Default", null, null),
    BLUE("Blue", Color(0xFFDCEBFA), Color(0xFF18324A)),
    GREEN("Green", Color(0xFFDFF2E1), Color(0xFF1B3A24)),
    PEACH("Peach", Color(0xFFFCE5D8), Color(0xFF492719)),
    LAVENDER("Lavender", Color(0xFFEAE1F8), Color(0xFF33234D)),
    YELLOW("Yellow", Color(0xFFFFF1C9), Color(0xFF493A12));

    companion object {
        fun fromPreference(value: String?): DeckCardColor {
            return values().firstOrNull { it.name == value } ?: DEFAULT
        }
    }
}

enum class DeckFont(
    val displayName: String,
    val family: FontFamily
) {
    DEFAULT("Default", FontFamily.Default),
    SANS_SERIF("Sans serif", FontFamily.SansSerif),
    SERIF("Serif", FontFamily.Serif),
    MONOSPACE("Monospace", FontFamily.Monospace);

    companion object {
        fun fromPreference(value: String?): DeckFont {
            return values().firstOrNull { it.name == value } ?: DEFAULT
        }
    }
}

data class DeckAppearance(
    val questionColor: DeckCardColor = DeckCardColor.DEFAULT,
    val answerColor: DeckCardColor = DeckCardColor.DEFAULT,
    val font: DeckFont = DeckFont.DEFAULT
)