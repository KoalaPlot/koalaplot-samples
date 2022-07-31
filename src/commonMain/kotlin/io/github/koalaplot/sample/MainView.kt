package io.github.koalaplot.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.theme.KoalaPlotTheme

// https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes
enum class WindowWidthSizeClass(private val threshold: Dp) {
    Compact(0.dp), Medium(600.dp), Expanded(840.dp);

    companion object {
        fun fromWidth(width: Dp): WindowWidthSizeClass {
            return when {
                width >= Expanded.threshold -> Expanded
                width >= Medium.threshold -> Medium
                else -> Compact
            }
        }
    }
}

enum class WindowHeightSizeClass(private val threshold: Dp) {
    Compact(0.dp), Medium(480.dp), Expanded(900.dp);

    companion object {
        fun fromHeight(height: Dp): WindowHeightSizeClass {
            return when {
                height >= Expanded.threshold -> Expanded
                height >= Medium.threshold -> Medium
                else -> Compact
            }
        }
    }
}

class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
) {
    companion object {
        fun fromSize(width: Dp, height: Dp): WindowSizeClass {
            return WindowSizeClass(
                WindowWidthSizeClass.fromWidth(width),
                WindowHeightSizeClass.fromHeight(height)
            )
        }
    }
}

private val samples = buildList {
    add(pieSampleView)
    add(verticalBarSampleView)
    add(groupedVerticalBarSampleView)
    add(stackedVerticalBarSampleView)
    add(xyLineSampleView)
    add(xyLogLineSampleView)
    // add(instrinsicsView)
}

private const val ThumbnailsPerRow = 3

@Composable
fun MainView() {
    MaterialTheme {
        KoalaPlotTheme {
            var selectedTabIndex by remember { mutableStateOf(-1) }

            Scaffold(topBar = {
                if (selectedTabIndex == -1) {
                    TopAppBar(
                        title = { Text("Select Sample") }
                    )
                } else {
                    TopAppBar(
                        title = { Text(samples[selectedTabIndex].name) },
                        navigationIcon = {
                            IconButton({ selectedTabIndex = -1 }) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        }
                    )
                }
            }) {
                if (selectedTabIndex == -1) {
                    // Scroll not yet implemented in JS target, throws runtime exception
                    Column(/*Modifier.verticalScroll(rememberScrollState())*/) {
                        for (i in samples.indices step ThumbnailsPerRow) {
                            Row {
                                Thumbnail({ selectedTabIndex = i }, samples[i].thumbnail)
                                if (i + 1 < samples.size)
                                    Thumbnail(
                                        { selectedTabIndex = i + 1 },
                                        samples[i + 1].thumbnail
                                    )
                                if (i + 2 < samples.size)
                                    Thumbnail(
                                        { selectedTabIndex = i + 2 },
                                        samples[i + 2].thumbnail
                                    )
                            }
                        }
                    }
                } else {
                    samples[selectedTabIndex].content.invoke()
                }
            }
        }
    }
}

@Composable
private fun RowScope.Thumbnail(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        elevation = 2.dp,
        modifier = Modifier.weight(1f).padding(padding).clickable(onClick = onClick)
            .aspectRatio(1f),
        content = content
    )
}

interface SampleView {
    val name: String
    val thumbnail: @Composable () -> Unit
    val content: @Composable () -> Unit
}
