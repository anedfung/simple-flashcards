
package com.example.simpleflashcards.data

class FlashcardRepository(
    private val database: FlashcardDatabase
) {

    private val flashcardDao = database.flashcardDao()
    private val deckDao = database.deckDao()

    // ---------------------------------------------------------
    // DECK OPERATIONS
    // ---------------------------------------------------------

    val decks = deckDao.getAll()

    suspend fun addDeck(deck: Deck): Long {
        return deckDao.insert(deck)
    }

    suspend fun createSubdeck(
        parentDeckId: Int,
        moveCards: Boolean
    ): Int {
        return if (moveCards) {
            database.createSubdeckAndMoveCards(
                parentDeckId = parentDeckId,
                name = "New subdeck"
            ).toInt()
        } else {
            database.createSubdeck(
                parentDeckId = parentDeckId,
                name = "New subdeck"
            )
        }
    }

    suspend fun updateDeck(deck: Deck) {
        deckDao.update(deck)
    }

    suspend fun deleteDeck(deck: Deck) {
        deckDao.delete(deck)
    }

    suspend fun moveDeck(
        deckId: Int,
        destinationDeckId: Int?,
        moveExistingCardsToSubdeck: Boolean
    ) {
        database.moveDeck(
            deckId = deckId,
            destinationDeckId = destinationDeckId,
            moveExistingCardsToSubdeck = moveExistingCardsToSubdeck
        )
    }

    // ---------------------------------------------------------
    // FLASHCARD OPERATIONS
    // ---------------------------------------------------------

    val cards = flashcardDao.getAll()

    fun getCardsByDeck(deckId: Int) =
        flashcardDao.getByDeck(deckId)

    suspend fun addCard(card: Flashcard) {
        database.addCardToDeck(card)
    }

    suspend fun addImportedCards(
        deckId: Int,
        importedCards: List<CsvFlashcard>
    ) {
        val cards = importedCards.map { imported ->
            Flashcard(
                question = imported.question,
                answer = imported.answer,
                deckId = deckId
            )
        }

        database.addImportedCardsToDeck(deckId, cards)
    }

    suspend fun updateCard(card: Flashcard) {
        database.updateCard(card)
    }

    suspend fun moveCards(
        cardIds: List<Int>,
        destinationDeckId: Int
    ) {
        database.moveCards(
            cardIds = cardIds,
            destinationDeckId = destinationDeckId
        )
    }

    suspend fun deleteCard(card: Flashcard) {
        flashcardDao.delete(card)
    }
}
