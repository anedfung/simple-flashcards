
package com.example.simpleflashcards

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.example.simpleflashcards.data.FlashcardBackupManager
import com.example.simpleflashcards.data.FlashcardDatabase
import com.example.simpleflashcards.data.FlashcardRepository
import com.example.simpleflashcards.data.ThemePreferences
import com.example.simpleflashcards.data.DeckAppearancePreferences
import com.example.simpleflashcards.data.PracticeSessionPreferences

import com.example.simpleflashcards.navigation.AppNavigation
import com.example.simpleflashcards.ui.screens.LanguageSelectionScreen
import com.example.simpleflashcards.ui.theme.SimpleFlashcardsTheme
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.AppColorTheme

import com.example.simpleflashcards.viewmodel.FlashcardViewModel
import com.example.simpleflashcards.viewmodel.FlashcardViewModelFactory

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LANGUAGE_LOADING = "loading"

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

            // DataStore states:
            // "loading" = preference has not loaded yet.
            // null = no language has been selected.
            // "en" or "es" = saved language.
            val appLanguage by themePreferences.appLanguage.collectAsState(
                initial = LANGUAGE_LOADING
            )

            val selectedLanguage = appLanguage

            // Minimum time to display the initial loading screen.
            var initialLoadingFinished by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(Unit) {
                delay(500)
                initialLoadingFinished = true
            }

            // Immediately show loading when the user selects a language.
            var isSavingLanguage by remember {
                mutableStateOf(false)
            }

            // Reset locale readiness whenever the selected language changes.
            var localeReady by remember(selectedLanguage) {
                mutableStateOf(false)
            }

            // Loading overlay starts visible whenever a language is loaded.
            // Keep the loading overlay visible across the entire transition.
            // It resets to visible when Android recreates the Activity.
            var showLoadingOverlay by remember {
                mutableStateOf(true)
            }

            // Apply the selected language.
            LaunchedEffect(selectedLanguage) {

                val language = selectedLanguage
                    ?: return@LaunchedEffect

                if (language == LANGUAGE_LOADING) {
                    return@LaunchedEffect
                }

                isSavingLanguage = false

                if (
                    android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.TIRAMISU
                ) {

                    val localeManager =
                        getSystemService(android.app.LocaleManager::class.java)

                    val currentLanguage =
                        localeManager.applicationLocales.toLanguageTags()

                    if (currentLanguage != language) {

                        android.util.Log.d(
                            "AppLanguage",
                            "Applying language: $language (current: $currentLanguage)"
                        )

                        localeManager.applicationLocales =
                            android.os.LocaleList.forLanguageTags(language)

                        // Android recreates the Activity after applying
                        // the new application locale.
                        return@LaunchedEffect
                    }
                }

                localeReady = true
            }

            // Once the locale is ready, allow the home screen to compose
            // underneath the loading overlay before fading it away.
            LaunchedEffect(
                selectedLanguage,
                localeReady,
                initialLoadingFinished
            ) {

                if (!initialLoadingFinished) {
                    return@LaunchedEffect
                }

                // First installation: show language selection.
                if (selectedLanguage == null) {
                    showLoadingOverlay = false
                    return@LaunchedEffect
                }

                if (selectedLanguage == LANGUAGE_LOADING) {
                    return@LaunchedEffect
                }

                // Wait until Android has applied the selected locale.
                if (localeReady) {

                    // Allow the home screen to compose and render first.
                    androidx.compose.runtime.withFrameNanos { }

                    // Give the home screen additional time to initialize.
                    delay(900)

                    // Finally, reveal the application.
                    showLoadingOverlay = false
                }
            }

            SimpleFlashcardsTheme(
                themeMode = themeMode,
                colorTheme = colorTheme,
                appFontScale = appFontScale
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {

                    // -------------------------------------------------
                    // MAIN APPLICATION CONTENT
                    // -------------------------------------------------

                    when {

                        // Initial loading or language being saved.
                        !initialLoadingFinished ||
                                selectedLanguage == LANGUAGE_LOADING ||
                                isSavingLanguage -> {

                            // Keep the background ready underneath.
                        }

                        // First installation: choose a language.
                        selectedLanguage == null -> {

                            LanguageSelectionScreen(
                                onLanguageSelected = { language ->

                                    // Immediately activate the loading overlay.
                                    showLoadingOverlay = true

                                    isSavingLanguage = true

                                    scope.launch {

                                        // Give Compose time to display the overlay.
                                        delay(250)

                                        // Save the selected language.
                                        themePreferences.setAppLanguage(language)
                                    }
                                }
                            )
                        }

                        // Wait for Android to apply the locale.
                        !localeReady -> {

                            // Loading screen remains above everything.
                        }

                        // Locale is ready: compose the actual application.
                        else -> {

                            AppNavigation(
                                viewModel = viewModel,
                                themeMode = themeMode,
                                colorTheme = colorTheme,

                                appLanguage = selectedLanguage,

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
                                }
                            )
                        }
                    }

                    // -------------------------------------------------
                    // GLOBAL LOADING OVERLAY
                    // -------------------------------------------------

                    AnimatedVisibility(
                        visible =
                            showLoadingOverlay ||
                                    !initialLoadingFinished ||
                                    selectedLanguage == LANGUAGE_LOADING ||
                                    isSavingLanguage ||
                                    (selectedLanguage != null && !localeReady),

                        modifier = Modifier.fillMaxSize(),

                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = 500
                            )
                        )
                    ) {

                        LoadingScreen()
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun LoadingScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()
    }
}
