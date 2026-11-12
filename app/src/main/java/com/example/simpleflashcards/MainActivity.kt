
package com.example.simpleflashcards

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect

import com.example.simpleflashcards.data.FlashcardBackupManager
import com.example.simpleflashcards.data.FlashcardDatabase
import com.example.simpleflashcards.data.FlashcardRepository
import com.example.simpleflashcards.data.ThemePreferences
import com.example.simpleflashcards.data.DeckAppearancePreferences
import com.example.simpleflashcards.data.PracticeSessionPreferences

import com.example.simpleflashcards.navigation.AppNavigation
import com.example.simpleflashcards.ui.theme.SimpleFlashcardsTheme
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.AppColorTheme

import com.example.simpleflashcards.viewmodel.FlashcardViewModel
import com.example.simpleflashcards.viewmodel.FlashcardViewModelFactory

import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FlashcardDatabase.getDatabase(this)

        val repository = FlashcardRepository(database)

        val factory = FlashcardViewModelFactory(repository)

        val viewModel = ViewModelProvider(
            this,
            factory
        )[FlashcardViewModel::class.java]

        val themePreferences = ThemePreferences(applicationContext)

        val appearancePreferences =
            DeckAppearancePreferences(applicationContext)

        val backupManager = FlashcardBackupManager(database)

        val practiceSessionPreferences =
            PracticeSessionPreferences(applicationContext)

        setContent {

            val themeMode by themePreferences.themeMode.collectAsState(
                initial = AppThemeMode.SYSTEM
            )

            val colorTheme by themePreferences.colorTheme.collectAsState(
                initial = AppColorTheme.OCEAN
            )

            val appFontScale by themePreferences.appFontScale.collectAsState(
                initial = 1.10f
            )

            val flashcardFontScale by themePreferences.flashcardFontScale.collectAsState(
                initial = 1.10f
            )

            val customFlashcardFontSizeEnabled by
            themePreferences.customFlashcardFontSizeEnabled.collectAsState(
                initial = false
            )

            val scope = rememberCoroutineScope()

            // Empty string means the saved language has not loaded yet.
            val appLanguage by themePreferences.appLanguage.collectAsState(
                initial = ""
            )

            LaunchedEffect(appLanguage) {

                // Do not apply a fallback language while DataStore loads.
                if (appLanguage.isBlank()) {
                    return@LaunchedEffect
                }

                if (
                    android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.TIRAMISU
                ) {

                    val localeManager =
                        getSystemService(android.app.LocaleManager::class.java)

                    val currentLanguage =
                        localeManager.applicationLocales.toLanguageTags()

                    // Only update Android's locale when it actually differs.
                    if (currentLanguage != appLanguage) {

                        android.util.Log.d(
                            "AppLanguage",
                            "Applying language: $appLanguage (current: $currentLanguage)"
                        )

                        localeManager.applicationLocales =
                            android.os.LocaleList.forLanguageTags(appLanguage)

                    } else {

                        android.util.Log.d(
                            "AppLanguage",
                            "Language already applied: $appLanguage"
                        )
                    }
                }
            }

            SimpleFlashcardsTheme(
                themeMode = themeMode,
                colorTheme = colorTheme,
                appFontScale = appFontScale
            ) {

                AppNavigation(
                    viewModel = viewModel,
                    themeMode = themeMode,
                    colorTheme = colorTheme,

                    appLanguage = appLanguage,

                    onAppLanguageChange = { newLanguage ->
                        scope.launch {
                            themePreferences.setAppLanguage(newLanguage)

                            finishAffinity()
                        }
                    },

                    onThemeModeChange = { newMode ->
                        scope.launch {
                            themePreferences.setThemeMode(newMode)
                        }
                    },

                    onColorThemeChange = { newTheme ->
                        scope.launch {
                            themePreferences.setColorTheme(newTheme)
                        }
                    },

                    appearancePreferences = appearancePreferences,
                    backupManager = backupManager,
                    practiceSessionPreferences = practiceSessionPreferences,

                    appFontScale = appFontScale,
                    flashcardFontScale = flashcardFontScale,
                    customFlashcardFontSizeEnabled =
                        customFlashcardFontSizeEnabled,

                    onAppFontScaleChange = { newScale ->
                        scope.launch {
                            themePreferences.setAppFontScale(newScale)
                        }
                    },

                    onFlashcardFontScaleChange = { newScale ->
                        scope.launch {
                            themePreferences.setFlashcardFontScale(newScale)
                        }
                    },

                    onCustomFlashcardFontSizeEnabledChange = { enabled ->
                        scope.launch {
                            themePreferences.setCustomFlashcardFontSizeEnabled(
                                enabled
                            )
                        }
                    },
                )
            }
        }
    }
}
