package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.ui.components.AppButton
import com.example.simpleflashcards.ui.components.AppDialog
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.components.CsvImportExportButtons

@Composable
fun ManageScreen(
    deckName: String,
    cards: List<Flashcard>,
    onAdd: () -> Unit,
    onEdit: (Flashcard) -> Unit,
    onDelete: (Flashcard) -> Unit,
    onBack: () -> Unit,
    onImportCards: (List<CsvFlashcard>) -> Unit,
    onCustomize: () -> Unit
) {
    var cardToDelete by remember {
        mutableStateOf<Flashcard?>(null)
    }

    Scaffold(
        topBar = {
            AppToolbar(deckName) {

                IconButton(onClick = onCustomize) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Customize deck appearance"
                    )
                }

                CsvImportExportButtons(
                    deckName = deckName,
                    cards = cards,
                    onImport = onImportCards,
                    menuMode = true
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            AppButton(
                text = "Add flashcard",
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("${cards.size} flashcards")

            Spacer(modifier = Modifier.height(12.dp))

            if (cards.isEmpty()) {
                Text("This deck is empty.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = cards,
                        key = { it.id }
                    ) { card ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 16.dp,
                                        end = 4.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = card.question,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                IconButton(
                                    onClick = { onEdit(card) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit flashcard"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        cardToDelete = card
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete flashcard"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            TextButton(onClick = onBack) {
                Text("Back to decks")
            }
        }
    }

    cardToDelete?.let { card ->
        AppDialog(
            title = "Delete flashcard?",
            message = "Are you sure you want to delete this flashcard?",
            confirmText = "Delete",
            onConfirm = {
                onDelete(card)
                cardToDelete = null
            },
            onDismiss = {
                cardToDelete = null
            }
        )
    }
}