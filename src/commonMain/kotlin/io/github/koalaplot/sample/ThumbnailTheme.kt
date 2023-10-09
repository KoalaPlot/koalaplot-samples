package io.github.koalaplot.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.style.KoalaPlotTheme

/**
 * Sets the KoalaPlotTheme with values used for Thumbnail chart views.
 */
@Composable
fun ThumbnailTheme(content: @Composable () -> Unit) {
    KoalaPlotTheme(
        axis = KoalaPlotTheme.axis.copy(
            majorTickSize = 0.dp,
            minorTickSize = 0.dp,
            majorGridlineStyle = null,
            minorGridlineStyle = null
        ),
        content = content
    )
}
