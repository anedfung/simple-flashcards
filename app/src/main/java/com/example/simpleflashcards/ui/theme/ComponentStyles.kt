package com.example.simpleflashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ComponentStyles {

    object Flashcard {

        val Background: Color
            @Composable get() = MaterialTheme.colorScheme.surface

        val TextColor: Color
            @Composable get() = MaterialTheme.colorScheme.onSurface

        val Padding = 24.dp
        val MinHeight = 300.dp

        val Shape = AppShapes.Card
    }

    object Button {

        val Background: Color
            @Composable get() = MaterialTheme.colorScheme.primary

        val TextColor: Color
            @Composable get() = MaterialTheme.colorScheme.onPrimary

        val Height = AppDimensions.ButtonHeight
        val Shape = AppShapes.Button
    }

    object TextField {

        val Background: Color
            @Composable get() = MaterialTheme.colorScheme.surface

        val TextColor: Color
            @Composable get() = MaterialTheme.colorScheme.onSurface

        val BorderColor: Color
            @Composable get() = MaterialTheme.colorScheme.outline

        val Shape = AppShapes.Input
    }

    object Toolbar {

        val Background: Color
            @Composable get() = MaterialTheme.colorScheme.surface

        val TextColor: Color
            @Composable get() = MaterialTheme.colorScheme.onSurface
    }
}