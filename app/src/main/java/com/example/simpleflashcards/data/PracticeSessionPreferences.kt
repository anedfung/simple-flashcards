package com.example.simpleflashcards.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

data class PracticeSession(
    val deckId: Int,
    val shuffleEnabled: Boolean,
    val cardIds: List<Int>,
    val currentCardId: Int
)

data class PracticeSessionSnapshot(
    val loaded: Boolean,
    val session: PracticeSession?
)

class PracticeSessionPreferences(context: Context) {

    private val appContext = context.applicationContext

    private val sessionKey =
        stringPreferencesKey("last_practice_session")

    val session: Flow<PracticeSessionSnapshot> =
        appContext.appSettingsDataStore.data.map { preferences ->

            PracticeSessionSnapshot(
                loaded = true,
                session = preferences[sessionKey]?.let { decode(it) }
            )
        }

    suspend fun saveSession(session: PracticeSession) {

        val json = JSONObject().apply {
            put("deckId", session.deckId)
            put("shuffleEnabled", session.shuffleEnabled)
            put("currentCardId", session.currentCardId)

            put(
                "cardIds",
                JSONArray().apply {
                    session.cardIds.forEach { put(it) }
                }
            )
        }

        appContext.appSettingsDataStore.edit { preferences ->
            preferences[sessionKey] = json.toString()
        }
    }

    private fun decode(value: String): PracticeSession? {

        return try {
            val json = JSONObject(value)
            val cardArray = json.getJSONArray("cardIds")

            val cardIds = List(cardArray.length()) { index ->
                cardArray.getInt(index)
            }

            val deckId = json.getInt("deckId")
            val currentCardId = json.getInt("currentCardId")

            if (deckId <= 0 || cardIds.isEmpty()) {
                return null
            }

            PracticeSession(
                deckId = deckId,
                shuffleEnabled = json.getBoolean("shuffleEnabled"),
                cardIds = cardIds,
                currentCardId = currentCardId
            )

        } catch (_: Exception) {
            null
        }
    }
}