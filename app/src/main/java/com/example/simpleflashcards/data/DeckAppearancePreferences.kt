package com.example.simpleflashcards.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.simpleflashcards.ui.theme.DeckAppearance
import com.example.simpleflashcards.ui.theme.DeckCardColor
import com.example.simpleflashcards.ui.theme.DeckFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckAppearancePreferences(context: Context) {

    private val appContext = context.applicationContext

    fun getAppearance(deckId: Int): Flow<DeckAppearance> {
        val questionKey =
            stringPreferencesKey("deck_${deckId}_question_color")

        val answerKey =
            stringPreferencesKey("deck_${deckId}_answer_color")

        val fontKey =
            stringPreferencesKey("deck_${deckId}_font")

        return appContext.appSettingsDataStore.data.map { preferences ->
            DeckAppearance(
                questionColor = DeckCardColor.fromPreference(
                    preferences[questionKey]
                ),
                answerColor = DeckCardColor.fromPreference(
                    preferences[answerKey]
                ),
                font = DeckFont.fromPreference(
                    preferences[fontKey]
                )
            )
        }
    }

    suspend fun setQuestionColor(
        deckId: Int,
        color: DeckCardColor
    ) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[
                stringPreferencesKey("deck_${deckId}_question_color")
            ] = color.name
        }
    }

    suspend fun setAnswerColor(
        deckId: Int,
        color: DeckCardColor
    ) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[
                stringPreferencesKey("deck_${deckId}_answer_color")
            ] = color.name
        }
    }

    suspend fun setFont(
        deckId: Int,
        font: DeckFont
    ) {
        appContext.appSettingsDataStore.edit { preferences ->
            preferences[
                stringPreferencesKey("deck_${deckId}_font")
            ] = font.name
        }
    }
}