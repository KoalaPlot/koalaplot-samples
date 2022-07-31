package io.github.koalaplot.sample.desktop

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import io.github.koalaplot.sample.MainView

fun main() = singleWindowApplication(
    title = "KoalaPlot Sample",
    state = WindowState()
) {
    MainView()
}
