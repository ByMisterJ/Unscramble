/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.unscramble.ui

/**
 * Data class that represents the game UI state
 */
// Data class: representa el estado inmutable de la UI del juego
// Este estado es observado por la UI y causa recomposición cuando cambia
data class GameUiState(
    val currentScrambledWord: String = "", // Palabra mezclada mostrada al usuario
    val currentWordCount: Int = 1, // Número de palabra actual en el juego
    val score: Int = 0, // Puntuación acumulada del jugador
    val isGuessedWordWrong: Boolean = false, // Indica si la respuesta del usuario fue incorrecta
    val isGameOver: Boolean = false // Estado que indica fin del juego y dispara el diálogo final
)
