
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
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.ui.components.AppButton
import com.example.simpleflashcards.ui.components.AppDialog
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.components.CsvImportExportButtons

@Composable
fun ManageScreen(
    deckName: String,
    deckId: Int,
    cards: List<Flashcard>,
    subdecks: List<Deck>,
    allDecks: List<Deck>,
    newSubdeckToRename: Deck?,
    onRenamePromptHandled: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Flashcard) -> Unit,
    onDelete: (Flashcard) -> Unit,
    onBack: () -> Unit,
    onOpenSubdeck: (Deck) -> Unit,
    onPracticeSubdeck: (Deck) -> Unit,
    onCreateSubdeck: (Boolean) -> Unit,
    onRenameSubdeck: (Deck, String) -> Unit,
    onDeleteSubdeck: (Deck) -> Unit,
    onImportCards: (List<CsvFlashcard>) -> Unit,
    onCustomize: () -> Unit
) {
    var cardToDelete by remember {
        mutableStateOf<Flashcard?>(null)
    }

    var showCreateSubdeckWarning by remember {
        mutableStateOf(false)
    }

    var deckToRename by remember {
        mutableStateOf<Deck?>(null)
    }

    var subdeckToDelete by remember {
        mutableStateOf<Deck?>(null)
    }

    // Only cards directly belonging to this deck.
    // Descendant cards remain available for recursive practice.
    val directCards = cards.filter {
        it.deckId == deckId
    }

    val isParent = subdecks.isNotEmpty()

    // Automatically open the rename dialog after creating a subdeck.
    LaunchedEffect(newSubdeckToRename?.id) {
        newSubdeckToRename?.let {
            deckToRename = it
        }
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

                if (!isParent) {
                    CsvImportExportButtons(
                        deckName = deckName,
                        cards = directCards,
                        onImport = onImportCards,
                        menuMode = true
                    )
                }
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
                text = "Create subdeck",
                onClick = {
                    if (directCards.isNotEmpty()) {
                        showCreateSubdeckWarning = true
                    } else {
                        onCreateSubdeck(false)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (subdecks.isEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    text = "Add flashcard",
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (subdecks.isNotEmpty()) {

                Text(
                    text = "${subdecks.size} subdecks",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = subdecks,
                        key = { it.id }
                    ) { subdeck ->

                        val cardCount = cards.count {
                            it.deckId == subdeck.id
                        }

                        val subdeckCount = allDecks.count {
                            it.parentDeckId == subdeck.id
                        }

                        // Same card structure and styling as HomeScreen.
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = subdeck.name,
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "$cardCount flashcards · $subdeckCount subdecks",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onPracticeSubdeck(subdeck)
                                        }
                                    ) {
                                        Text("Practice")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onOpenSubdeck(subdeck)
                                        }
                                    ) {
                                        Text("Open")
                                    }
                                }

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(
                                        onClick = {
                                            deckToRename = subdeck
                                        }
                                    ) {
                                        Text("Rename")
                                    }

                                    TextButton(
                                        onClick = {
                                            subdeckToDelete = subdeck
                                        }
                                    ) {
                                        Text("Delete")
                                    }
                                }
                            }
                        }
                    }
                }

            } else if (directCards.isEmpty()) {

                Text("This deck is empty.")

            } else {

                Text(
                    text = "${directCards.size} flashcards",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = directCards,
                        key = { it.id }
                    ) { card ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
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
                                    onClick = {
                                        onEdit(card)
                                    }
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
                Text("Back")
            }
        }
    }

    // Warning when the current deck has direct flashcards.
    if (showCreateSubdeckWarning) {
        AlertDialog(
            onDismissRequest = {
                showCreateSubdeckWarning = false
            },
            title = {
                Text("Move flashcards?")
            },
            text = {
                Text(
                    "This deck currently contains ${directCards.size} flashcards. " +
                            "To create subdecks, these cards must be moved into a new " +
                            "subdeck named \"New subdeck\". You can rename it afterward."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateSubdeckWarning = false
                        onCreateSubdeck(true)
                    }
                ) {
                    Text("Move cards and create subdeck")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateSubdeckWarning = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
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

    deckToRename?.let { deck ->
        DeckRenameDialog(
            deck = deck,
            onDismiss = {
                deckToRename = null
                onRenamePromptHandled()
            },
            onConfirm = { newName ->
                onRenameSubdeck(deck, newName)
                deckToRename = null
                onRenamePromptHandled()
            }
        )
    }

    subdeckToDelete?.let { deck ->
        AppDialog(
            title = "Delete subdeck?",
            message = "Deleting \"${deck.name}\" will also delete its nested subdecks and flashcards.",
            confirmText = "Delete",
            onConfirm = {
                onDeleteSubdeck(deck)
                subdeckToDelete = null
            },
            onDismiss = {
                subdeckToDelete = null
            }
        )
    }
}

@Composable
private fun DeckRenameDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember(deck.id) {
        mutableStateOf(deck.name)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Rename subdeck")
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text("Deck name")
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
