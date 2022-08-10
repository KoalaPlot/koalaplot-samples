package io.github.koalaplot.sample.browser

import io.github.koalaplot.sample.MainView
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        browserViewportWindow("Koala Plot Samples") {
            MainView()
        }
    }
}
