
package com.example.simpleflashcards.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource

import com.example.simpleflashcards.R
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
            AppToolbar(
                title = stringResource(R.string.customize_deck),
                onBack = onBack
            )
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
                text = stringResource(R.string.question_card_color),
                style = MaterialTheme.typography.titleMedium
            )

            ColorChoices(
                selected = appearance.questionColor,
                onSelected = onQuestionColorChange
            )

            HorizontalDivider()

            Text(
                text = stringResource(R.string.answer_card_color),
                style = MaterialTheme.typography.titleMedium
            )

            ColorChoices(
                selected = appearance.answerColor,
                onSelected = onAnswerColorChange
            )

            HorizontalDivider()

            Text(
                text = stringResource(R.string.card_font),
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
                            text = when (font) {
                                DeckFont.DEFAULT ->
                                    stringResource(R.string.font_default)

                                DeckFont.SANS_SERIF ->
                                    stringResource(R.string.font_sans_serif)

                                DeckFont.SERIF ->
                                    stringResource(R.string.font_serif)

                                DeckFont.MONOSPACE ->
                                    stringResource(R.string.font_monospace)
                            },
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = font.family
                            )
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.changes_saved_automatically),
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
                    Text(
                        text = when (color) {
                            DeckCardColor.DEFAULT ->
                                stringResource(R.string.color_default)

                            DeckCardColor.BLUE ->
                                stringResource(R.string.color_blue)

                            DeckCardColor.GREEN ->
                                stringResource(R.string.color_green)

                            DeckCardColor.PEACH ->
                                stringResource(R.string.color_peach)

                            DeckCardColor.LAVENDER ->
                                stringResource(R.string.color_lavender)

                            DeckCardColor.YELLOW ->
                                stringResource(R.string.color_yellow)
                        }
                    )
                }
            )
        }
    }
}
