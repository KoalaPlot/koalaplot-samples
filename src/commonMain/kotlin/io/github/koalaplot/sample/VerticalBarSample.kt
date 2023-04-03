package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.BarChartEntry
import io.github.koalaplot.core.bar.DefaultBarChartEntry
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xychart.LinearAxisModel
import io.github.koalaplot.core.xychart.TickPosition
import io.github.koalaplot.core.xychart.XYChart
import io.github.koalaplot.core.xychart.rememberAxisStyle

private val colors = generateHueColorPalette(fibonacci.size)
private const val BarWidth = 0.8f

private fun barChartEntries(): List<BarChartEntry<Float, Float>> {
    return buildList {
        fibonacci.forEachIndexed { index, fl ->
            add(
                DefaultBarChartEntry(
                    xValue = (index + 1).toFloat(),
                    yMin = 0f,
                    yMax = fl,
                )
            )
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
val verticalBarSampleView = object : SampleView {
    override val name: String = "Vertical Bar"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            BarSamplePlot(true, TickPositionState(TickPosition.None, TickPosition.None), name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        var tickPositionState by remember {
            mutableStateOf(
                TickPositionState(
                    TickPosition.Outside,
                    TickPosition.Outside
                )
            )
        }

        Column {
            ChartLayout(
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f)
            ) {
                BarSamplePlot(false, tickPositionState, "Fibonacci Sequence")
            }
            Divider(modifier = Modifier.fillMaxWidth())
            TickPositionSelector(tickPositionState) {
                tickPositionState = it
            }
        }
    }
}

private val YAxisRange = 0f..25f
private val XAxisRange = 0.5f..8.5f

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun BarSamplePlot(
    thumbnail: Boolean = false,
    tickPositionState: TickPositionState,
    title: String
) {
    val barChartEntries = remember(thumbnail) { barChartEntries() }

    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) }
    ) {
        XYChart(
            xAxisModel = LinearAxisModel(
                XAxisRange,
                minimumMajorTickIncrement = 1f,
                minimumMajorTickSpacing = 10.dp,
                zoomRangeLimit = 3f,
                minorTickCount = 0
            ),
            yAxisModel = LinearAxisModel(
                YAxisRange,
                minimumMajorTickIncrement = 1f,
                minorTickCount = 0
            ),
            xAxisStyle = rememberAxisStyle(
                tickPosition = tickPositionState.horizontalAxis,
                color = Color.LightGray
            ),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(0), Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = { if (!thumbnail) AxisTitle("Position in Sequence") },
            yAxisStyle = rememberAxisStyle(tickPosition = tickPositionState.verticalAxis),
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(1), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "Value",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            },
            verticalMajorGridLineStyle = null
        ) {
            VerticalBarChart(
                series = listOf(barChartEntries),
                bar = { series, _, value ->
                    DefaultVerticalBar(
                        brush = SolidColor(colors[series]),
                        modifier = Modifier.fillMaxWidth(BarWidth),
                    ) {
                        if (!thumbnail) {
                            HoverSurface { Text(value.yMax.toString()) }
                        }
                    }
                }

            )
        }
    }
}

private data class TickPositionState(
    val verticalAxis: TickPosition,
    val horizontalAxis: TickPosition
)

@Composable
private fun TickPositionSelector(
    state: TickPositionState,
    update: (TickPositionState) -> Unit
) {
    ExpandableCard(
        modifier = paddingMod,
        titleContent = { Text("Axis options", modifier = paddingMod) }
    ) {
        Row {
            Column {
                Text("Vertical")
                TickPosition.values().forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            it == state.verticalAxis,
                            onClick = { update(state.copy(verticalAxis = it)) }
                        )
                        Text(it.name)
                    }
                }
            }
            Column {
                Text("Horizontal")
                TickPosition.values().forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            it == state.horizontalAxis,
                            onClick = { update(state.copy(horizontalAxis = it)) }
                        )
                        Text(it.name)
                    }
                }
            }
        }
    }
}
