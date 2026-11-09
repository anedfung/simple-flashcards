package com.example.simpleflashcards.data

import android.content.ContentResolver
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

class FlashcardBackupManager(
    private val database: FlashcardDatabase
) {

    suspend fun exportBackup(
        resolver: ContentResolver,
        uri: Uri
    ) {
        val decks = database.deckDao().getAllNow()
        val cards = database.flashcardDao().getAllNow()

        val root = JSONObject()
        root.put("format", "simple_flashcards_backup")
        root.put("version", 1)

        val deckArray = JSONArray()

        decks.forEach { deck ->
            deckArray.put(
                JSONObject().apply {
                    put("id", deck.id)
                    put("name", deck.name)
                    put("createdAt", deck.createdAt)
                }
            )
        }

        val cardArray = JSONArray()

        cards.forEach { card ->
            cardArray.put(
                JSONObject().apply {
                    put("id", card.id)
                    put("question", card.question)
                    put("answer", card.answer)
                    put("deckId", card.deckId)
                }
            )
        }

        root.put("decks", deckArray)
        root.put("cards", cardArray)

        resolver.openOutputStream(uri)?.bufferedWriter()?.use {
            it.write(root.toString(2))
        } ?: throw IllegalStateException(
            "Could not open the backup file for writing."
        )
    }

    suspend fun importBackup(
        resolver: ContentResolver,
        uri: Uri
    ): Pair<Int, Int> {

        val json = resolver.openInputStream(uri)?.bufferedReader()?.use {
            it.readText()
        } ?: throw IllegalStateException(
            "Could not open the selected backup file."
        )

        val root = JSONObject(json)

        require(
            root.optString("format") == "simple_flashcards_backup"
        ) {
            "This is not a Simple Flashcards backup."
        }

        require(root.optInt("version") == 1) {
            "This backup version is not supported."
        }

        val deckArray = root.getJSONArray("decks")
        val cardArray = root.getJSONArray("cards")

        val decks = mutableListOf<Deck>()
        val cards = mutableListOf<Flashcard>()

        for (i in 0 until deckArray.length()) {
            val item = deckArray.getJSONObject(i)

            decks.add(
                Deck(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    createdAt = item.getLong("createdAt")
                )
            )
        }

        for (i in 0 until cardArray.length()) {
            val item = cardArray.getJSONObject(i)

            cards.add(
                Flashcard(
                    id = item.getInt("id"),
                    question = item.getString("question"),
                    answer = item.getString("answer"),
                    deckId = item.getInt("deckId")
                )
            )
        }

        validateBackup(decks, cards)

        database.replaceAllData(decks, cards)

        return Pair(decks.size, cards.size)
    }

    private fun validateBackup(
        decks: List<Deck>,
        cards: List<Flashcard>
    ) {

        require(decks.isNotEmpty()) {
            "The backup does not contain any decks."
        }

        require(decks.all {
            it.id > 0 && it.name.isNotBlank()
        }) {
            "The backup contains an invalid deck."
        }

        require(cards.all {
            it.id > 0 &&
                    it.question.isNotBlank() &&
                    it.answer.isNotBlank()
        }) {
            "The backup contains an invalid flashcard."
        }

        require(decks.map { it.id }.distinct().size == decks.size) {
            "The backup contains duplicate deck IDs."
        }

        require(cards.map { it.id }.distinct().size == cards.size) {
            "The backup contains duplicate flashcard IDs."
        }

        val deckIds = decks.map { it.id }.toSet()

        require(cards.all {
            it.deckId in deckIds
        }) {
            "Some flashcards refer to decks that do not exist."
        }
    }
}