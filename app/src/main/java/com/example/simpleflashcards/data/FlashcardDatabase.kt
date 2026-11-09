package com.example.simpleflashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Flashcard::class,
        Deck::class
    ],
    version = 2,
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

            deckDao().insertAll(decks)
            flashcardDao().insertAll(cards)
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: FlashcardDatabase? = null

        // Version 1 -> Version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                // Create decks table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS decks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Create default deck
                db.execSQL(
                    """
                    INSERT INTO decks (id, name, createdAt)
                    VALUES (1, 'My Flashcards', 0)
                    """.trimIndent()
                )

                // Create new flashcards table
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

                // Preserve existing flashcards
                db.execSQL(
                    """
                    INSERT INTO flashcards_new
                        (id, question, answer, deckId)
                    SELECT id, question, answer, 1
                    FROM flashcards
                    """.trimIndent()
                )

                // Replace old table
                db.execSQL("DROP TABLE flashcards")

                db.execSQL(
                    "ALTER TABLE flashcards_new RENAME TO flashcards"
                )

                // Create deck index
                db.execSQL(
                    """
                    CREATE INDEX index_flashcards_deckId
                    ON flashcards(deckId)
                    """.trimIndent()
                )
            }
        }

        private val DATABASE_CALLBACK = object : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {

                super.onCreate(db)

                db.execSQL(
                    """
                    INSERT INTO decks (name, createdAt)
                    VALUES ('My Flashcards', 0)
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
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DATABASE_CALLBACK)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}