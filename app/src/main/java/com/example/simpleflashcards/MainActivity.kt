package com.example.simpleflashcards

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

import com.example.simpleflashcards.data.FlashcardDatabase
import com.example.simpleflashcards.data.FlashcardRepository

import com.example.simpleflashcards.navigation.AppNavigation

import com.example.simpleflashcards.ui.theme.SimpleFlashcardsTheme

import com.example.simpleflashcards.viewmodel.FlashcardViewModel
import com.example.simpleflashcards.viewmodel.FlashcardViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize database
        val database = FlashcardDatabase.getDatabase(this)

        // Initialize repository
        val repository = FlashcardRepository(
            database.flashcardDao(),
            database.deckDao()
        )

        // Initialize ViewModel
        val factory = FlashcardViewModelFactory(repository)

        val viewModel = ViewModelProvider(
            this,
            factory
        )[FlashcardViewModel::class.java]

        // Launch application
        setContent {

            SimpleFlashcardsTheme {

                AppNavigation(
                    viewModel = viewModel
                )
            }
        }
    }
}