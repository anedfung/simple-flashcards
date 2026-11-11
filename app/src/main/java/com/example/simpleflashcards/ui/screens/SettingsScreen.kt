
package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import kotlin.math.roundToInt

import com.example.simpleflashcards.R
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.AppColorTheme

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    colorTheme: AppColorTheme,
    appFontScale: Float,
    flashcardFontScale: Float,
    customFlashcardFontSizeEnabled: Boolean,
    appLanguage: String,
    onAppLanguageChange: (String) -> Unit,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onColorThemeChange: (AppColorTheme) -> Unit,
    onAppFontScaleChange: (Float) -> Unit,
    onFlashcardFontScaleChange: (Float) -> Unit,
    onCustomFlashcardFontSizeEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onBackupRestore: () -> Unit
) {
    var pendingLanguage by remember {
        mutableStateOf<String?>(null)
    }

    pendingLanguage?.let { language ->

        AlertDialog(
            onDismissRequest = {
                pendingLanguage = null
            },

            title = {
                Text("Restart required")
            },

            text = {
                Text(
                    "Changing the application language requires " +
                            "closing the app. Your new language will " +
                            "take effect the next time you open it."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        pendingLanguage = null
                        onAppLanguageChange(language)
                    }
                ) {
                    Text("Close app")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        pendingLanguage = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = "Appearance",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Text(
                text = stringResource(R.string.app_language),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.app_language_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                "en" to stringResource(R.string.language_english),
                "es" to stringResource(R.string.language_spanish)
            ).forEach { (languageCode, languageName) ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = appLanguage == languageCode,
                        onClick = {
                            if (languageCode != appLanguage) {
                                pendingLanguage = languageCode
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = languageName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Display mode",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose between light, dark, or your system setting.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppThemeMode.values().forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = themeMode == mode,
                        onClick = { onThemeModeChange(mode) }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = when (mode) {
                            AppThemeMode.SYSTEM -> "System default"
                            AppThemeMode.LIGHT -> "Light"
                            AppThemeMode.DARK -> "Dark"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Color theme",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose the colors used throughout the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppColorTheme.values().forEach { theme ->
                val selected = colorTheme == theme

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = { onColorThemeChange(theme) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = theme.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            theme.lightPrimary,
                            theme.lightSecondary,
                            theme.darkPrimary
                        ).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(17.dp)
                                    .background(color, CircleShape)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // App font size

            Text(
                text = "App font size",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adjust the text size throughout the application.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Font size",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "${(appFontScale * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = appFontScale,
                onValueChange = onAppFontScaleChange,
                valueRange = 0.80f..1.60f,
                steps = 15
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("80%", style = MaterialTheme.typography.bodySmall)
                Text("160%", style = MaterialTheme.typography.bodySmall)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Custom flashcard font size

            Text(
                text = "Custom flashcard font size",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Give flashcards their own font size, independent of the rest of the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Enable custom size",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = if (customFlashcardFontSizeEnabled) {
                            "Flashcards use their own font size."
                        } else {
                            "Flashcards follow the app font size."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = customFlashcardFontSizeEnabled,
                    onCheckedChange = onCustomFlashcardFontSizeEnabledChange
                )
            }

            if (customFlashcardFontSizeEnabled) {

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flashcard font size",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "${(flashcardFontScale * 100).roundToInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = flashcardFontScale,
                    onValueChange = onFlashcardFontScaleChange,
                    valueRange = 0.80f..1.60f,
                    steps = 15
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("80%", style = MaterialTheme.typography.bodySmall)
                    Text("160%", style = MaterialTheme.typography.bodySmall)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "Data",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBackupRestore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Backup & Restore")
            }
        }
    }
}
