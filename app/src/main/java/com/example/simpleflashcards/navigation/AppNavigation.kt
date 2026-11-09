package com.example.simpleflashcards.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import com.example.simpleflashcards.data.FlashcardBackupManager

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.DeckAppearancePreferences
import com.example.simpleflashcards.data.Flashcard

import com.example.simpleflashcards.ui.screens.*
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.DeckAppearance

import com.example.simpleflashcards.viewmodel.FlashcardViewModel

private enum class AppScreen {
    HOME,
    PRACTICE,
    MANAGE,
    EDIT,
    SETTINGS,
    DECK_APPEARANCE,

    BACKUP_RESTORE
}

@Composable
fun AppNavigation(
    viewModel: FlashcardViewModel,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    appearancePreferences: DeckAppearancePreferences,
    backupManager: FlashcardBackupManager
) {
    val decks by viewModel.decks.collectAsState()
    val cards by viewModel.cards.collectAsState()

    val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()

    val scope = rememberCoroutineScope()

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

    val appearanceFlow = remember(selectedDeck?.id) {
        selectedDeck?.let {
            appearancePreferences.getAppearance(it.id)
        } ?: flowOf(DeckAppearance())
    }

    val selectedAppearance by appearanceFlow.collectAsState(
        initial = DeckAppearance()
    )

    val context = LocalContext.current

    var backupStatus by remember {
        mutableStateOf<String?>(null)
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
                    appearance = selectedAppearance,
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
                        viewModel.addImportedCards(
                            deck.id,
                            importedCards
                        )
                    },

                    onCustomize = {
                        currentScreen = AppScreen.DECK_APPEARANCE
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
                },
                onBackupRestore = {
                    backupStatus = null
                    currentScreen = AppScreen.BACKUP_RESTORE
                }
            )
        }

        AppScreen.DECK_APPEARANCE -> {
            selectedDeck?.let { deck ->
                DeckAppearanceScreen(
                    deckName = deck.name,
                    appearance = selectedAppearance,

                    onQuestionColorChange = { color ->
                        scope.launch {
                            appearancePreferences.setQuestionColor(
                                deck.id,
                                color
                            )
                        }
                    },

                    onAnswerColorChange = { color ->
                        scope.launch {
                            appearancePreferences.setAnswerColor(
                                deck.id,
                                color
                            )
                        }
                    },

                    onFontChange = { font ->
                        scope.launch {
                            appearancePreferences.setFont(
                                deck.id,
                                font
                            )
                        }
                    },

                    onBack = {
                        currentScreen = AppScreen.MANAGE
                    }
                )
            }
        }

        AppScreen.BACKUP_RESTORE -> {
            BackupRestoreScreen(
                statusMessage = backupStatus,

                onExport = { uri ->
                    scope.launch {
                        backupStatus = try {
                            backupManager.exportBackup(
                                context.contentResolver,
                                uri
                            )

                            "Backup exported successfully."
                        } catch (e: Exception) {
                            "Export failed: ${e.message ?: "Unknown error"}"
                        }
                    }
                },

                onImport = { uri ->
                    scope.launch {
                        backupStatus = try {
                            val result = backupManager.importBackup(
                                context.contentResolver,
                                uri
                            )

                            "Restored ${result.first} decks and ${result.second} flashcards."
                        } catch (e: Exception) {
                            "Restore failed: ${e.message ?: "Unknown error"}"
                        }
                    }
                },

                onBack = {
                    currentScreen = AppScreen.SETTINGS
                }
            )
        }
    }
}