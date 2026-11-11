
package com.example.simpleflashcards.navigation

import android.net.Uri

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.DeckAppearancePreferences
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.data.FlashcardBackupManager
import com.example.simpleflashcards.data.PracticeSession
import com.example.simpleflashcards.data.PracticeSessionPreferences
import com.example.simpleflashcards.data.PracticeSessionSnapshot

import com.example.simpleflashcards.ui.screens.*
import com.example.simpleflashcards.ui.theme.AppThemeMode
import com.example.simpleflashcards.ui.theme.DeckAppearance
import com.example.simpleflashcards.ui.theme.AppColorTheme
import com.example.simpleflashcards.ui.theme.FontSizeOption

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

private data class NavigationEntry(
    val screen: AppScreen,
    val deck: Deck?
)

@Composable
fun AppNavigation(
    viewModel: FlashcardViewModel,
    themeMode: AppThemeMode,
    colorTheme: AppColorTheme,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onColorThemeChange: (AppColorTheme) -> Unit,
    appearancePreferences: DeckAppearancePreferences,
    backupManager: FlashcardBackupManager,
    practiceSessionPreferences: PracticeSessionPreferences,
    appFontScale: Float,
    flashcardFontScale: Float,
    customFlashcardFontSizeEnabled: Boolean,
    onAppFontScaleChange: (Float) -> Unit,
    onFlashcardFontScaleChange: (Float) -> Unit,
    onCustomFlashcardFontSizeEnabledChange: (Boolean) -> Unit,
    appLanguage: String,
    onAppLanguageChange: (String) -> Unit,
) {

    val decks by viewModel.decks.collectAsState()
    val cards by viewModel.cards.collectAsState()

    val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()

    val savedSessionSnapshot by
    practiceSessionPreferences.session.collectAsState(
        initial = PracticeSessionSnapshot(
            loaded = false,
            session = null
        )
    )

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var currentScreen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    var selectedDeck by remember {
        mutableStateOf<Deck?>(null)
    }

    var navigationBackStack by remember {
        mutableStateOf<List<NavigationEntry>>(emptyList())
    }

    var editingCard by remember {
        mutableStateOf<Flashcard?>(null)
    }

    var restoredSession by remember {
        mutableStateOf<PracticeSession?>(null)
    }

    var restorationHandled by remember {
        mutableStateOf(false)
    }

    var backupStatus by remember {
        mutableStateOf<String?>(null)
    }

    var newlyCreatedSubdeckId by remember {
        mutableStateOf<Int?>(null)
    }

    val newlyCreatedSubdeck = decks.firstOrNull {
        it.id == newlyCreatedSubdeckId
    }

    var lastBackPressedTime by remember {
        mutableLongStateOf(0L)
    }

    fun navigateTo(
        screen: AppScreen,
        deck: Deck? = selectedDeck
    ) {
        navigationBackStack = navigationBackStack + NavigationEntry(
            screen = currentScreen,
            deck = selectedDeck
        )

        selectedDeck = deck
        currentScreen = screen
    }

    fun handleBack() {

        // Clear the restored practice session when leaving practice.
        if (currentScreen == AppScreen.PRACTICE) {
            restoredSession = null
        }

        // Restore the actual previous screen and deck.
        if (navigationBackStack.isNotEmpty()) {

            val previous = navigationBackStack.last()

            navigationBackStack = navigationBackStack.dropLast(1)

            selectedDeck = previous.deck
            currentScreen = previous.screen

            return
        }

        // Fallback for screens opened without navigation history.
        when (currentScreen) {

            AppScreen.HOME -> Unit

            AppScreen.PRACTICE -> {
                currentScreen = AppScreen.HOME
            }

            AppScreen.MANAGE -> {
                val deck = selectedDeck

                if (deck?.parentDeckId != null) {
                    selectedDeck = decks.firstOrNull {
                        it.id == deck.parentDeckId
                    }
                } else {
                    selectedDeck = null
                    currentScreen = AppScreen.HOME
                }
            }

            AppScreen.EDIT -> {
                currentScreen = AppScreen.MANAGE
            }

            AppScreen.SETTINGS -> {
                currentScreen = AppScreen.HOME
            }

            AppScreen.DECK_APPEARANCE -> {
                currentScreen = AppScreen.MANAGE
            }

            AppScreen.BACKUP_RESTORE -> {
                currentScreen = AppScreen.SETTINGS
            }
        }
    }

    // Keep the selected deck synchronized with database updates.
    LaunchedEffect(decks) {
        selectedDeck?.let { selected ->
            selectedDeck = decks.firstOrNull {
                it.id == selected.id
            } ?: selected
        }
    }

    // Restore the last practice session.
    LaunchedEffect(savedSessionSnapshot, decks) {

        if (
            savedSessionSnapshot.loaded &&
            decks.isNotEmpty() &&
            !restorationHandled
        ) {

            restorationHandled = true

            val savedSession = savedSessionSnapshot.session

            val matchingDeck = decks.firstOrNull {
                it.id == savedSession?.deckId
            }

            if (savedSession != null && matchingDeck != null) {

                selectedDeck = matchingDeck
                restoredSession = savedSession

                viewModel.setShuffleEnabled(
                    savedSession.shuffleEnabled
                )

                navigationBackStack = emptyList()

                currentScreen = AppScreen.PRACTICE
            }
        }
    }

    val selectedDeckIds = selectedDeck?.let { deck ->
        getDescendantDeckIds(deck.id, decks)
    } ?: emptySet()

    val selectedCards = cards.filter {
        it.deckId in selectedDeckIds
    }

    val selectedSubdecks = decks.filter {
        it.parentDeckId == selectedDeck?.id
    }

    val appearanceFlow = remember(selectedDeck?.id) {
        selectedDeck?.let {
            appearancePreferences.getAppearance(it.id)
        } ?: flowOf(DeckAppearance())
    }

    val selectedAppearance by appearanceFlow.collectAsState(
        initial = DeckAppearance()
    )

    BackHandler(
        enabled = currentScreen != AppScreen.HOME
    ) {
        handleBack()
    }

    BackHandler(enabled = currentScreen == AppScreen.HOME) {

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBackPressedTime < 2000L) {

            (context as? Activity)?.finish()

        } else {

            lastBackPressedTime = currentTime

            Toast.makeText(
                context,
                "Press Back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    when (currentScreen) {

        AppScreen.HOME -> {

            HomeScreen(
                decks = decks,
                cards = cards,

                onPractice = { deck ->
                    restoredSession = null
                    navigateTo(AppScreen.PRACTICE, deck)
                },

                onManage = { deck ->
                    navigateTo(AppScreen.MANAGE, deck)
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
                    navigateTo(AppScreen.SETTINGS, null)
                }
            )
        }

        AppScreen.PRACTICE -> {

            selectedDeck?.let { deck ->

                PracticeScreen(
                    deckId = deck.id,
                    deckName = deck.name,
                    cards = selectedCards,
                    appearance = selectedAppearance,

                    initialSession = restoredSession?.takeIf {
                        it.deckId == deck.id
                    },

                    shuffleEnabled = shuffleEnabled,
                    flashcardFontScale = if (customFlashcardFontSizeEnabled) {
                        flashcardFontScale
                    } else {
                        appFontScale
                    },

                    onToggleShuffle = {
                        viewModel.toggleShuffle()
                    },

                    onSessionChange = { session ->
                        scope.launch {
                            practiceSessionPreferences.saveSession(session)
                        }
                    },

                    onBack = {
                        handleBack()
                    },
                )
            }
        }

        AppScreen.MANAGE -> {

            selectedDeck?.let { deck ->

                ManageScreen(
                    deckName = deck.name,
                    deckId = deck.id,
                    cards = selectedCards,
                    subdecks = selectedSubdecks,
                    allDecks = decks,
                    newSubdeckToRename = newlyCreatedSubdeck,

                    onRenamePromptHandled = {
                        newlyCreatedSubdeckId = null
                    },

                    onAdd = {
                        editingCard = null
                        navigateTo(AppScreen.EDIT)
                    },

                    onEdit = { card ->
                        editingCard = card
                        navigateTo(AppScreen.EDIT)
                    },

                    onDelete = { card ->
                        viewModel.deleteCard(card)
                    },

                    onBack = {
                        handleBack()
                    },

                    onOpenSubdeck = { subdeck ->
                        navigateTo(AppScreen.MANAGE, subdeck)
                    },

                    onPracticeSubdeck = { subdeck ->
                        restoredSession = null
                        navigateTo(AppScreen.PRACTICE, subdeck)
                    },

                    onCreateSubdeck = { moveCards ->
                        viewModel.createSubdeck(
                            parentDeckId = deck.id,
                            moveCards = moveCards
                        ) { newDeckId ->

                            // Keep the current parent selected.
                            // The new ID triggers the rename dialog once
                            // the updated deck list arrives from the database.
                            newlyCreatedSubdeckId = newDeckId
                        }
                    },

                    onRenameSubdeck = { subdeck, newName ->
                        viewModel.renameDeck(subdeck, newName)
                    },

                    onDeleteSubdeck = { subdeck ->
                        viewModel.deleteDeck(subdeck)
                    },

                    onImportCards = { importedCards ->
                        viewModel.addImportedCards(
                            deck.id,
                            importedCards
                        )
                    },

                    onCustomize = {
                        navigateTo(AppScreen.DECK_APPEARANCE)
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

                        handleBack()
                    },

                    onCancel = {
                        handleBack()
                    }
                )
            }
        }

        AppScreen.SETTINGS -> {

            SettingsScreen(
                themeMode = themeMode,
                colorTheme = colorTheme,

                appFontScale = appFontScale,
                flashcardFontScale = flashcardFontScale,
                customFlashcardFontSizeEnabled = customFlashcardFontSizeEnabled,
                appLanguage = appLanguage,
                onAppLanguageChange = onAppLanguageChange,

                onThemeModeChange = onThemeModeChange,
                onColorThemeChange = onColorThemeChange,

                onAppFontScaleChange = onAppFontScaleChange,
                onFlashcardFontScaleChange = onFlashcardFontScaleChange,
                onCustomFlashcardFontSizeEnabledChange =
                    onCustomFlashcardFontSizeEnabledChange,

                onBack = {
                    handleBack()
                },

                onBackupRestore = {
                    backupStatus = null
                    navigateTo(AppScreen.BACKUP_RESTORE)
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
                        handleBack()
                    }
                )
            }
        }

        AppScreen.BACKUP_RESTORE -> {

            BackupRestoreScreen(
                statusMessage = backupStatus,

                onExport = { uri: Uri ->
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
                    handleBack()
                }
            )
        }
    }
}

private fun getDescendantDeckIds(
    rootDeckId: Int,
    decks: List<Deck>
): Set<Int> {

    val result = mutableSetOf<Int>()
    val pending = ArrayDeque<Int>()

    pending.add(rootDeckId)

    while (pending.isNotEmpty()) {

        val currentId = pending.removeFirst()

        if (!result.add(currentId)) {
            continue
        }

        decks
            .filter { it.parentDeckId == currentId }
            .forEach { child ->
                pending.add(child.id)
            }
    }

    return result
}
