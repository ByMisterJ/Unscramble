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

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column // Jetpack Compose: elemento de UI que organiza hijos verticalmente
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState // Compose: recuerda estado del scroll sobreviviendo a recomposiciones
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog // Material 3: diálogo modal para mostrar información importante
import androidx.compose.material3.Button // Material 3: botón con estilo Material Design que responde a clics
import androidx.compose.material3.Card // Material 3: superficie con bordes redondeados y elevación
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme // Material 3: esquema de colores del tema actual
import androidx.compose.material3.MaterialTheme.shapes // Material 3: formas (rounded corners) definidas en el tema
import androidx.compose.material3.MaterialTheme.typography // Material 3: estilos tipográficos definidos en el tema
import androidx.compose.material3.OutlinedButton // Material 3: botón con borde sin relleno
import androidx.compose.material3.OutlinedTextField // Material 3: campo de texto con borde para entrada de usuario
import androidx.compose.material3.Text // Jetpack Compose: elemento de UI para mostrar texto
import androidx.compose.material3.TextButton // Material 3: botón de texto sin borde ni relleno
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable // Jetpack Compose: anotación que marca funciones como componibles (building blocks de UI)
import androidx.compose.runtime.collectAsState // Compose: convierte StateFlow en State de Compose para observación reactiva
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Jetpack Compose: obtiene o crea ViewModel asociado al ciclo de vida
import com.example.unscramble.R
import com.example.unscramble.ui.theme.UnscrambleTheme

// @Composable: marca esta función como componible, permitiendo construir UI declarativa
// Esta es la pantalla principal del juego que contiene todos los elementos de UI
@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) { // viewModel(): crea/obtiene ViewModel que sobrevive a recomposiciones
    // collectAsState: convierte StateFlow en State de Compose, recomponiendo la UI cuando el estado cambia
    val gameUiState by gameViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    // Column: elemento de UI de Compose que organiza sus hijos en vertical
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()) // rememberScrollState: mantiene estado del scroll entre recomposiciones
            .safeDrawingPadding()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Text: elemento de UI de Compose para mostrar texto con estilos de Material
        Text(
            text = stringResource(R.string.app_name),
            style = typography.titleLarge, // Usa tipografía definida en MaterialTheme
        )
        GameLayout(
            // Eventos: callbacks que permiten a la UI comunicarse con el ViewModel
            onUserGuessChanged = { gameViewModel.updateUserGuess(it) }, // Evento: actualiza texto mientras el usuario escribe
            wordCount = gameUiState.currentWordCount, // Estado de UI: número de palabra actual
            userGuess = gameViewModel.userGuess, // Estado de UI: entrada actual del usuario
            onKeyboardDone = { gameViewModel.checkUserGuess() }, // Evento: verifica respuesta cuando usuario presiona Done
            currentScrambledWord = gameUiState.currentScrambledWord, // Estado de UI: palabra mezclada
            isGuessWrong = gameUiState.isGuessedWordWrong, // Estado de UI: indicador de error
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(mediumPadding)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding),
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Button: botón Material 3 que dispara evento al hacer clic
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { gameViewModel.checkUserGuess() } // Evento: envía acción de verificar respuesta al ViewModel
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    fontSize = 16.sp
                )
            }

            // OutlinedButton: variante de botón Material 3 con borde
            OutlinedButton(
                onClick = { gameViewModel.skipWord() }, // Evento: salta a la siguiente palabra
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    fontSize = 16.sp
                )
            }
        }

        GameStatus(score = gameUiState.score, modifier = Modifier.padding(20.dp))

        // Renderizado condicional basado en estado: muestra diálogo cuando el juego termina
        if (gameUiState.isGameOver) {
            FinalScoreDialog(
                score = gameUiState.score,
                onPlayAgain = { gameViewModel.resetGame() } // Evento: reinicia el juego
            )
        }
    }
}

// @Composable: función componible que muestra la puntuación actual
@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier) {
    // Card: superficie Material 3 con elevación y bordes redondeados
    Card(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.score, score),
            style = typography.headlineMedium, // Estilo tipográfico de Material Theme
            modifier = Modifier.padding(8.dp)
        )

    }
}

// @Composable: función componible que define el layout principal del juego
@Composable
fun GameLayout(
    currentScrambledWord: String,
    wordCount: Int,
    isGuessWrong: Boolean,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit, // Callback de evento: función lambda que maneja cambios en el texto
    onKeyboardDone: () -> Unit, // Callback de evento: función lambda que maneja acción Done del teclado
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    // Card: contenedor Material que aplica elevación y estilo visual
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            Text(
                modifier = Modifier
                    .clip(shapes.medium) // shapes: formas de Material Theme para bordes redondeados
                    .background(colorScheme.surfaceTint) // colorScheme: colores del tema Material actual
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
                text = stringResource(R.string.word_count, wordCount),
                style = typography.titleMedium, // Tipografía de Material
                color = colorScheme.onPrimary // Color de Material que contrasta con el fondo
            )
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium // Tipografía grande de Material para destacar
            )
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            // OutlinedTextField: campo de texto Material 3 para entrada de usuario
            OutlinedTextField(
                value = userGuess, // Estado de UI: texto actual del usuario
                singleLine = true,
                shape = shapes.large, // Forma de Material Theme
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors( // Colores de Material Theme
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = onUserGuessChanged, // Evento: callback cuando el usuario escribe
                label = {
                    // Renderizado condicional: muestra mensaje de error o instrucción según estado
                    if (isGuessWrong) {
                        Text(stringResource(R.string.wrong_guess))
                    } else {
                        Text(stringResource(R.string.enter_your_word))
                    }
                },
                isError = isGuessWrong, // Estado de UI: indica error visualmente con color rojo
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Configura botón Done en el teclado
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() } // Evento: callback cuando usuario presiona Done
                )
            )
        }
    }
}

/*
 * Creates and shows an AlertDialog with final score.
 */
// @Composable: función componible que muestra diálogo modal con la puntuación final
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit, // Evento: callback para reiniciar el juego
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    // AlertDialog: componente Material 3 que muestra diálogo modal sobre la UI principal
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            // TextButton: botón Material 3 de texto sin fondo
            TextButton(
                onClick = {
                    activity.finish() // Evento: cierra la actividad cuando usuario elige salir
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) { // Evento: callback para reiniciar juego
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

// @Preview: anotación de Jetpack Compose que permite previsualizar la UI en Android Studio
@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    // UnscrambleTheme: aplica el tema Material para la preview
    UnscrambleTheme {
        GameScreen() // Muestra la pantalla del juego en modo preview
    }
}
