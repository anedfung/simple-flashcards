
package com.example.simpleflashcards.ui.theme

enum class AppColorStyle {
    ZEN,
    VIBRANT;

    companion object {
        fun fromPreference(value: String?): AppColorStyle {
            return values().firstOrNull { it.name == value } ?: ZEN
        }
    }
}
