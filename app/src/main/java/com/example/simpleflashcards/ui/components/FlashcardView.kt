package com.example.simpleflashcards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.ui.theme.DeckAppearance
import com.example.simpleflashcards.ui.theme.ComponentStyles

@Composable
fun FlashcardView(
    question: String,
    answer: String,
    appearance: DeckAppearance = DeckAppearance()
) {
    var revealed by remember(question, answer) {
        mutableStateOf(false)
    }

    val selectedColor = if (revealed) {
        appearance.answerColor
    } else {
        appearance.questionColor
    }

    val backgroundColor =
        selectedColor.background ?: MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        selectedColor.foreground ?: MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ComponentStyles.Flashcard.MinHeight)
            .background(
                backgroundColor,
                ComponentStyles.Flashcard.Shape
            )
            .clickable {
                revealed = !revealed
            }
            .padding(ComponentStyles.Flashcard.Padding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (revealed) answer else question,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = appearance.font.family
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (revealed) {
                    "Tap to see question"
                } else {
                    "Tap to reveal answer"
                },
                color = textColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = appearance.font.family
                )
            )
        }
    }
}