package com.example.simpleflashcards.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard

import com.example.simpleflashcards.ui.screens.*

import com.example.simpleflashcards.viewmodel.FlashcardViewModel

private enum class AppScreen {

    HOME,
    PRACTICE,
    MANAGE,
    EDIT

}

@Composable
fun AppNavigation(
    viewModel: FlashcardViewModel
) {

    // Application data
    val decks by viewModel.decks.collectAsState()

    val cards by viewModel.cards.collectAsState()

    val shuffleEnabled by
    viewModel.shuffleEnabled.collectAsState()

    // Navigation state
    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    // Selected deck
    var selectedDeck by remember {
        mutableStateOf<Deck?>(null)
    }

    // Selected card for editing
    var editingCard by remember {
        mutableStateOf<Flashcard?>(null)
    }

    // Cards belonging to the selected deck
    val selectedCards = cards.filter {
        it.deckId == selectedDeck?.id
    }

    when (currentScreen) {

        // HOME
        AppScreen.HOME -> {

            HomeScreen(

                decks = decks,

                cards = cards,

                onPractice = { deck ->

                    selectedDeck = deck

                    currentScreen = AppScreen.PRACTICE
                },

                onManage = { deck ->

                    selectedDeck = deck

                    currentScreen = AppScreen.MANAGE
                },

                onAddDeck = { name ->

                    viewModel.addDeck(name)
                },

                onRenameDeck = { deck, newName ->

                    viewModel.renameDeck(deck, newName)
                },

                onDeleteDeck = { deck ->

                    viewModel.deleteDeck(deck)
                }
            )
        }

        // PRACTICE
        AppScreen.PRACTICE -> {

            selectedDeck?.let { deck ->

                PracticeScreen(

                    deckName = deck.name,

                    cards = selectedCards,

                    shuffleEnabled = shuffleEnabled,

                    onToggleShuffle = {
                        viewModel.toggleShuffle()
                    },

                    onBack = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }
        }

        // MANAGE
        AppScreen.MANAGE -> {

            selectedDeck?.let { deck ->

                ManageScreen(

                    deckName = deck.name,

                    cards = selectedCards,

                    onAdd = {

                        editingCard = null

                        currentScreen = AppScreen.EDIT
                    },

                    onEdit = { card ->

                        editingCard = card

                        currentScreen = AppScreen.EDIT
                    },

                    onDelete = { card ->

                        viewModel.deleteCard(card)
                    },

                    onBack = {

                        currentScreen = AppScreen.HOME
                    },

                    onImportCards = { importedCards ->
                        selectedDeck?.let { deck ->
                            viewModel.addImportedCards(
                                deck.id,
                                importedCards
                            )
                        }
                    }
                )
            }
        }

        // EDIT
        AppScreen.EDIT -> {

            selectedDeck?.let { deck ->

                EditCardScreen(

                    deckName = deck.name,

                    deckId = deck.id,

                    card = editingCard,

                    onSave = { question, answer ->

                        if (editingCard == null) {

                            viewModel.addCard(
                                question = question,
                                answer = answer,
                                deckId = deck.id
                            )

                        } else {

                            viewModel.updateCard(
                                editingCard!!.copy(
                                    question = question,
                                    answer = answer
                                )
                            )
                        }

                        currentScreen = AppScreen.MANAGE
                    },

                    onCancel = {

                        currentScreen = AppScreen.MANAGE
                    }
                )
            }
        }
    }
}