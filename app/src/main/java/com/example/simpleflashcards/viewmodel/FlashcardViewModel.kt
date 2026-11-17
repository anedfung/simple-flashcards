
package com.example.simpleflashcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.data.FlashcardRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    val decks = repository.decks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val cards = repository.cards.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _shuffleEnabled = MutableStateFlow(false)

    val shuffleEnabled = _shuffleEnabled.asStateFlow()

    // ---------------------------------------------------------
    // DECK OPERATIONS
    // ---------------------------------------------------------

    fun addDeck(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            repository.addDeck(
                Deck(name = name.trim())
            )
        }
    }

    fun createSubdeck(
        parentDeckId: Int,
        moveCards: Boolean,
        onCreated: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val newDeckId = repository.createSubdeck(
                parentDeckId = parentDeckId,
                moveCards = moveCards
            )

            onCreated(newDeckId)
        }
    }

    fun renameDeck(deck: Deck, newName: String) {
        if (newName.isBlank()) return

        viewModelScope.launch {
            repository.updateDeck(
                deck.copy(name = newName.trim())
            )
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            val rootDeckCount = decks.value.count {
                it.parentDeckId == null
            }

            // Always retain at least one root deck.
            if (deck.parentDeckId == null && rootDeckCount <= 1) {
                return@launch
            }

            repository.deleteDeck(deck)
        }
    }

    /**
     * Moves a deck into another deck.
     *
     * Set moveExistingCardsToSubdeck to true only after
     * the user has confirmed the conversion.
     */
    fun moveDeck(
        deckId: Int,
        destinationDeckId: Int?,
        moveExistingCardsToSubdeck: Boolean = false,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.moveDeck(
                    deckId = deckId,
                    destinationDeckId = destinationDeckId,
                    moveExistingCardsToSubdeck = moveExistingCardsToSubdeck
                )

                onResult(Result.success(Unit))

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                onResult(Result.failure(exception))
            }
        }
    }

    // ---------------------------------------------------------
    // FLASHCARD OPERATIONS
    // ---------------------------------------------------------

    fun addCard(
        question: String,
        answer: String,
        deckId: Int
    ) {
        if (question.isBlank() || answer.isBlank()) return

        viewModelScope.launch {
            repository.addCard(
                Flashcard(
                    question = question.trim(),
                    answer = answer.trim(),
                    deckId = deckId
                )
            )
        }
    }

    fun addImportedCards(
        deckId: Int,
        importedCards: List<CsvFlashcard>
    ) {
        viewModelScope.launch {
            repository.addImportedCards(
                deckId,
                importedCards
            )
        }
    }

    fun updateCard(card: Flashcard) {
        if (card.question.isBlank() || card.answer.isBlank()) return

        viewModelScope.launch {
            repository.updateCard(
                card.copy(
                    question = card.question.trim(),
                    answer = card.answer.trim()
                )
            )
        }
    }

    /**
     * Moves selected flashcards into another deck.
     *
     * The database handles the special parent-deck case,
     * including deleting the source deck when appropriate.
     */
    fun moveCards(
        cardIds: List<Int>,
        destinationDeckId: Int,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.moveCards(
                    cardIds = cardIds,
                    destinationDeckId = destinationDeckId
                )

                onResult(Result.success(Unit))

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                onResult(Result.failure(exception))
            }
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun setCardsEnabled(cardIds: List<Int>, enabled: Boolean) {
        if (cardIds.isEmpty()) return

        viewModelScope.launch {
            repository.setCardsEnabled(cardIds, enabled)
        }
    }

    // ---------------------------------------------------------
    // PRACTICE SETTINGS
    // ---------------------------------------------------------

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun setShuffleEnabled(enabled: Boolean) {
        _shuffleEnabled.value = enabled
    }
}
