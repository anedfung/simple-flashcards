package com.example.simpleflashcards.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppColorTheme(
    val displayName: String,
    val description: String,
    val lightPrimary: Color,
    val lightSecondary: Color,
    val lightBackground: Color,
    val lightSurface: Color,
    val lightText: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkText: Color
) {
    OCEAN(
        "Ocean", "Calm blues and teal",
        Color(0xFF287A91), Color(0xFF65AEB5),
        Color(0xFFF2F8FA), Color(0xFFFFFFFF), Color(0xFF18343D),
        Color(0xFF80C5D2), Color(0xFF8BC5BE),
        Color(0xFF101F25), Color(0xFF1B3038), Color(0xFFE1F0F2)
    ),

    FOREST(
        "Forest", "Natural greens",
        Color(0xFF477A54), Color(0xFF8BAF78),
        Color(0xFFF3F7F0), Color(0xFFFFFFFF), Color(0xFF263B2A),
        Color(0xFF9BCB91), Color(0xFFB4C99A),
        Color(0xFF17231A), Color(0xFF243429), Color(0xFFE5F0E1)
    ),

    LAVENDER(
        "Lavender", "Soft purple tones",
        Color(0xFF7962A6), Color(0xFFA994CE),
        Color(0xFFF7F4FB), Color(0xFFFFFFFF), Color(0xFF352B45),
        Color(0xFFC0A8EA), Color(0xFFBBAAD8),
        Color(0xFF211B2B), Color(0xFF30283D), Color(0xFFECE4F6)
    ),

    SUNRISE(
        "Sunrise", "Warm peach and amber",
        Color(0xFFB96D43), Color(0xFFE0A36E),
        Color(0xFFFCF5EF), Color(0xFFFFFFFF), Color(0xFF493023),
        Color(0xFFFFB887), Color(0xFFE8BD8C),
        Color(0xFF281C17), Color(0xFF392820), Color(0xFFF6E6D9)
    ),

    ROSE(
        "Rose", "Gentle pink and burgundy",
        Color(0xFFAD5C78), Color(0xFFD99AAF),
        Color(0xFFFBF3F6), Color(0xFFFFFFFF), Color(0xFF442A35),
        Color(0xFFE9A5BD), Color(0xFFD8A9BA),
        Color(0xFF281B21), Color(0xFF39272F), Color(0xFFF4E3E9)
    ),

    SLATE(
        "Slate", "Minimal blue-grey",
        Color(0xFF526B80), Color(0xFF8299AA),
        Color(0xFFF3F5F7), Color(0xFFFFFFFF), Color(0xFF263541),
        Color(0xFFA5BED0), Color(0xFF9BAFBE),
        Color(0xFF192127), Color(0xFF27343D), Color(0xFFE5EBEF)
    );

    companion object {
        fun fromPreference(value: String?): AppColorTheme {
            return values().firstOrNull { it.name == value } ?: OCEAN
        }
    }
}