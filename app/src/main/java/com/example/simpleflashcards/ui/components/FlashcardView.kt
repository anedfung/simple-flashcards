
package com.example.simpleflashcards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

import com.example.simpleflashcards.ui.theme.DeckAppearance
import com.example.simpleflashcards.ui.theme.ComponentStyles

@Composable
fun FlashcardView(
    question: String,
    answer: String,
    appearance: DeckAppearance = DeckAppearance(),
    textScale: Float = 1f
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

    val clipboardManager = LocalClipboardManager.current

    val activeText = if (revealed) answer else question

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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = activeText,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = appearance.font.family,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize * textScale
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
                    fontFamily = appearance.font.family,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize * textScale
                )
            )
        }

        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(activeText))
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy current side",
                tint = textColor
            )
        }
    }
}
