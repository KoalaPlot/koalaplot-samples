package io.github.koalaplot.sample.browser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.koalaplot.sample.MainView

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        MainView()
    }
}
