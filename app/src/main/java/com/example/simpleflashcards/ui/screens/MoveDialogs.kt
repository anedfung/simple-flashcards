
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

/**
 * Represents a deck and its indentation level in the hierarchy.
 */
private data class DeckTreeItem(
    val deck: Deck,
    val depth: Int
)

/**
 * Converts the flat deck list into a hierarchical list.
 *
 * The original deck objects and IDs are preserved.
 */
private fun buildDeckTree(decks: List<Deck>): List<DeckTreeItem> {
    val result = mutableListOf<DeckTreeItem>()
    val visited = mutableSetOf<Int>()

    fun addChildren(parentId: Int?, depth: Int) {
        decks
            .filter { it.parentDeckId == parentId }
            .forEach { deck ->

                // Prevent infinite recursion if invalid cyclic data exists.
                if (visited.add(deck.id)) {
                    result.add(
                        DeckTreeItem(
                            deck = deck,
                            depth = depth
                        )
                    )

                    addChildren(deck.id, depth + 1)
                }
            }
    }

    // Start with root decks.
    addChildren(null, 0)

    // Include any orphaned or cyclic entries instead of hiding them.
    decks.forEach { deck ->
        if (visited.add(deck.id)) {
            result.add(
                DeckTreeItem(
                    deck = deck,
                    depth = 0
                )
            )

            addChildren(deck.id, 1)
        }
    }

    return result
}

/**
 * A single selectable deck in the hierarchy.
 */
@Composable
private fun DeckTreeOption(
    item: DeckTreeItem,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding((item.depth * 20).dp, 4.dp, 0.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            enabled = enabled,
            onClick = onClick
        )

        Text(
            text = item.deck.name,
            modifier = Modifier.padding(start = 8.dp),
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun MoveCardsDialog(
    currentDeck: Deck,
    sourceCards: List<Flashcard>,
    selectedCardIds: Set<Int>,
    allDecks: List<Deck>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var destinationId by remember(currentDeck.id) {
        mutableStateOf<Int?>(null)
    }

    val deckTree = remember(allDecks) {
        buildDeckTree(allDecks)
    }

    val parent = allDecks.firstOrNull {
        it.id == currentDeck.parentDeckId
    }

    val parentChildren = allDecks.filter {
        it.parentDeckId == parent?.id
    }

    val allSourceCardsSelected =
        sourceCards.isNotEmpty() &&
                selectedCardIds.size == sourceCards.size &&
                sourceCards.all { it.id in selectedCardIds }

    val eligibleDeckIds = remember(
        currentDeck.id,
        allDecks,
        allSourceCardsSelected
    ) {
        allDecks.filter { destination ->

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

        }.map { it.id }.toSet()
    }

    // Clear the destination if it becomes invalid after selection changes.
    LaunchedEffect(eligibleDeckIds) {
        if (destinationId !in eligibleDeckIds) {
            destinationId = null
        }
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

                if (eligibleDeckIds.isEmpty()) {
                    Text(
                        stringResource(
                            R.string.no_valid_flashcard_destinations
                        )
                    )
                } else {
                    deckTree.forEach { item ->

                        val eligible =
                            item.deck.id in eligibleDeckIds

                        DeckTreeOption(
                            item = item,
                            selected = destinationId == item.deck.id,
                            enabled = eligible,
                            onClick = {
                                destinationId = item.deck.id
                            }
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
                enabled = destinationId != null &&
                        destinationId in eligibleDeckIds
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

    val deckTree = remember(allDecks) {
        buildDeckTree(allDecks)
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

    val eligibleDestinationIds = remember(
        movingDeck.id,
        movingDeck.parentDeckId,
        allDecks,
        cards,
        descendantIds
    ) {
        allDecks.filter { destination ->

            // A deck cannot be moved into itself.
            if (destination.id == movingDeck.id) {
                return@filter false
            }

            // A deck cannot be moved into one of its descendants.
            if (destination.id in descendantIds) {
                return@filter false
            }

            // Keep the existing restriction on moving directly
            // back into its current parent.
            if (destination.id == movingDeck.parentDeckId) {
                return@filter false
            }

            val destinationHasCards = cards.any {
                it.deckId == destination.id
            }

            val destinationHasChildren = allDecks.any {
                it.parentDeckId == destination.id
            }

            // A destination containing both direct cards and
            // subdecks is structurally invalid.
            !(destinationHasCards && destinationHasChildren)

        }.map { it.id }.toSet()
    }

    val selectedDestination = destinationKey
        ?.takeIf { it != "root" }
        ?.removePrefix("deck:")
        ?.toIntOrNull()

    // Clear an invalid selection if the available destinations change.
    LaunchedEffect(eligibleDestinationIds) {
        if (
            selectedDestination != null &&
            selectedDestination !in eligibleDestinationIds
        ) {
            destinationKey = null
        }
    }

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

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    deckTree.forEach { item ->

                        val eligible =
                            item.deck.id in eligibleDestinationIds

                        DeckTreeOption(
                            item = item,
                            selected = destinationKey == "deck:${item.deck.id}",
                            enabled = eligible,
                            onClick = {
                                destinationKey = "deck:${item.deck.id}"
                            }
                        )
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

                            if (
                                targetId != null &&
                                targetId in eligibleDestinationIds
                            ) {
                                val targetHasCards = cards.any {
                                    it.deckId == targetId
                                }

                                val targetHasChildren = allDecks.any {
                                    it.parentDeckId == targetId
                                }

                                if (targetHasCards && targetHasChildren) {
                                    // This should not happen with a valid deck hierarchy.
                                    return@TextButton
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
                    enabled = destinationKey == "root" ||
                            selectedDestination in eligibleDestinationIds
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
