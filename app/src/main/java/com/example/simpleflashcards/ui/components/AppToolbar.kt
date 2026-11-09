package com.example.simpleflashcards.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.simpleflashcards.ui.theme.ComponentStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(title: String) {

    TopAppBar(
        title = {
            Text(
                text = title,
                color = ComponentStyles.Toolbar.TextColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ComponentStyles.Toolbar.Background
        )
    )
}