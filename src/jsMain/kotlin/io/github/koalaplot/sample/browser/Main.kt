package io.github.koalaplot.sample.browser

import androidx.compose.ui.window.Window
import io.github.koalaplot.sample.MainView
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Window Title") {
            MainView()
        }
    }
}
