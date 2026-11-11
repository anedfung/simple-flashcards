
package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource

import com.example.simpleflashcards.R
import com.example.simpleflashcards.data.Flashcard
import com.example.simpleflashcards.ui.components.AppButton
import com.example.simpleflashcards.ui.components.AppTextField
import com.example.simpleflashcards.ui.components.AppToolbar

@Composable
fun EditCardScreen(
    deckName: String,
    deckId: Int,
    card: Flashcard?,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {

    var question by remember(card?.id) {
        mutableStateOf(card?.question ?: "")
    }

    var answer by remember(card?.id) {
        mutableStateOf(card?.answer ?: "")
    }

    Scaffold(
        topBar = {
            AppToolbar(
                if (card == null) {
                    stringResource(R.string.new_card_title, deckName)
                } else {
                    stringResource(R.string.edit_card_title, deckName)
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AppTextField(
                value = question,
                onValueChange = {
                    question = it
                },
                label = stringResource(R.string.question),
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = answer,
                onValueChange = {
                    answer = it
                },
                label = stringResource(R.string.answer),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                text = stringResource(R.string.save_flashcard),
                onClick = {
                    if (
                        question.isNotBlank() &&
                        answer.isNotBlank()
                    ) {
                        onSave(question, answer)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
