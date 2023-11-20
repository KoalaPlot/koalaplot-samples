package io.github.koalaplot.sample

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.LinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import kotlin.math.PI
import kotlin.math.cos

val trigSampleView = object : SampleView {
    override val name: String = "Area to x-axis"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            CosineSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        CosineSamplePlot(false, "Cosine")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun CosineSamplePlot(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = LinearAxisModel(0f..4.0.toFloat()), // units of PI
            yAxisModel = LinearAxisModel(-1.1f..1.1f, minimumMajorTickSpacing = 50.dp),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel("$it \u03C0", Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = { if (!thumbnail) AxisTitle("Angle (radians)") },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "cosine",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            }
        ) {
            LinePlot(
                data = cosineData,
                lineStyle = LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 2.dp),
                areaStyle = AreaStyle(
                    brush = SolidColor(Color.Blue),
                    alpha = 0.5f,
                ),
                areaBaseline = AreaBaseline.ConstantLine(0f)
            )
        }
    }
}

@Suppress("MagicNumber")
private val cosineData: List<Point<Float, Float>> = buildList {
    for (i in 0..500) {
        add(DefaultPoint((i * 4.0 / 500.0).toFloat(), cos(i * 4.0 * PI / 500.0).toFloat()))
    }
}
