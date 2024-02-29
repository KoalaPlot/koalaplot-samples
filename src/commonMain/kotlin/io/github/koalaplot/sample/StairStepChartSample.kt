package io.github.koalaplot.sample

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.StairstepPlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import kotlin.math.ceil

val stairStepSampleView = object : SampleView {
    override val name: String = "Stair Step"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            StairStepSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        StairStepSamplePlot(false, "Rainfall")
    }
}

private val colorMap = buildMap {
    val colors = generateHueColorPalette(RainData.rainfall.size)
    var i = 0
    RainData.rainfall.forEach {
        put(it.key, colors[i++])
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun StairStepSamplePlot(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = CategoryAxisModel(RainData.months),
            yAxisModel = FloatLinearAxisModel(
                0f..(ceil(RainData.max / 50.0) * 50.0).toFloat(),
                minimumMajorTickSpacing = 50.dp,
            ),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it, Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = { if (!thumbnail) AxisTitle("Month") },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "Rainfall (mm)",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            }
        ) {
            RainData.rainfall.entries.sortedBy { it.key }.forEach { (city, rain) ->
                chart(
                    city,
                    rain.mapIndexed { index, d ->
                        DefaultPoint(RainData.months[index], d.toFloat())
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYGraphScope<String, Float>.chart(
    city: String,
    data: List<DefaultPoint<String, Float>>,
) {
    StairstepPlot(
        data = data,
        lineStyle = LineStyle(
            brush = SolidColor(colorMap[city] ?: Color.Black),
            strokeWidth = 2.dp
        )
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    val cities = RainData.rainfall.keys.sorted()

    if (!thumbnail) {
        Surface(shadowElevation = 2.dp) {
            FlowLegend(
                itemCount = cities.size,
                symbol = { i ->
                    Symbol(
                        modifier = Modifier.size(padding),
                        fillBrush = SolidColor(colorMap[cities[i]] ?: Color.Black)
                    )
                },
                label = { i ->
                    Text(cities[i])
                },
                modifier = paddingMod
            )
        }
    }
}
