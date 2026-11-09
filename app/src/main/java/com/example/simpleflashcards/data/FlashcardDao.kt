package com.example.simpleflashcards.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAll(): Flow<List<Flashcard>>

    @Query(
        "SELECT * FROM flashcards " +
                "WHERE deckId = :deckId ORDER BY id ASC"
    )
    fun getByDeck(deckId: Int): Flow<List<Flashcard>>

    @Insert
    suspend fun insert(card: Flashcard)

    @Update
    suspend fun update(card: Flashcard)

    @Delete
    suspend fun delete(card: Flashcard)

}