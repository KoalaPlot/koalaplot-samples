@file:Suppress("MagicNumber")

package io.github.koalaplot.sample

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.LogAxisModel
import io.github.koalaplot.core.xygraph.XYGraph

@OptIn(ExperimentalKoalaPlotApi::class)
val xyLogLineSampleView = object : SampleView {
    override val name: String = "XY Line - Log Axis"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            XYSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        XYSamplePlot(false, "Fibonacci Sequence")
    }
}

private val YAxisRange = -1..4
private val XAxisRange = -0.5f..15.5f

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYSamplePlot(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) }
    ) {
        XYGraph(
            xAxisModel = FloatLinearAxisModel(
                XAxisRange,
                minimumMajorTickIncrement = 1f,
                minorTickCount = 0
            ),
            yAxisModel = LogAxisModel(YAxisRange),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel("${it.toInt()}", Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = { if (!thumbnail) AxisTitle("Position in Sequence") },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
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
        ) {
            LinePlot(
                data = fibonacciExtended.mapIndexed { index, fl ->
                    DefaultPoint(index.toFloat(), fl)
                },
                lineStyle = LineStyle(brush = SolidColor(Color.Black), strokeWidth = 2.dp),
                symbol = { point ->
                    Symbol(
                        shape = CircleShape,
                        fillBrush = SolidColor(Color.Black),
                        modifier = Modifier.size(8.dp).then(
                            if (!thumbnail) {
                                Modifier.hoverableElement {
                                    HoverSurface { Text(point.y.toString()) }
                                }
                            } else {
                                Modifier
                            }
                        )
                    )
                }
            )
        }
    }
}
