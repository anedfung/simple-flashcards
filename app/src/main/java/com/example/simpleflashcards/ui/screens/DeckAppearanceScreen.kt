package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simpleflashcards.ui.components.AppToolbar
import com.example.simpleflashcards.ui.theme.DeckAppearance
import com.example.simpleflashcards.ui.theme.DeckCardColor
import com.example.simpleflashcards.ui.theme.DeckFont

@Composable
fun DeckAppearanceScreen(
    deckName: String,
    appearance: DeckAppearance,
    onQuestionColorChange: (DeckCardColor) -> Unit,
    onAnswerColorChange: (DeckCardColor) -> Unit,
    onFontChange: (DeckFont) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppToolbar("Customize deck") {
                TextButton(onClick = onBack) {
                    Text("Done")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = deckName,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Question card color",
                style = MaterialTheme.typography.titleMedium
            )

            ColorChoices(
                selected = appearance.questionColor,
                onSelected = onQuestionColorChange
            )

            HorizontalDivider()

            Text(
                text = "Answer card color",
                style = MaterialTheme.typography.titleMedium
            )

            ColorChoices(
                selected = appearance.answerColor,
                onSelected = onAnswerColorChange
            )

            HorizontalDivider()

            Text(
                text = "Card font",
                style = MaterialTheme.typography.titleMedium
            )

            Column {
                DeckFont.values().forEach { font ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = appearance.font == font,
                            onClick = {
                                onFontChange(font)
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = font.displayName,
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = font.family
                            )
                        )
                    }
                }
            }

            Text(
                text = "Changes are saved automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ColorChoices(
    selected: DeckCardColor,
    onSelected: (DeckCardColor) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeckCardColor.values().forEach { color ->
            FilterChip(
                selected = selected == color,
                onClick = {
                    onSelected(color)
                },
                label = {
                    Text(color.displayName)
                }
            )
        }
    }
}