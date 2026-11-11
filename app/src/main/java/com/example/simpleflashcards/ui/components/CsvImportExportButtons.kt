
package com.example.simpleflashcards.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.R
import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.CsvUtils
import com.example.simpleflashcards.data.Flashcard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CsvImportExportButtons(
    deckName: String,
    cards: List<Flashcard>,
    onImport: (List<CsvFlashcard>) -> Unit,
    menuMode: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }

    val openFailed = stringResource(R.string.csv_open_failed)
    val exportSuccess = stringResource(R.string.csv_export_success)
    val exportFailed = stringResource(R.string.csv_export_failed)
    val importSuccess = stringResource(R.string.csv_import_success)
    val importFailed = stringResource(R.string.csv_import_failed)
    val readFailed = stringResource(R.string.csv_read_failed)
    val fileName = stringResource(R.string.flashcards_filename)
    val unknownError = stringResource(R.string.unknown_error)

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val csv = CsvUtils.export(cards)

                    withContext(Dispatchers.IO) {
                        context.contentResolver
                            .openOutputStream(uri)
                            ?.bufferedWriter(Charsets.UTF_8)
                            ?.use { it.write(csv) }
                            ?: error(openFailed)
                    }

                    Toast.makeText(
                        context,
                        exportSuccess,
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    val message = exportFailed ?: unknownError

                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val csv = withContext(Dispatchers.IO) {
                        context.contentResolver
                            .openInputStream(uri)
                            ?.bufferedReader(Charsets.UTF_8)
                            ?.use { it.readText() }
                            ?: error(readFailed)
                    }

                    val importedCards = CsvUtils.import(csv)
                    onImport(importedCards)

                    Toast.makeText(
                        context,
                        importSuccess,
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    val message = importFailed ?: unknownError

                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun importCsv() {
        importLauncher.launch(
            arrayOf("text/*", "application/vnd.ms-excel")
        )
    }

    fun exportCsv() {
        val filename = deckName
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
            .ifBlank { fileName }

        exportLauncher.launch("$filename.csv")
    }

    if (menuMode) {
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.import_csv))
                    },
                    onClick = {
                        expanded = false
                        importCsv()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.export_csv))
                    },
                    onClick = {
                        expanded = false
                        exportCsv()
                    }
                )
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = { importCsv() }) {
                Text(stringResource(R.string.import_csv))
            }

            Button(onClick = { exportCsv() }) {
                Text(stringResource(R.string.export_csv))
            }
        }
    }
}
