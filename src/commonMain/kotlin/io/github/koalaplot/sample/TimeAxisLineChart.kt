package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.IntLinearAxisModel
import io.github.koalaplot.core.xygraph.LongLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val timeLineSampleView = object : SampleView {
    override val name: String = "Time Chart"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            TimeSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        TimeSamplePlot(false, "Random Walk")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalTime::class)
@Composable
@Suppress("MagicNumber")
private fun TimeSamplePlot(thumbnail: Boolean, title: String) {
    val data = remember { mutableStateListOf(DefaultPoint(kotlin.time.Clock.System.now().epochSeconds, 0)) }
    var yDataMin by remember { mutableStateOf(0) }
    var yDataMax by remember { mutableStateOf(1) }

    Column {
        if (!thumbnail) {
            Button(
                onClick = {
                    val yLast = data.last().y
                    val yNext = if (Random.nextBoolean()) {
                        yLast + 1
                    } else {
                        yLast - 1
                    }
                    data.add(DefaultPoint(kotlin.time.Clock.System.now().epochSeconds, yNext))
                    yDataMin = minOf(yDataMin, yNext)
                    yDataMax = maxOf(yDataMax, yNext)
                }
            ) {
                Text("Add Step")
            }
        }

        ChartLayout(
            modifier = paddingMod.padding(end = 16.dp),
            title = { ChartTitle(title) },
            legendLocation = LegendLocation.BOTTOM
        ) {
            XYGraph(
                xAxisModel = LongLinearAxisModel(
                    range = (data.first().x)..(data.last().x) + 1
                ),
                yAxisModel = IntLinearAxisModel(
                    range = yDataMin..yDataMax
                ),
                xAxisLabels = {
                    if (!thumbnail) {
                        AxisLabel(Instant.fromEpochSeconds(it).toString(), Modifier.padding(top = 2.dp))
                    }
                },
                xAxisStyle = rememberAxisStyle(labelRotation = 90),
                xAxisTitle = {
                    if (!thumbnail) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AxisTitle("Time")
                        }
                    }
                },
                yAxisLabels = {
                    if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
                },
                yAxisTitle = {
                    if (!thumbnail) {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            AxisTitle(
                                "Value",
                                modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                                    .padding(bottom = padding)
                            )
                        }
                    }
                }
            ) {
                chart(data)
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYGraphScope<Long, Int>.chart(data: List<DefaultPoint<Long, Int>>) {
    LinePlot(
        data = data,
        lineStyle = LineStyle(
            brush = SolidColor(Color.Black),
            strokeWidth = 2.dp
        ),
    )
}
