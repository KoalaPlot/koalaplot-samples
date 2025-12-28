package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.DefaultBarPosition
import io.github.koalaplot.core.bar.DefaultHorizontalBarPlotEntry
import io.github.koalaplot.core.bar.HorizontalBarPlot
import io.github.koalaplot.core.bar.HorizontalBarPlotEntry
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.TickPosition
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle

private val colors = generateHueColorPalette(fibonacci.size)
private const val BarWidth = 0.8f

private fun barChartEntries(): List<HorizontalBarPlotEntry<Float, Float>> = buildList {
    fibonacci.forEachIndexed { index, fl ->
        add(DefaultHorizontalBarPlotEntry((index + 1).toFloat(), DefaultBarPosition(0f, fl)))
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
val horizontalBarSampleView = object : SampleView {
    override val name: String = "Horizontal Bar"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            BarSamplePlot(TickPositionState(TickPosition.None, TickPosition.None), name, thumbnail = true)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        var tickPositionState by remember {
            mutableStateOf(
                TickPositionState(
                    TickPosition.Outside,
                    TickPosition.Outside,
                ),
            )
        }

        Column {
            ChartLayout(
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f),
            ) {
                BarSamplePlot(tickPositionState, "Fibonacci Sequence")
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            TickPositionSelector(tickPositionState, {
                tickPositionState = it
            }, Modifier)
        }
    }
}

private val XAxisRange = 0f..25f
private val YAxisRange = 0.5f..8.5f

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun BarSamplePlot(
    tickPositionState: TickPositionState,
    title: String,
    modifier: Modifier = Modifier,
    thumbnail: Boolean = false,
) {
    val barChartEntries = remember(thumbnail) { barChartEntries() }

    ChartLayout(
        modifier = modifier.then(paddingMod),
        title = { ChartTitle(title) },
    ) {
        XYGraph(
            yAxisModel = FloatLinearAxisModel(
                YAxisRange,
                minimumMajorTickIncrement = 1f,
                minimumMajorTickSpacing = 10.dp,
                minViewExtent = 3f,
                minorTickCount = 0,
            ),
            xAxisModel = FloatLinearAxisModel(
                XAxisRange,
                minimumMajorTickIncrement = 1f,
                minorTickCount = 0,
            ),
            yAxisStyle = rememberAxisStyle(
                tickPosition = tickPositionState.verticalAxis,
                color = Color.LightGray,
            ),
            yAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(0), Modifier.padding(top = 2.dp))
                }
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "Position in Sequence",
                        modifier = Modifier
                            .rotateVertically(
                                VerticalRotation.COUNTER_CLOCKWISE,
                            ).padding(bottom = padding),
                    )
                }
            },
            xAxisStyle = rememberAxisStyle(tickPosition = tickPositionState.horizontalAxis),
            xAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(1), Modifier.absolutePadding(right = 2.dp))
            },
            xAxisTitle = {
                if (!thumbnail) {
                    AxisTitle("Value")
                }
            },
            horizontalMajorGridLineStyle = null,
        ) {
            HorizontalBarPlot(
                barChartEntries,
                bar = { index, _, _ ->
                    DefaultBar(
                        brush = SolidColor(colors[0]),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (!thumbnail) {
                            HoverSurface { Text(barChartEntries[index].x.end.toString()) }
                        }
                    }
                },
                barWidth = BarWidth,
            )
        }
    }
}
