# Simple Flashcards

A lightweight Android flashcard application built with Kotlin and Jetpack Compose.

## Technologies

- Kotlin
- Jetpack Compose
- Material 3
- Room Database

## Features

- Create flashcards
- Edit and delete flashcards
- Sequential practice
- Randomized practice
- Local data persistence
- Customizable interface
- Light and dark themes
- Backup and restore

## Download

The latest APK is available on the [GitHub Releases page](https://github.com/anedfung/simple-flashcards/releases/latest).

### Installation

1. Open the [latest release](https://github.com/anedfung/simple-flashcards/releases/latest).
2. Download the APK file attached to the release.
3. Open the downloaded APK on your Android device.
4. Follow Android's installation prompts.

You may need to allow your browser or file manager to install apps from that source.

## Requirements

- Android device
- Android Studio (for development)
- Android SDK
- Kotlin
- Gradle

## Building

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Wait for Gradle synchronization.
4. Select **Build > Build APK(s)**.

The generated debug APK is usually located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

To generate a signed release APK, select **Build > Generate Signed Bundle / APK** in Android Studio and choose APK.

The generated release APK is usually located at:

```text
app/build/outputs/apk/release/app-release.apk
```