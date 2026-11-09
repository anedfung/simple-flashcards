package com.example.simpleflashcards.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "flashcards",

    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["deckId"])
    ]
)
data class Flashcard(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val question: String,

    val answer: String,

    val deckId: Int

)