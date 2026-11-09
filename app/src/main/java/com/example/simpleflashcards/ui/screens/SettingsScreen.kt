package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.theme.AppThemeMode

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onBack: () -> Unit
) {

    Scaffold(

        topBar = {
            AppToolbar("Appearance") {
                TextButton(onClick = onBack) {
                    Text("Done")
                }
            }
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)

        ) {

            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose how the application should look.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppThemeMode.entries.forEach { mode ->

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    RadioButton(

                        selected = themeMode == mode,

                        onClick = {
                            onThemeModeChange(mode)
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(

                            text = when (mode) {

                                AppThemeMode.SYSTEM -> "System default"

                                AppThemeMode.LIGHT -> "Light"

                                AppThemeMode.DARK -> "Dark"
                            },

                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (mode == AppThemeMode.SYSTEM) {

                            Text(
                                text = "Follow your Android appearance settings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}