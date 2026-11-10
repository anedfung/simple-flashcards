
package com.example.simpleflashcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.simpleflashcards.data.CsvFlashcard
import com.example.simpleflashcards.data.Deck
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.data.FlashcardRepository

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

    // Deck operations

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

    // Flashcard operations

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

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    // Practice settings

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun setShuffleEnabled(enabled: Boolean) {
        _shuffleEnabled.value = enabled
    }
}
