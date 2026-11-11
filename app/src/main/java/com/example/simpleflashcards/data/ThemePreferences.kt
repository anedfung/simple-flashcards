
package com.example.simpleflashcards.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.AppColorTheme
import com.example.simpleflashcards.ui.theme.FontSizeOption

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.appSettingsDataStore by preferencesDataStore(
    name = "app_settings"
)

class ThemePreferences(context: Context) {

    private val appContext = context.applicationContext

    private val themeKey = stringPreferencesKey("theme_mode")
    private val colorThemeKey = stringPreferencesKey("color_theme")

    private val appFontSizeKey =
        stringPreferencesKey("app_font_size")

    private val flashcardFontSizeKey =
        stringPreferencesKey("flashcard_font_size")

    val themeMode: Flow<AppThemeMode> =
        appContext.appSettingsDataStore.data.map { preferences ->
            AppThemeMode.fromPreference(preferences[themeKey])
        }

    val colorTheme: Flow<AppColorTheme> =
        appContext.appSettingsDataStore.data.map { preferences ->
            AppColorTheme.fromPreference(preferences[colorThemeKey])
        }

    val appFontSize: Flow<FontSizeOption> =
        appContext.appSettingsDataStore.data.map { preferences ->
            FontSizeOption.fromPreference(
                preferences[appFontSizeKey]
            )
        }

    val flashcardFontSize: Flow<FontSizeOption> =
        appContext.appSettingsDataStore.data.map { preferences ->
            FontSizeOption.fromPreference(
                preferences[flashcardFontSizeKey]
            )
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

    suspend fun setAppFontSize(size: FontSizeOption) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[appFontSizeKey] = size.name
        }
    }

    suspend fun setFlashcardFontSize(size: FontSizeOption) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[flashcardFontSizeKey] = size.name
        }
    }
}
