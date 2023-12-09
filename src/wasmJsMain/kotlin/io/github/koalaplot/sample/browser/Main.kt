package io.github.koalaplot.sample.browser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.koalaplot.sample.MainView

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("Koala Plot Samples") {
        MainView()
    }
}
