package com.example.simpleflashcards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simpleflashcards.ui.theme.ComponentStyles

@Composable
fun FlashcardView(
    question: String,
    answer: String
) {

    var revealed by remember(question, answer) {
        mutableStateOf(false)
    }

    val style = ComponentStyles.Flashcard

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = style.MinHeight)
            .background(
                style.Background,
                style.Shape
            )
            .clickable {
                revealed = !revealed
            }
            .padding(style.Padding),

        contentAlignment = Alignment.Center

    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (revealed) answer else question,
                color = style.TextColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (revealed)
                    "Tap to see question"
                else
                    "Tap to reveal answer"
            )
        }
    }
}