package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarChart
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xychart.CategoryAxisModel
import io.github.koalaplot.core.xychart.LinearAxisModel
import io.github.koalaplot.core.xychart.XYChart
import kotlin.math.ceil

private val colors = generateHueColorPalette(PopulationData.Categories.values().size)

private fun barChartEntries(): List<List<PopulationBarChartEntry<Int, Float>>> {
    return PopulationData.Categories.values().map { borough ->
        PopulationData.data[borough]!!.mapIndexed { index, population ->
            PopulationBarChartEntry(
                xValue = PopulationData.years[index],
                yMin = 0f,
                yMax = population.toFloat(),
                borough,
                population
            )
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    if (!thumbnail) {
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
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
val groupedVerticalBarSampleView = object : SampleView {
    override val name: String = "Grouped Vertical Bar"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            BarSample2Plot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        Column {
            ChartLayout(
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f)
            ) {
                BarSample2Plot(false, "New York City Population")
            }
        }
    }
}

private const val PopulationScale = 1E6f

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun BarSample2Plot(thumbnail: Boolean, title: String) {
    val barChartEntries = remember(thumbnail) { barChartEntries() }

    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYChart(
            xAxisModel = CategoryAxisModel(PopulationData.years),
            yAxisModel = LinearAxisModel(
                0f..(ceil(PopulationData.maxPopulation / PopulationScale) * PopulationScale),
                minorTickCount = 0
            ),
            content = {
                VerticalBarChart(
                    series = barChartEntries,
                    bar = { series, _, value ->
                        DefaultVerticalBar(
                            brush = SolidColor(colors[series]),
                            modifier = Modifier.sizeIn(minWidth = 5.dp, maxWidth = 20.dp),
                        ) {
                            if (!thumbnail) {
                                HoverSurface { Text("${value.borough}: ${value.population}") }
                            }
                        }
                    }
                )
            },
            xAxisLabels = {
                if (!thumbnail)
                    AxisLabel("$it", Modifier.padding(top = 2.dp))
            },
            xAxisTitle = {
                if (!thumbnail) AxisTitle("Year")
            },
            yAxisLabels = {
                if (!thumbnail)
                    AxisLabel(
                        (it / PopulationScale).toString(2),
                        Modifier.absolutePadding(right = 2.dp)
                    )
            },
            yAxisTitle = {
                if (!thumbnail)
                    Text(
                        "Population (Millions)",
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding),
                    )
            },
            verticalMajorGridLineStyle = null
        )
    }
}
