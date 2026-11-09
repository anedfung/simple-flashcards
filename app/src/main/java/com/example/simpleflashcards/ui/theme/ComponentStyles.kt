package com.example.simpleflashcards.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ComponentStyles {

    object Flashcard {

        val Background = AppColors.Surface
        val TextColor = AppColors.TextPrimary

        val Padding = 24.dp
        val MinHeight = 300.dp

        val Shape = AppShapes.Card

    }

    object Button {

        val Background = AppColors.Primary
        val TextColor = Color.White

        val Height = AppDimensions.ButtonHeight
        val Shape = AppShapes.Button

    }

    object TextField {

        val Background = AppColors.Surface
        val TextColor = AppColors.TextPrimary
        val BorderColor = AppColors.Border

        val Shape = AppShapes.Input

    }

    object Toolbar {

        val Background = AppColors.Surface
        val TextColor = AppColors.TextPrimary

    }

}