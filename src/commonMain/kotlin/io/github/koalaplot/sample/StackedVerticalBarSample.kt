package io.github.koalaplot.sample

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.BarChartEntry
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarChart
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.theme.KoalaPlotTheme
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xychart.CategoryAxisModel
import io.github.koalaplot.core.xychart.LineStyle
import io.github.koalaplot.core.xychart.LinearAxisModel
import io.github.koalaplot.core.xychart.XYChart
import io.github.koalaplot.core.xychart.rememberAxisStyle
import kotlin.math.ceil
import kotlin.math.max

private val colors = generateHueColorPalette(PopulationData.Categories.values().size)

private const val BarWidth = 0.8f

internal data class PopulationBarChartEntry<X, Y>(
    override val xValue: X,
    override val yMin: Y,
    override val yMax: Y,
    val borough: PopulationData.Categories,
    val population: Int
) : BarChartEntry<X, Y>

private fun barChartEntries(): Pair<List<List<PopulationBarChartEntry<Int, Float>>>, Float> {
    val series =
        Array<MutableList<PopulationBarChartEntry<Int, Float>>>(PopulationData.Categories.values().size) {
            ArrayList(PopulationData.years.size)
        }

    var maxPopulation = 0f

    PopulationData.years.forEachIndexed { yearIndex, year ->
        var yearTotal = 0f

        PopulationData.Categories.values().forEachIndexed { bIndex, borough ->
            series[bIndex] += PopulationBarChartEntry(
                xValue = year,
                yMin = yearTotal,
                yMax = yearTotal + PopulationData.data[borough]!![yearIndex].toFloat(),
                borough,
                PopulationData.data[borough]!![yearIndex]
            )

            yearTotal += PopulationData.data[borough]!![yearIndex].toFloat()
        }

        maxPopulation = max(maxPopulation, yearTotal)
    }

    return series.toList() to maxPopulation
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    if (!thumbnail) {
        Surface(elevation = 2.dp) {
            FlowLegend(
                itemCount = PopulationData.Categories.values().size,
                symbol = { i ->
                    Symbol(
                        modifier = Modifier.size(padding), fillBrush = SolidColor(colors[i])
                    )
                },
                label = { i ->
                    Text(PopulationData.Categories.values()[i].toString())
                },
                modifier = paddingMod
            )
        }
    }
}

val stackedVerticalBarSampleView = object : SampleView {
    override val name: String = "Stacked Vertical Bar"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            StackedBarSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(minorGridlineStyle = minorGridLineStyle)) {
            StackedBarSamplePlot(false, "New York City Population")
        }
    }
}

private const val PopulationScale = 1E6

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun StackedBarSamplePlot(thumbnail: Boolean = false, title: String) {
    val (barChartEntries, maxPopulation) = remember { barChartEntries() }

    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYChart(
            xAxisModel = CategoryAxisModel(PopulationData.years),
            yAxisModel = LinearAxisModel(
                0f..(ceil(maxPopulation / PopulationScale) * PopulationScale).toFloat(),
            ),
            xAxisLabels = {
                if (!thumbnail)
                    AxisLabel("$it", Modifier.padding(top = 2.dp))
            },
            xAxisTitle = {
                if (!thumbnail) AxisTitle("Year", modifier = paddingMod)
            },
            yAxisStyle = rememberAxisStyle(minorTickSize = 0.dp),
            yAxisLabels = {
                if (!thumbnail)
                    AxisLabel(
                        (it / PopulationScale).toString(2),
                        Modifier.absolutePadding(right = 2.dp)
                    )
            },
            yAxisTitle = {
                if (!thumbnail)
                    AxisTitle(
                        "Population (Millions)",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
            },
            verticalMajorGridLineStyle = null
        ) {
            VerticalBarChart(
                series = barChartEntries, stacked = true,
                bar = { series, _, value ->
                    DefaultVerticalBar(
                        brush = SolidColor(colors[series]),
                        modifier = Modifier.fillMaxWidth(BarWidth)
                    ) {
                        if (!thumbnail) {
                            HoverSurface {
                                Text("${value.borough}: ${value.population}")
                            }
                        }
                    }
                }
            )
        }
    }
}

private val minorGridLineStyle = LineStyle(
    brush = SolidColor(Color.LightGray),
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f))
)
