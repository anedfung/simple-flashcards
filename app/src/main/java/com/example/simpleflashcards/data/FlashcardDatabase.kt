
package com.example.simpleflashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Flashcard::class,
        Deck::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FlashcardDatabase : RoomDatabase() {

    abstract fun flashcardDao(): FlashcardDao

    abstract fun deckDao(): DeckDao

    // ---------------------------------------------------------
    // BACKUP / RESTORE
    // ---------------------------------------------------------

    suspend fun replaceAllData(
        decks: List<Deck>,
        cards: List<Flashcard>
    ) {
        withTransaction {
            flashcardDao().deleteAll()
            deckDao().deleteAll()

            // Parent decks must exist before their children.
            val remaining = decks.toMutableList()
            val insertedIds = mutableSetOf<Int>()

            while (remaining.isNotEmpty()) {
                val ready = remaining.filter { deck ->
                    deck.parentDeckId == null ||
                            deck.parentDeckId in insertedIds
                }

                check(ready.isNotEmpty()) {
                    "The deck hierarchy contains an invalid parent reference or cycle."
                }

                deckDao().insertAll(ready)

                insertedIds.addAll(ready.map { it.id })
                remaining.removeAll(ready.toSet())
            }

            flashcardDao().insertAll(cards)
        }
    }

    // ---------------------------------------------------------
    // CREATE SUBDECKS
    // ---------------------------------------------------------

    /**
     * Creates a subdeck and moves all direct flashcards from
     * the parent into that new subdeck.
     *
     * Both operations succeed or neither is applied.
     */
    suspend fun createSubdeckAndMoveCards(
        parentDeckId: Int,
        name: String = "New subdeck"
    ): Long = withTransaction {

        val parent = deckDao().getById(parentDeckId)
            ?: error("Parent deck not found.")

        val children = deckDao().getChildrenNow(parentDeckId)

        check(children.isEmpty()) {
            "This deck already contains subdecks."
        }

        val newDeckId = deckDao().insert(
            Deck(
                name = name,
                parentDeckId = parent.id
            )
        )

        flashcardDao().moveCardsToDeck(
            oldDeckId = parentDeckId,
            newDeckId = newDeckId.toInt()
        )

        newDeckId
    }

    /**
     * Creates an empty subdeck.
     *
     * A deck may contain multiple subdecks, but it cannot
     * contain both direct flashcards and subdecks.
     */
    suspend fun createSubdeck(
        parentDeckId: Int,
        name: String = "New subdeck"
    ): Int = withTransaction {

        val parent = deckDao().getById(parentDeckId)
            ?: error("Parent deck not found.")

        val hasCards = flashcardDao().getAllNow()
            .any { it.deckId == parentDeckId }

        check(!hasCards) {
            "This deck contains flashcards. Move them into a subdeck first."
        }

        val existingNames = deckDao()
            .getChildrenNow(parentDeckId)
            .map { it.name }
            .toSet()

        var finalName = name
        var counter = 1

        while (finalName in existingNames) {
            finalName = "$name $counter"
            counter++
        }

        deckDao().insert(
            Deck(
                name = finalName,
                parentDeckId = parent.id
            )
        ).toInt()
    }

    // ---------------------------------------------------------
    // ADD / IMPORT FLASHCARDS
    // ---------------------------------------------------------

    suspend fun addCardToDeck(card: Flashcard) {
        withTransaction {
            val destination = deckDao().getById(card.deckId)
                ?: error("Destination deck not found.")

            val hasChildren = deckDao()
                .getChildrenNow(destination.id)
                .isNotEmpty()

            check(!hasChildren) {
                "Cannot add flashcards to a deck containing subdecks."
            }

            flashcardDao().insert(card)
        }
    }

    suspend fun addImportedCardsToDeck(
        deckId: Int,
        cards: List<Flashcard>
    ) {
        if (cards.isEmpty()) return

        withTransaction {
            val destination = deckDao().getById(deckId)
                ?: error("Destination deck not found.")

            val hasChildren = deckDao()
                .getChildrenNow(destination.id)
                .isNotEmpty()

            check(!hasChildren) {
                "Cannot import flashcards into a deck containing subdecks."
            }

            check(cards.all { it.deckId == deckId }) {
                "Imported flashcards contain an invalid destination."
            }

            flashcardDao().insertAll(cards)
        }
    }

    // ---------------------------------------------------------
    // UPDATE FLASHCARD
    // ---------------------------------------------------------

    /**
     * Updating a flashcard can also change its deckId.
     * Therefore, destination validation must happen here.
     */
    suspend fun updateCard(card: Flashcard) {
        withTransaction {
            val existingCard = flashcardDao().getAllNow()
                .firstOrNull { it.id == card.id }
                ?: error("Flashcard not found.")

            if (existingCard.deckId != card.deckId) {
                val destination = deckDao().getById(card.deckId)
                    ?: error("Destination deck not found.")

                val hasChildren = deckDao()
                    .getChildrenNow(destination.id)
                    .isNotEmpty()

                check(!hasChildren) {
                    "Cannot move flashcards into a deck containing subdecks."
                }
            }

            flashcardDao().update(card)
        }
    }

    // ---------------------------------------------------------
    // MOVE SELECTED FLASHCARDS
    // ---------------------------------------------------------

    /**
     * Moves selected flashcards into another deck.
     *
     * Special case:
     * If the destination is the source deck's parent, the
     * source must be its only child, and every flashcard
     * in the source must be selected.
     *
     * In that case, the source deck is deleted after moving
     * its cards into the parent.
     */
    suspend fun moveCards(
        cardIds: List<Int>,
        destinationDeckId: Int
    ) {
        require(cardIds.isNotEmpty()) {
            "No flashcards were selected."
        }

        withTransaction {
            val destination = deckDao().getById(destinationDeckId)
                ?: error("Destination deck not found.")

            val allCards = flashcardDao().getAllNow()

            val selectedIds = cardIds.distinct()
            val selectedCards = allCards.filter {
                it.id in selectedIds
            }

            check(selectedCards.size == selectedIds.size) {
                "One or more selected flashcards could not be found."
            }

            val destinationChildren = deckDao()
                .getChildrenNow(destinationDeckId)

            val destinationCards = allCards.filter {
                it.deckId == destinationDeckId
            }

            if (destinationChildren.isNotEmpty()) {

                // The only permitted exception is moving all
                // cards from the destination's sole child.
                check(destinationCards.isEmpty()) {
                    "The destination deck has an invalid content structure."
                }

                check(destinationChildren.size == 1) {
                    "This deck contains multiple subdecks."
                }

                val sourceDeck = destinationChildren.first()

                val sourceChildren = deckDao()
                    .getChildrenNow(sourceDeck.id)

                check(sourceChildren.isEmpty()) {
                    "The source deck contains subdecks."
                }

                val sourceCards = allCards.filter {
                    it.deckId == sourceDeck.id
                }

                check(sourceCards.isNotEmpty()) {
                    "The source deck contains no flashcards."
                }

                check(selectedCards.all {
                    it.deckId == sourceDeck.id
                }) {
                    "Only flashcards from the current subdeck can be moved here."
                }

                check(
                    selectedCards.map { it.id }.toSet() ==
                            sourceCards.map { it.id }.toSet()
                ) {
                    "All flashcards in the subdeck must be selected to move them into its parent."
                }

                flashcardDao().moveSelectedCards(
                    cardIds = selectedIds,
                    destinationDeckId = destinationDeckId
                )

                // Delete the now-empty source deck.
                deckDao().delete(sourceDeck)

            } else {

                // A deck without children can accept flashcards,
                // including when it already contains other cards.
                flashcardDao().moveSelectedCards(
                    cardIds = selectedIds,
                    destinationDeckId = destinationDeckId
                )
            }
        }
    }

    // ---------------------------------------------------------
    // MOVE DECK
    // ---------------------------------------------------------

    /**
     * Moves a deck into another deck.
     *
     * If the destination currently contains flashcards,
     * moveExistingCardsToSubdeck must be true.
     *
     * Those flashcards are moved into a new subdeck first,
     * allowing the destination to become a parent deck.
     *
     * Existing descendants of the moved deck are preserved.
     */
    suspend fun moveDeck(
        deckId: Int,
        destinationDeckId: Int?,
        moveExistingCardsToSubdeck: Boolean
    ) {
        withTransaction {
            val movingDeck = deckDao().getById(deckId)
                ?: error("Deck to move was not found.")

            check(deckId != destinationDeckId) {
                "A deck cannot be moved into itself."
            }

            val allDecks = deckDao().getAllNow()
            val deckMap = allDecks.associateBy { it.id }

            // Moving to the root does not require a destination deck.
            if (destinationDeckId == null) {
                deckDao().update(
                    movingDeck.copy(parentDeckId = null)
                )
                return@withTransaction
            }

            val destination = deckMap[destinationDeckId]
                ?: error("Destination deck not found.")

            // Prevent moving a deck into itself or one of its descendants.
            var currentParentId: Int? = destination.id

            while (currentParentId != null) {
                check(currentParentId != deckId) {
                    "A deck cannot be moved into one of its descendants."
                }

                currentParentId = deckMap[currentParentId]?.parentDeckId
            }

            // No operation is needed if it is already in this location.
            if (movingDeck.parentDeckId == destinationDeckId) {
                return@withTransaction
            }

            val allCards = flashcardDao().getAllNow()

            val movingDeckCards = allCards.filter {
                it.deckId == movingDeck.id
            }

            val destinationCards = allCards.filter {
                it.deckId == destinationDeckId
            }

            val destinationChildren = deckDao()
                .getChildrenNow(destinationDeckId)

            check(destinationCards.isEmpty() || destinationChildren.isEmpty()) {
                "The destination deck has an invalid content structure."
            }

            // A destination containing subdecks cannot accept a deck
            // that directly contains flashcards.
            if (destinationChildren.isNotEmpty()) {
                check(movingDeckCards.isEmpty()) {
                    "A deck containing flashcards cannot be moved into a deck containing subdecks."
                }
            }

            if (destinationCards.isNotEmpty()) {

                check(moveExistingCardsToSubdeck) {
                    "Confirmation is required before moving the destination's flashcards into a new subdeck."
                }

                // Generate a unique name for the new subdeck.
                val baseName = "${destination.name} - Cards"

                val existingNames = destinationChildren
                    .map { it.name }
                    .toSet()

                var finalName = baseName
                var counter = 1

                while (finalName in existingNames) {
                    finalName = "$baseName $counter"
                    counter++
                }

                // Create the new deck temporarily at the root.
                val cardsDeckId = deckDao().insert(
                    Deck(name = finalName)
                ).toInt()

                // Move the destination's cards into it.
                flashcardDao().moveCardsToDeck(
                    oldDeckId = destinationDeckId,
                    newDeckId = cardsDeckId
                )

                // Attach the new subdeck to the destination.
                val cardsDeck = deckDao().getById(cardsDeckId)
                    ?: error("Could not create the flashcard subdeck.")

                deckDao().update(
                    cardsDeck.copy(parentDeckId = destinationDeckId)
                )
            }

            // Finally, attach the requested deck.
            deckDao().update(
                movingDeck.copy(parentDeckId = destinationDeckId)
            )
        }
    }

    // ---------------------------------------------------------
    // DATABASE MIGRATIONS
    // ---------------------------------------------------------

    companion object {

        @Volatile
        private var INSTANCE: FlashcardDatabase? = null

        // Version 1 -> Version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS decks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO decks (id, name, createdAt)
                    VALUES (1, 'My Flashcards', 0)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE flashcards_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        question TEXT NOT NULL,
                        answer TEXT NOT NULL,
                        deckId INTEGER NOT NULL,
                        FOREIGN KEY(deckId)
                            REFERENCES decks(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO flashcards_new
                        (id, question, answer, deckId)
                    SELECT id, question, answer, 1
                    FROM flashcards
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE flashcards")

                db.execSQL(
                    "ALTER TABLE flashcards_new RENAME TO flashcards"
                )

                db.execSQL(
                    """
                    CREATE INDEX index_flashcards_deckId
                    ON flashcards(deckId)
                    """.trimIndent()
                )
            }
        }

        // Version 2 -> Version 3: add parentDeckId.
        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    ALTER TABLE decks
                    ADD COLUMN parentDeckId INTEGER
                    REFERENCES decks(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX index_decks_parentDeckId
                    ON decks(parentDeckId)
                    """.trimIndent()
                )
            }
        }

        private val DATABASE_CALLBACK = object : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL(
                    """
                    INSERT INTO decks (name, createdAt, parentDeckId)
                    VALUES ('My Flashcards', 0, NULL)
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): FlashcardDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardDatabase::class.java,
                    "flashcards_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .addCallback(DATABASE_CALLBACK)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
