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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // Jetpack Compose: estado mutable que desencadena recomposición cuando cambia
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel // Jetpack: clase que almacena datos de UI y sobrevive a cambios de configuración (rotaciones)
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow // StateFlow mutable para emisión de estados de UI de forma reactiva
import kotlinx.coroutines.flow.StateFlow // Flow que mantiene y emite el estado actual y futuras actualizaciones
import kotlinx.coroutines.flow.asStateFlow // Convierte MutableStateFlow en StateFlow de solo lectura
import kotlinx.coroutines.flow.update // Función para actualizar el estado de forma atómica y thread-safe

/**
 * ViewModel containing the app data and methods to process the data
 */
// ViewModel: contiene lógica de negocio y estado de UI; sobrevive a rotaciones y cambios de configuración
class GameViewModel : ViewModel() {

    // Estado de UI del juego usando StateFlow: permite que la UI observe cambios reactivamente
    // MutableStateFlow: versión privada y mutable del estado que solo el ViewModel puede modificar
    private val _uiState = MutableStateFlow(GameUiState())
    // StateFlow público de solo lectura: expone el estado a la UI sin permitir modificaciones directas
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // mutableStateOf: estado de Compose que desencadena recomposición; persiste la entrada del usuario
    var userGuess by mutableStateOf("")
        private set // setter privado: solo el ViewModel puede modificar este estado

    // Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    // init: bloque de inicialización que se ejecuta cuando se crea el ViewModel
    init {
        resetGame() // Inicializa el juego al crear el ViewModel
    }

    /*
     * Re-initializes the game data to restart the game.
     */
    // Evento: reinicia el juego, llamado desde la UI cuando el usuario elige jugar de nuevo
    fun resetGame() {
        usedWords.clear()
        // Actualiza el estado de UI con una nueva palabra; la UI reacciona automáticamente al cambio
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    /*
     * Update the user's guess
     */
    // Evento: actualiza el estado con la entrada del usuario desde el campo de texto
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord // Cambiar este estado desencadena recomposición en TextField
    }

    /*
     * Checks if the user's guess is correct.
     * Increases the score accordingly.
     */
    // Evento: verifica la respuesta del usuario cuando presiona Submit, actualiza puntuación o muestra error
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // User's guess is correct, increase the score
            // and call updateGameState() to prepare the game for next round
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore) // Actualiza el estado para la siguiente ronda
        } else {
            // User's guess is wrong, show an error
            // update: función de StateFlow que actualiza el estado de forma atómica y thread-safe
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true) // Inmutable update: crea nueva instancia del estado
            }
        }
        // Reset user guess
        updateUserGuess("") // Limpia el campo de entrada para la siguiente palabra
    }

    /*
     * Skip to next word
     */
    // Evento: permite al usuario saltar la palabra actual sin cambiar la puntuación
    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Reset user guess
        updateUserGuess("") // Limpia el campo de entrada
    }

    /*
     * Picks a new currentWord and currentScrambledWord and updates UiState according to
     * current game state.
     */
    // Actualiza el estado del juego: maneja lógica de fin de juego y transiciones entre rondas
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            //Last round in the game, update isGameOver to true, don't pick a new word
            // update: actualiza múltiples propiedades del estado de forma atómica
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true // Marca el fin del juego, disparando el diálogo de puntuación final
                )
            }
        } else{
            // Normal round in the game
            // Actualiza el estado para la siguiente ronda: nueva palabra, contador incrementado
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    // Mezcla las letras de la palabra actual para crear el acertijo
    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    // Selecciona una palabra aleatoria de la capa de datos y la mezcla
    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random() // Obtiene palabra aleatoria del conjunto de datos
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle() // Recursión: busca otra palabra si esta ya fue usada
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord) // Retorna la palabra mezclada para mostrar en UI
        }
    }
}
