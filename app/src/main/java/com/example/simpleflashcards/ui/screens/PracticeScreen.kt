package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.components.FlashcardView
import com.example.simpleflashcards.ui.theme.DeckAppearance

@Composable
fun PracticeScreen(
    deckName: String,
    cards: List<Flashcard>,
    appearance: DeckAppearance,
    shuffleEnabled: Boolean,
    onToggleShuffle: () -> Unit,
    onBack: () -> Unit
) {
    val practiceCards = remember(cards, shuffleEnabled) {
        if (shuffleEnabled) {
            cards.shuffled()
        } else {
            cards
        }
    }

    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(practiceCards) {
        currentIndex = 0
    }

    Scaffold(
        topBar = {
            AppToolbar(deckName)
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

            if (practiceCards.isEmpty()) {

                Text("This deck has no flashcards.")

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onBack) {
                    Text("Back to decks")
                }

            } else {

                val safeIndex = currentIndex.coerceIn(
                    0,
                    practiceCards.lastIndex
                )

                val currentCard = practiceCards[safeIndex]

                Text(
                    text = "${safeIndex + 1} / ${practiceCards.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(30.dp))

                FlashcardView(
                    question = currentCard.question,
                    answer = currentCard.answer,
                    appearance = appearance
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedButton(
                        onClick = {
                            currentIndex =
                                if (safeIndex == 0) {
                                    practiceCards.lastIndex
                                } else {
                                    safeIndex - 1
                                }
                        }
                    ) {
                        Text("Previous")
                    }

                    OutlinedButton(
                        onClick = {
                            currentIndex =
                                (safeIndex + 1) % practiceCards.size
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
                            onToggleShuffle()
                        }
                    )
                }

                TextButton(onClick = onBack) {
                    Text("Back to decks")
                }
            }
        }
    }
}