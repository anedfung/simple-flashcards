package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.ui.components.AppButton
import com.example.simpleflashcards.ui.components.AppDialog
import com.example.simpleflashcards.ui.components.AppToolbar

@Composable
fun HomeScreen(
    decks: List<Deck>,
    cards: List<Flashcard>,
    onPractice: (Deck) -> Unit,
    onManage: (Deck) -> Unit,
    onAddDeck: (String) -> Unit,
    onRenameDeck: (Deck, String) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onSettings: () -> Unit
) {

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var deckToRename by remember {
        mutableStateOf<Deck?>(null)
    }

    var deckToDelete by remember {
        mutableStateOf<Deck?>(null)
    }

    Scaffold(
        topBar = {
            AppToolbar("My Decks") {

                IconButton(
                    onClick = onSettings
                ) {

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
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
                text = "Create new deck",
                onClick = {
                    showAddDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${decks.size} decks",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = decks,
                    key = { it.id }
                ) { deck ->

                    val cardCount = cards.count {
                        it.deckId == deck.id
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = deck.name,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "$cardCount flashcards",
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
                                        onPractice(deck)
                                    }
                                ) {
                                    Text("Practice")
                                }

                                OutlinedButton(
                                    onClick = {
                                        onManage(deck)
                                    }
                                ) {
                                    Text("Manage")
                                }
                            }

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                TextButton(
                                    onClick = {
                                        deckToRename = deck
                                    }
                                ) {
                                    Text("Rename")
                                }

                                TextButton(
                                    onClick = {
                                        if (decks.size > 1) {
                                            deckToDelete = deck
                                        }
                                    },
                                    enabled = decks.size > 1
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add deck dialog
    if (showAddDialog) {

        DeckNameDialog(
            title = "Create deck",
            initialName = "",
            confirmText = "Create",
            onDismiss = {
                showAddDialog = false
            },
            onConfirm = { name ->

                onAddDeck(name)
                showAddDialog = false
            }
        )
    }

    // Rename dialog
    deckToRename?.let { deck ->

        DeckNameDialog(
            title = "Rename deck",
            initialName = deck.name,
            confirmText = "Save",
            onDismiss = {
                deckToRename = null
            },
            onConfirm = { name ->

                onRenameDeck(deck, name)
                deckToRename = null
            }
        )
    }

    // Delete confirmation
    deckToDelete?.let { deck ->

        AppDialog(
            title = "Delete deck?",
            message = "Deleting \"${deck.name}\" will also delete all its flashcards.",
            confirmText = "Delete",
            onConfirm = {

                onDeleteDeck(deck)
                deckToDelete = null
            },
            onDismiss = {
                deckToDelete = null
            }
        )
    }
}

@Composable
private fun DeckNameDialog(
    title: String,
    initialName: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var name by remember(initialName) {
        mutableStateOf(initialName)
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(title)
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
                Text(confirmText)
            }
        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}