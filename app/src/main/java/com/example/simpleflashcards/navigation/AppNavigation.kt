package com.example.simpleflashcards.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard

import com.example.simpleflashcards.ui.screens.*
import com.example.simpleflashcards.ui.theme.AppThemeMode

import com.example.simpleflashcards.viewmodel.FlashcardViewModel

private enum class AppScreen {

    HOME,
    PRACTICE,
    MANAGE,
    EDIT,
    SETTINGS
}

@Composable
fun AppNavigation(
    viewModel: FlashcardViewModel,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit
) {

    val decks by viewModel.decks.collectAsState()
    val cards by viewModel.cards.collectAsState()

    val shuffleEnabled by
    viewModel.shuffleEnabled.collectAsState()

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var selectedDeck by remember {
        mutableStateOf<Deck?>(null)
    }

    var editingCard by remember {
        mutableStateOf<Flashcard?>(null)
    }

    val selectedCards = cards.filter {
        it.deckId == selectedDeck?.id
    }

    when (currentScreen) {

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
                },

                onSettings = {
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }

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

                        selectedDeck?.let { selected ->

                            viewModel.addImportedCards(
                                selected.id,
                                importedCards
                            )
                        }
                    }
                )
            }
        }

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

        AppScreen.SETTINGS -> {

            SettingsScreen(

                themeMode = themeMode,

                onThemeModeChange = onThemeModeChange,

                onBack = {
                    currentScreen = AppScreen.HOME
                }
            )
        }
    }
}