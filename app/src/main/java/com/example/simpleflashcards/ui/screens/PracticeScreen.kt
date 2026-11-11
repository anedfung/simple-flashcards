package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.data.PracticeSession
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.components.FlashcardView
import com.example.simpleflashcards.ui.theme.DeckAppearance

@Composable
fun PracticeScreen(
    deckId: Int,
    deckName: String,
    cards: List<Flashcard>,
    appearance: DeckAppearance,
    flashcardTextScale: Float = 1f,
    initialSession: PracticeSession?,
    shuffleEnabled: Boolean,
    onToggleShuffle: () -> Unit,
    onSessionChange: (PracticeSession) -> Unit,
    onBack: () -> Unit
) {

    var orderedCards by remember(deckId) {
        mutableStateOf(emptyList<Flashcard>())
    }

    var currentCardId by remember(deckId) {
        mutableStateOf(initialSession?.currentCardId)
    }

    // Initialize or reconcile the card order.
    LaunchedEffect(cards) {

        if (cards.isNotEmpty()) {

            val availableIds = cards.map { it.id }.toSet()

            if (orderedCards.isEmpty()) {

                val savedOrder = initialSession
                    ?.takeIf { it.deckId == deckId }
                    ?.cardIds
                    ?.mapNotNull { id ->
                        cards.firstOrNull { it.id == id }
                    }
                    .orEmpty()

                val remainingCards = cards.filter {
                        card -> savedOrder.none { it.id == card.id }
                }

                orderedCards = if (savedOrder.isNotEmpty()) {
                    savedOrder + remainingCards
                } else if (shuffleEnabled) {
                    cards.shuffled()
                } else {
                    cards
                }

            } else {

                orderedCards = orderedCards.filter {
                    it.id in availableIds
                } + cards.filter { card ->
                    orderedCards.none { it.id == card.id }
                }
            }

            if (orderedCards.none { it.id == currentCardId }) {
                currentCardId = orderedCards.firstOrNull()?.id
            }
        }
    }

    val currentIndex = orderedCards.indexOfFirst {
        it.id == currentCardId
    }.let { index ->
        if (index < 0) 0 else index
    }

    // Save the session whenever its position, order, or mode changes.
    LaunchedEffect(
        deckId,
        orderedCards,
        currentCardId,
        shuffleEnabled
    ) {
        if (orderedCards.isNotEmpty()) {

            val validCurrentId = currentCardId
                ?.takeIf { id -> orderedCards.any { it.id == id } }
                ?: orderedCards.first().id

            onSessionChange(
                PracticeSession(
                    deckId = deckId,
                    shuffleEnabled = shuffleEnabled,
                    cardIds = orderedCards.map { it.id },
                    currentCardId = validCurrentId
                )
            )
        }
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = deckName,
                onBack = onBack
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (orderedCards.isEmpty()) {

                Text("This deck has no flashcards.")

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onBack) {
                    Text("Back to decks")
                }

            } else {

                val currentCard = orderedCards[currentIndex]

                Text(
                    text = "${currentIndex + 1} / ${orderedCards.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(30.dp))

                FlashcardView(
                    question = currentCard.question,
                    answer = currentCard.answer,
                    appearance = appearance,
                    textScale = flashcardTextScale
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedButton(
                        onClick = {
                            val previousIndex =
                                if (currentIndex == 0) {
                                    orderedCards.lastIndex
                                } else {
                                    currentIndex - 1
                                }

                            currentCardId =
                                orderedCards[previousIndex].id
                        }
                    ) {
                        Text("Previous")
                    }

                    OutlinedButton(
                        onClick = {
                            val nextIndex =
                                (currentIndex + 1) % orderedCards.size

                            currentCardId =
                                orderedCards[nextIndex].id
                        }
                    ) {
                        Text("Next")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text("Shuffle")

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = shuffleEnabled,
                        onCheckedChange = {

                            val nextShuffleState = !shuffleEnabled

                            orderedCards = if (nextShuffleState) {
                                cards.shuffled()
                            } else {
                                cards
                            }

                            currentCardId = orderedCards.firstOrNull()?.id

                            onToggleShuffle()
                        }
                    )
                }
            }
        }
    }
}