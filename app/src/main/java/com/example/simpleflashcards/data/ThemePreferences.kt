
package com.example.simpleflashcards.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
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

    private val appFontScaleKey =
        floatPreferencesKey("app_font_scale")

    private val flashcardFontScaleKey =
        floatPreferencesKey("flashcard_font_scale")

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
            preferences[appFontScaleKey] ?: 1.10f
        }

    val flashcardFontScale: Flow<Float> =
        appContext.appSettingsDataStore.data.map { preferences ->
            preferences[flashcardFontScaleKey] ?: 1.10f
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
            preferences[appFontScaleKey] = scale
        }
    }

    suspend fun setFlashcardFontScale(scale: Float) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[flashcardFontScaleKey] = scale
        }
    }

    suspend fun setCustomFlashcardFontSizeEnabled(enabled: Boolean) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[customFlashcardFontSizeEnabledKey] = enabled
        }
    }
}
