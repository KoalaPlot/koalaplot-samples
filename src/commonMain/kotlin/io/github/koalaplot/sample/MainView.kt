package io.github.koalaplot.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.theme.KoalaPlotTheme
import kotlin.math.ceil
import kotlin.math.min

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
    add(minimalBarChartSampleView)
    add(bulletGraphSampleView)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    var currentPage by remember { mutableStateOf(0) }

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
                Column(Modifier.fillMaxWidth()) {
                    if (selectedTabIndex == -1) {
                        ThumbnailsView(
                            currentPage = currentPage,
                            setCurrentPage = { currentPage = it },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            selectedTabIndex = it
                        }
                    } else {
                        samples[selectedTabIndex].content.invoke()
                    }
                }
            }
        }
    }
}

/**
 * Displays the sample thumbnails.
 */
@Composable
private fun ThumbnailsView(
    currentPage: Int,
    setCurrentPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
    select: (Int) -> Unit
) {
    BoxWithConstraints(modifier) {
        val sizeClass = WindowSizeClass.fromSize(maxWidth, maxHeight)

        @Suppress("MagicNumber")
        val columns = when (sizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 2
            WindowWidthSizeClass.Medium -> 3
            WindowWidthSizeClass.Expanded -> 4
        }

        @Suppress("MagicNumber")
        val rows = when (sizeClass.heightSizeClass) {
            WindowHeightSizeClass.Compact -> 2
            WindowHeightSizeClass.Medium -> 3
            WindowHeightSizeClass.Expanded -> 4
        }

        val (numPages, pageSize) =
            remember(rows, columns) { ceil(samples.size.toDouble() / (rows * columns)).toInt() to rows * columns }

        Pager(numPages, currentPage, { setCurrentPage(it) }) {
            Layout(content = {
                for (row in 0 until rows) {
                    for (column in 0 until columns) {
                        val index = currentPage * pageSize + row * columns + column
                        if (index < samples.size) {
                            Thumbnail({ select(index) }, samples[index].thumbnail)
                        }
                    }
                }
            }) { measurables, constraints ->
                val cellSize =
                    min(constraints.maxWidth.toDouble() / columns, constraints.maxHeight.toDouble() / rows).toInt()

                val placeables = measurables.map { it.measure(Constraints.fixed(cellSize, cellSize)) }

                layout(cellSize * columns, cellSize * rows) {
                    var row = 0
                    var column = 0
                    placeables.forEach {
                        it.place(column * cellSize, row * cellSize)
                        column++
                        if (column >= columns) {
                            column = 0
                            row++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Pager(
    numPages: Int,
    currentPage: Int,
    setCurrentPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
    page: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            // Left button
            IconButton(
                onClick = { setCurrentPage(currentPage - 1) },
                enabled = currentPage > 0,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
            }

            Text("${currentPage + 1} / $numPages", modifier = Modifier.align(Alignment.CenterVertically))

            // Right button
            IconButton(
                onClick = { setCurrentPage(currentPage + 1) },
                enabled = currentPage < numPages - 1,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            page()
        }
    }
}

@Composable
private fun Thumbnail(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        shadowElevation = 2.dp,
        modifier = Modifier.padding(padding).clickable(onClick = onClick).aspectRatio(1f),
        content = content
    )
}

interface SampleView {
    val name: String
    val thumbnail: @Composable () -> Unit
    val content: @Composable () -> Unit
}
