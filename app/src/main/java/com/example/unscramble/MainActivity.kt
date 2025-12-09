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

package com.example.unscramble

import android.os.Bundle
import androidx.activity.ComponentActivity // Jetpack: clase base para actividades que usan componentes modernos de Android
import androidx.activity.compose.setContent // Jetpack Compose: establece el contenido de la UI usando funciones componibles
import androidx.activity.enableEdgeToEdge // Permite que la UI se extienda de borde a borde en la pantalla
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface // Material 3: contenedor básico que aplica color y elevación del tema
import androidx.compose.ui.Modifier
import com.example.unscramble.ui.GameScreen
import com.example.unscramble.ui.theme.UnscrambleTheme

// ComponentActivity: actividad compatible con Jetpack que sobrevive a cambios de configuración como rotaciones
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Habilita diseño edge-to-edge para UI inmersiva
        super.onCreate(savedInstanceState)
        // setContent: define la UI usando Jetpack Compose en lugar de XML layouts
        setContent {
            // UnscrambleTheme: aplica el tema Material con colores, tipografía y formas personalizadas
            UnscrambleTheme {
                // Surface: contenedor Material que proporciona fondo y elevación según el tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    GameScreen() // Función composable que contiene la pantalla principal del juego
                }
            }
        }
    }
}
