package com.example.simpleflashcards.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "decks",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["parentDeckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parentDeckId"])
    ]
)
data class Deck(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val createdAt: Long = System.currentTimeMillis(),

    val parentDeckId: Int? = null
)