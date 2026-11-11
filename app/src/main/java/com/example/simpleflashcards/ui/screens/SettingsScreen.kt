package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    onThemeModeChange: (AppThemeMode) -> Unit,
    onColorThemeChange: (AppColorTheme) -> Unit,
    onAppFontScaleChange: (Float) -> Unit,
    onFlashcardFontScaleChange: (Float) -> Unit,
    onCustomFlashcardFontSizeEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onBackupRestore: () -> Unit
) {
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
                text = "Text size",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Adjust the text size throughout the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            FontSizeSlider(
                title = "App font size",
                value = appFontScale,
                enabled = true,
                onValueChange = onAppFontScaleChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Custom flashcard font size",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Use a separate size for flashcard content.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = customFlashcardFontSizeEnabled,
                    onCheckedChange = onCustomFlashcardFontSizeEnabledChange
                )
            }

            FontSizeSlider(
                title = "Flashcard font size",
                value = flashcardFontScale,
                enabled = customFlashcardFontSizeEnabled,
                onValueChange = onFlashcardFontScaleChange
            )

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

@Composable
private fun FontSizeSlider(
    title: String,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )

            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.8f..1.6f,
            steps = 15,
            enabled = enabled
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("80%", style = MaterialTheme.typography.labelSmall)
            Text("160%", style = MaterialTheme.typography.labelSmall)
        }
    }
}