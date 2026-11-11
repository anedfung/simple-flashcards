
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource

import com.example.simpleflashcards.R
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

    val directCards = cards.filter {
        it.deckId == deckId
    }

    val isParent = subdecks.isNotEmpty()

    LaunchedEffect(newSubdeckToRename?.id) {
        newSubdeckToRename?.let {
            deckToRename = it
        }
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = deckName,
                onBack = onBack
            ) {
                IconButton(onClick = onCustomize) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = stringResource(
                            R.string.customize_deck
                        )
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
                text = stringResource(R.string.create_subdeck),
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
                    text = stringResource(R.string.add_flashcard),
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (subdecks.isNotEmpty()) {

                Text(
                    text = pluralStringResource(
                        R.plurals.subdeck_count,
                        subdecks.size,
                        subdecks.size
                    ),
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

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = stringResource(
                                        R.string.deck_card_summary,
                                        pluralStringResource(
                                            R.plurals.flashcard_count,
                                            cardCount,
                                            cardCount
                                        ),
                                        pluralStringResource(
                                            R.plurals.subdeck_count,
                                            subdeckCount,
                                            subdeckCount
                                        )
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            onPracticeSubdeck(subdeck)
                                        }
                                    ) {
                                        Text(stringResource(R.string.practice))
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onOpenSubdeck(subdeck)
                                        }
                                    ) {
                                        Text(stringResource(R.string.open))
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
                                        Text(stringResource(R.string.rename))
                                    }

                                    TextButton(
                                        onClick = {
                                            subdeckToDelete = subdeck
                                        }
                                    ) {
                                        Text(stringResource(R.string.delete))
                                    }
                                }
                            }
                        }
                    }
                }

            } else if (directCards.isEmpty()) {

                Text(stringResource(R.string.deck_empty))

            } else {

                Text(
                    text = pluralStringResource(
                        R.plurals.flashcard_count,
                        directCards.size,
                        directCards.size
                    ),
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
                                        contentDescription = stringResource(
                                            R.string.edit_flashcard
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        cardToDelete = card
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(
                                            R.string.delete_flashcard
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateSubdeckWarning) {
        AlertDialog(
            onDismissRequest = {
                showCreateSubdeckWarning = false
            },
            title = {
                Text(stringResource(R.string.move_flashcards_title))
            },
            text = {
                Text(
                    stringResource(
                        R.string.move_flashcards_message,
                        directCards.size,
                        stringResource(R.string.new_subdeck_default_name)
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateSubdeckWarning = false
                        onCreateSubdeck(true)
                    }
                ) {
                    Text(stringResource(R.string.move_cards_create_subdeck))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateSubdeckWarning = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    cardToDelete?.let { card ->
        AppDialog(
            title = stringResource(R.string.delete_flashcard_title),
            message = stringResource(R.string.delete_flashcard_message),
            confirmText = stringResource(R.string.delete),
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
            title = stringResource(R.string.delete_subdeck_title),
            message = stringResource(
                R.string.delete_subdeck_message,
                deck.name
            ),
            confirmText = stringResource(R.string.delete),
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
            Text(stringResource(R.string.rename_deck_title))
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = {
                    Text(stringResource(R.string.deck_name))
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
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
