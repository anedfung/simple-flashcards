package com.example.simpleflashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.migration.Migration
import androidx.room.withTransaction
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

    suspend fun replaceAllData(
        decks: List<Deck>,
        cards: List<Flashcard>
    ) {
        withTransaction {
            flashcardDao().deleteAll()
            deckDao().deleteAll()

            // Parent decks must exist before their children are inserted.
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

    /**
     * Creates a default child deck and moves the parent's direct cards into it.
     * Both operations succeed or neither operation is applied.
     */
    suspend fun createSubdeckAndMoveCards(
        parentDeckId: Int,
        name: String = "New subdeck"
    ): Long = withTransaction {

        val children = deckDao().getChildrenNow(parentDeckId)

        check(children.isEmpty()) {
            "This deck already contains subdecks."
        }

        val newDeckId = deckDao().insert(
            Deck(
                name = name,
                parentDeckId = parentDeckId
            )
        )

        flashcardDao().moveCardsToDeck(
            oldDeckId = parentDeckId,
            newDeckId = newDeckId.toInt()
        )

        newDeckId
    }

    suspend fun createSubdeck(
        parentDeckId: Int,
        name: String = "New subdeck"
    ): Int {
        return withTransaction {
            val parent = deckDao().getAllNow()
                .firstOrNull { it.id == parentDeckId }
                ?: error("Parent deck not found")

            val hasCards = flashcardDao().getAllNow()
                .any { it.deckId == parentDeckId }

            val hasChildren = deckDao()
                .getChildrenNow(parentDeckId)
                .isNotEmpty()

//            check(!hasCards && !hasChildren) {
//                "This deck cannot contain both flashcards and subdecks."
//            }

            // Get existing sibling deck names.
            val existingNames = deckDao()
                .getChildrenNow(parentDeckId)
                .map { it.name }
                .toSet()

            // Find the first available name.
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
    }

    suspend fun addCardToDeck(card: Flashcard) {
        withTransaction {
            val hasChildren = deckDao()
                .getChildrenNow(card.deckId)
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
            val hasChildren = deckDao()
                .getChildrenNow(deckId)
                .isNotEmpty()

            check(!hasChildren) {
                "Cannot import flashcards into a deck containing subdecks."
            }

            flashcardDao().insertAll(cards)
        }
    }

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