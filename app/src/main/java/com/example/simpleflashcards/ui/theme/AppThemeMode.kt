package com.example.simpleflashcards.ui.theme

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromPreference(value: String?): AppThemeMode {
            return values().firstOrNull { it.name == value } ?: SYSTEM
        }
    }
}