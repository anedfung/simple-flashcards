
package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import kotlin.math.roundToInt

import com.example.simpleflashcards.R
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.AppColorTheme
import com.example.simpleflashcards.ui.theme.AppColorStyle

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    colorTheme: AppColorTheme,
    colorStyle: AppColorStyle,
    appFontScale: Float,
    flashcardFontScale: Float,
    customFlashcardFontSizeEnabled: Boolean,
    appLanguage: String,
    onAppLanguageChange: (String) -> Unit,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onColorThemeChange: (AppColorTheme) -> Unit,
    onColorStyleChange: (AppColorStyle) -> Unit,
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
                Text(stringResource(R.string.restart_required))
            },
            text = {
                Text(stringResource(R.string.language_restart_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingLanguage = null
                        onAppLanguageChange(language)
                    }
                ) {
                    Text(stringResource(R.string.close_app))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingLanguage = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = stringResource(R.string.settings),
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

            // Language

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

            // Display mode

            Text(
                text = stringResource(R.string.display_mode),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.display_mode_description),
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
                            AppThemeMode.SYSTEM ->
                                stringResource(R.string.system_default)

                            AppThemeMode.LIGHT ->
                                stringResource(R.string.light)

                            AppThemeMode.DARK ->
                                stringResource(R.string.dark)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Color style

            Text(
                text = stringResource(R.string.color_style),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.color_style_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppColorStyle.values().forEach { style ->

                val styleName = when (style) {
                    AppColorStyle.ZEN ->
                        stringResource(R.string.color_style_zen)

                    AppColorStyle.VIBRANT ->
                        stringResource(R.string.color_style_vibrant)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = colorStyle == style,
                        onClick = {
                            onColorStyleChange(style)
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = styleName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Color theme

            Text(
                text = stringResource(R.string.color_theme),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.color_theme_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppColorTheme.values().forEach { theme ->
                val selected = colorTheme == theme

                val previewDark = when (themeMode) {
                    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                    AppThemeMode.LIGHT -> false
                    AppThemeMode.DARK -> true
                }

                val previewPalette = theme.palette(
                    style = colorStyle,
                    darkTheme = previewDark
                )

                val themeName = when (theme) {
                    AppColorTheme.OCEAN ->
                        stringResource(R.string.theme_ocean)

                    AppColorTheme.FOREST ->
                        stringResource(R.string.theme_forest)

                    AppColorTheme.LAVENDER ->
                        stringResource(R.string.theme_lavender)

                    AppColorTheme.SUNRISE ->
                        stringResource(R.string.theme_sunrise)

                    AppColorTheme.ROSE ->
                        stringResource(R.string.theme_rose)

                    AppColorTheme.SLATE ->
                        stringResource(R.string.theme_slate)
                }

                val themeDescription = when (theme) {
                    AppColorTheme.OCEAN ->
                        stringResource(R.string.theme_ocean_description)

                    AppColorTheme.FOREST ->
                        stringResource(R.string.theme_forest_description)

                    AppColorTheme.LAVENDER ->
                        stringResource(R.string.theme_lavender_description)

                    AppColorTheme.SUNRISE ->
                        stringResource(R.string.theme_sunrise_description)

                    AppColorTheme.ROSE ->
                        stringResource(R.string.theme_rose_description)

                    AppColorTheme.SLATE ->
                        stringResource(R.string.theme_slate_description)
                }

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
                            text = themeName,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = themeDescription,
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
                            previewPalette.primary,
                            previewPalette.secondary,
                            previewPalette.cardStart,
                            previewPalette.cardEnd
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
                text = stringResource(R.string.app_font_size),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.app_font_size_description),
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
                    text = stringResource(R.string.font_size),
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
                text = stringResource(R.string.custom_flashcard_font_size),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.custom_flashcard_font_description),
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
                        text = stringResource(R.string.enable_custom_size),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = if (customFlashcardFontSizeEnabled) {
                            stringResource(R.string.flashcards_own_font_size)
                        } else {
                            stringResource(R.string.flashcards_follow_app_font_size)
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
                        text = stringResource(R.string.flashcard_font_size),
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

            // Data

            Text(
                text = stringResource(R.string.data),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBackupRestore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.backup_restore))
            }
        }
    }
}
