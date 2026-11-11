
package com.example.simpleflashcards.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource

import com.example.simpleflashcards.R
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
            AppToolbar(
                title = stringResource(R.string.backup_restore),
                onBack = onBack
            )
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
                text = stringResource(R.string.backup_title),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.backup_description),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    exportLauncher.launch("flashcards_backup.json")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.export_backup))
            }

            HorizontalDivider()

            Text(
                text = stringResource(R.string.restore_title),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.restore_description),
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
                Text(stringResource(R.string.choose_backup_file))
            }

            Text(
                text = stringResource(R.string.restore_warning),
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
                Text(stringResource(R.string.restore_backup_title))
            },
            text = {
                Text(stringResource(R.string.restore_backup_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onImport(uri)
                        pendingImport = null
                    }
                ) {
                    Text(stringResource(R.string.restore))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingImport = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
