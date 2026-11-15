
package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.R
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard

@Composable
fun MoveCardsDialog(
    currentDeck: Deck,
    sourceCards: List<Flashcard>,
    selectedCardIds: Set<Int>,
    allDecks: List<Deck>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var destinationId by remember(currentDeck.id, selectedCardIds) {
        mutableStateOf<Int?>(null)
    }

    val parent = allDecks.firstOrNull {
        it.id == currentDeck.parentDeckId
    }

    val parentChildren = allDecks.filter {
        it.parentDeckId == parent?.id
    }

    val allSourceCardsSelected =
        sourceCards.isNotEmpty() &&
                selectedCardIds.size == sourceCards.size

    val eligibleDecks = allDecks.filter { destination ->

        if (destination.id == currentDeck.id) {
            return@filter false
        }

        val children = allDecks.filter {
            it.parentDeckId == destination.id
        }

        val isSpecialParentDestination =
            destination.id == parent?.id &&
                    parentChildren.size == 1 &&
                    parentChildren.firstOrNull()?.id == currentDeck.id &&
                    allSourceCardsSelected

        children.isEmpty() || isSpecialParentDestination
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.move_flashcards_title))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    stringResource(
                        R.string.move_flashcards_selection_count,
                        selectedCardIds.size
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (eligibleDecks.isEmpty()) {
                    Text(
                        stringResource(
                            R.string.no_valid_flashcard_destinations
                        )
                    )
                }

                eligibleDecks.forEach { deck ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = destinationId == deck.id,
                            onClick = {
                                destinationId = deck.id
                            }
                        )

                        Text(
                            text = deck.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    destinationId?.let(onConfirm)
                },
                enabled = destinationId != null
            ) {
                Text(stringResource(R.string.move))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun MoveDeckDialog(
    movingDeck: Deck,
    allDecks: List<Deck>,
    cards: List<Flashcard>,
    onDismiss: () -> Unit,
    onConfirm: (Int?, Boolean) -> Unit
) {
    var destinationKey by remember(movingDeck.id) {
        mutableStateOf<String?>(null)
    }

    var showWarning by remember {
        mutableStateOf(false)
    }

    var pendingDestination by remember {
        mutableStateOf<Int?>(null)
    }

    val movingDeckHasCards = cards.any {
        it.deckId == movingDeck.id
    }

    val descendantIds = remember(movingDeck.id, allDecks) {
        val result = mutableSetOf<Int>()
        val pending = ArrayDeque<Int>()

        pending.add(movingDeck.id)

        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()

            allDecks
                .filter { it.parentDeckId == current }
                .forEach { child ->
                    if (result.add(child.id)) {
                        pending.add(child.id)
                    }
                }
        }

        result
    }

    val eligibleDestinations = allDecks.filter { destination ->

        if (destination.id == movingDeck.id) {
            return@filter false
        }

        if (destination.id in descendantIds) {
            return@filter false
        }

        if (destination.id == movingDeck.parentDeckId) {
            return@filter false
        }

        val destinationHasChildren = allDecks.any {
            it.parentDeckId == destination.id
        }

        // A deck with direct flashcards cannot be moved into
        // a destination that already contains subdecks.
        !(movingDeckHasCards && destinationHasChildren)
    }

    val selectedDestination = destinationKey
        ?.takeIf { it != "root" }
        ?.removePrefix("deck:")
        ?.toIntOrNull()

    if (showWarning) {
        val destination = allDecks.firstOrNull {
            it.id == pendingDestination
        }

        AlertDialog(
            onDismissRequest = {
                showWarning = false
            },
            title = {
                Text(stringResource(R.string.move_deck_warning_title))
            },
            text = {
                Text(
                    stringResource(
                        R.string.move_deck_cards_warning,
                        movingDeck.name,
                        destination?.name.orEmpty(),
                        "${destination?.name.orEmpty()} - Cards"
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(pendingDestination, true)
                    }
                ) {
                    Text(stringResource(R.string.move_deck))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showWarning = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(stringResource(R.string.move_deck_title))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (movingDeck.parentDeckId != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = destinationKey == "root",
                                onClick = {
                                    destinationKey = "root"
                                }
                            )

                            Text(
                                text = stringResource(R.string.move_to_root),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    eligibleDestinations.forEach { deck ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = destinationKey == "deck:${deck.id}",
                                onClick = {
                                    destinationKey = "deck:${deck.id}"
                                }
                            )

                            Text(
                                text = deck.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (destinationKey == "root") {
                            onConfirm(null, false)
                        } else {
                            val targetId = selectedDestination

                            if (targetId != null) {
                                val targetHasCards = cards.any {
                                    it.deckId == targetId
                                }

                                if (targetHasCards) {
                                    pendingDestination = targetId
                                    showWarning = true
                                } else {
                                    onConfirm(targetId, false)
                                }
                            }
                        }
                    },
                    enabled = destinationKey != null
                ) {
                    Text(stringResource(R.string.continue_label))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
