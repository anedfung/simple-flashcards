package com.example.simpleflashcards.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.AppColorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.appSettingsDataStore by preferencesDataStore(
    name = "app_settings"
)

class ThemePreferences(context: Context) {

    private val appContext = context.applicationContext

    private val themeKey = stringPreferencesKey("theme_mode")
    private val colorThemeKey = stringPreferencesKey("color_theme")
    private val appFontScaleKey = floatPreferencesKey("app_font_scale")
    private val flashcardFontScaleKey = floatPreferencesKey("flashcard_font_scale")
    private val customFlashcardFontSizeEnabledKey =
        booleanPreferencesKey("custom_flashcard_font_size_enabled")

    val themeMode: Flow<AppThemeMode> =
        appContext.appSettingsDataStore.data.map { preferences ->
            AppThemeMode.fromPreference(preferences[themeKey])
        }

    val colorTheme: Flow<AppColorTheme> =
        appContext.appSettingsDataStore.data.map { preferences ->
            AppColorTheme.fromPreference(preferences[colorThemeKey])
        }

    val appFontScale: Flow<Float> =
        appContext.appSettingsDataStore.data.map { preferences ->
            (preferences[appFontScaleKey] ?: DEFAULT_APP_FONT_SCALE)
                .coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
        }

    val flashcardFontScale: Flow<Float> =
        appContext.appSettingsDataStore.data.map { preferences ->
            (preferences[flashcardFontScaleKey] ?: DEFAULT_FLASHCARD_FONT_SCALE)
                .coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
        }

    val customFlashcardFontSizeEnabled: Flow<Boolean> =
        appContext.appSettingsDataStore.data.map { preferences ->
            preferences[customFlashcardFontSizeEnabledKey] ?: false
        }

    suspend fun setThemeMode(mode: AppThemeMode) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[themeKey] = mode.name
        }
    }

    suspend fun setColorTheme(theme: AppColorTheme) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[colorThemeKey] = theme.name
        }
    }

    suspend fun setAppFontScale(scale: Float) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[appFontScaleKey] = normalizeFontScale(scale)
        }
    }

    suspend fun setFlashcardFontScale(scale: Float) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[flashcardFontScaleKey] = normalizeFontScale(scale)
        }
    }

    suspend fun setCustomFlashcardFontSizeEnabled(enabled: Boolean) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[customFlashcardFontSizeEnabledKey] = enabled
        }
    }

    private fun normalizeFontScale(scale: Float): Float {
        val clamped = scale.coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
        return (kotlin.math.round((clamped - MIN_FONT_SCALE) / FONT_SCALE_STEP) *
            FONT_SCALE_STEP + MIN_FONT_SCALE)
    }

    companion object {
        const val MIN_FONT_SCALE = 0.8f
        const val MAX_FONT_SCALE = 1.6f
        const val FONT_SCALE_STEP = 0.05f
        const val DEFAULT_APP_FONT_SCALE = 1.1f
        const val DEFAULT_FLASHCARD_FONT_SCALE = 1.1f
    }
}