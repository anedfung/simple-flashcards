package com.example.simpleflashcards.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simpleflashcards.ui.components.AppToolbar

@Composable
fun BackupRestoreScreen(
    onExport: (Uri) -> Unit,
    onImport: (Uri) -> Unit,
    onBack: () -> Unit,
    statusMessage: String?
) {
    var pendingImport by remember {
        mutableStateOf<Uri?>(null)
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/json"
        )
    ) { uri ->
        uri?.let(onExport)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        pendingImport = uri
    }

    Scaffold(
        topBar = {
            AppToolbar("Backup & Restore") {
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Backup",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Save all your decks and flashcards into one backup file.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    exportLauncher.launch("flashcards_backup.json")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export backup")
            }

            HorizontalDivider()

            Text(
                text = "Restore",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Restore your decks and flashcards from a previously exported backup.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedButton(
                onClick = {
                    importLauncher.launch(
                        arrayOf("application/json", "text/json", "*/*")
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Choose backup file")
            }

            Text(
                text = "Warning: restoring a backup replaces all existing decks and flashcards.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )

            statusMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    pendingImport?.let { uri ->

        AlertDialog(
            onDismissRequest = {
                pendingImport = null
            },

            title = {
                Text("Restore backup?")
            },

            text = {
                Text(
                    "This will replace all your current decks and flashcards. " +
                            "This action cannot be undone. Continue?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        onImport(uri)
                        pendingImport = null
                    }
                ) {
                    Text("Restore")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        pendingImport = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}