package com.example.simpleflashcards.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.simpleflashcards.ui.theme.ComponentStyles

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {

    val style = ComponentStyles.TextField

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = style.Shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = style.BorderColor,
            unfocusedBorderColor = style.BorderColor,
            focusedTextColor = style.TextColor,
            unfocusedTextColor = style.TextColor
        )
    )
}