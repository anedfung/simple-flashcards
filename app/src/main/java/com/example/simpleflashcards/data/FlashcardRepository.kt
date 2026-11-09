package com.example.simpleflashcards.data

class FlashcardRepository(
    private val flashcardDao: FlashcardDao,
    private val deckDao: DeckDao
) {

    // Deck operations

    val decks = deckDao.getAll()

    suspend fun addDeck(deck: Deck): Long {
        return deckDao.insert(deck)
    }

    suspend fun updateDeck(deck: Deck) {
        deckDao.update(deck)
    }

    suspend fun deleteDeck(deck: Deck) {
        deckDao.delete(deck)
    }

    // Flashcard operations

    val cards = flashcardDao.getAll()

    fun getCardsByDeck(deckId: Int) =
        flashcardDao.getByDeck(deckId)

    suspend fun addCard(card: Flashcard) {
        flashcardDao.insert(card)
    }

    suspend fun updateCard(card: Flashcard) {
        flashcardDao.update(card)
    }

    suspend fun deleteCard(card: Flashcard) {
        flashcardDao.delete(card)
    }
}