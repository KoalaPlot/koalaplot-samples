package io.github.koalaplot.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.sample.polar.polarScatterPlotSample
import io.github.koalaplot.sample.polar.radialLinePlotSample
import io.github.koalaplot.sample.polar.spiderPlotSample

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
    add(horizontalBarSampleView)
    add(groupedVerticalBarSampleView)
    add(groupedHorizontalBarSampleView)
    add(stackedVerticalBarSampleView)
    add(stackedHorizontalBarSampleView)
    add(waterfallChartSampleView)
    add(bulletGraphSampleView)
    add(xyLineSampleView)
    add(stairStepSampleView)
    add(xyLogLineSampleView)
    add(trigSampleView)
    add(areaPlotSample1View)
    add(stackedAreaSampleView)
    add(radialLinePlotSample)
    add(spiderPlotSample)
    add(polarScatterPlotSample)
    add(timeLineSampleView)
    add(liveTimeChartSampleView)
    add(xyLineChartGestureSampleView)
    add(candleStickSampleView)
}

@OptIn(ExperimentalMaterial3Api::class)
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
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    )
                }
            }) { innerPadding ->
                Column(
                    Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    if (selectedTabIndex == -1) {
                        ThumbnailsView(
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns)
        ) {
            itemsIndexed(samples) { index, item ->
                Thumbnail({ select(index) }, item.thumbnail)
            }
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
