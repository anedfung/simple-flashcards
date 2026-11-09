package com.example.simpleflashcards.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simpleflashcards.ui.theme.ComponentStyles

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val style = ComponentStyles.Button

    Button(
        onClick = onClick,
        modifier = modifier.height(style.Height),
        shape = style.Shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = style.Background,
            contentColor = style.TextColor
        )
    ) {

        Text(text)
    }
}