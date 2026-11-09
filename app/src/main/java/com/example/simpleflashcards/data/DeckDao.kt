package com.example.simpleflashcards.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Query("SELECT * FROM decks ORDER BY createdAt ASC")
    fun getAll(): Flow<List<Deck>>

    @Query("SELECT * FROM decks ORDER BY createdAt ASC")
    suspend fun getAllNow(): List<Deck>

    @Insert
    suspend fun insert(deck: Deck): Long

    @Insert
    suspend fun insertAll(decks: List<Deck>)

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("DELETE FROM decks")
    suspend fun deleteAll()
}