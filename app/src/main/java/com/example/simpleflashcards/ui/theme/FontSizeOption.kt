
package com.example.simpleflashcards.ui.theme

enum class FontSizeOption(
    val displayName: String,
    val scaleFactor: Float
) {
    SMALL("Small", 0.9f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.1f),
    EXTRA_LARGE("Extra large", 1.2f);

    companion object {
        fun fromPreference(value: String?): FontSizeOption {
            return values().firstOrNull {
                it.name == value
            } ?: LARGE
        }
    }
}
