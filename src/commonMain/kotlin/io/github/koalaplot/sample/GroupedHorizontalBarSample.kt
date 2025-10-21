package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.BarPlotGroupedPointEntry
import io.github.koalaplot.core.bar.BarPosition
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.DefaultBarPosition
import io.github.koalaplot.core.bar.GroupedHorizontalBarPlot
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import kotlin.math.ceil

private val colors = generateHueColorPalette(PopulationData.Categories.entries.size)

private fun barChartEntries(): List<BarPlotGroupedPointEntry<Int, Float>> {
    return PopulationData.years.mapIndexed { yearIndex, year ->
        object : BarPlotGroupedPointEntry<Int, Float> {
            override val i: Int = year

            override val d: List<BarPosition<Float>> = object : AbstractList<BarPosition<Float>>() {
                override val size: Int
                    get() = PopulationData.Categories.entries.size

                override fun get(index: Int): BarPosition<Float> {
                    return DefaultBarPosition(
                        0f,
                        PopulationData.data[PopulationData.Categories.entries[index]]!![yearIndex].toFloat()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    if (!thumbnail) {
        FlowLegend(
            itemCount = PopulationData.Categories.entries.size,
            symbol = { i ->
                Symbol(modifier = Modifier.size(padding), fillBrush = SolidColor(colors[i]))
            },
            label = { i ->
                Text(PopulationData.Categories.entries[i].toString())
            },
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
val groupedHorizontalBarSampleView = object : SampleView {
    override val name: String = "Grouped Horizontal Bar"

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
    val barChartEntries: List<BarPlotGroupedPointEntry<Int, Float>> = remember(thumbnail) { barChartEntries() }

    ChartLayout(
        // modifier = paddingMod,
        modifier = paddingMod.padding(end = 16.dp),
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            yAxisModel = CategoryAxisModel(PopulationData.years),
            xAxisModel = FloatLinearAxisModel(
                0f..(ceil(PopulationData.maxPopulation / PopulationScale) * PopulationScale),
                minorTickCount = 0
            ),
            yAxisLabels = {
                if (!thumbnail) AxisLabel("$it", Modifier.padding(top = 2.dp))
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "Year",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            },
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(
                        (it / PopulationScale).toString(2),
                    )
                }
            },
            xAxisTitle = {
                if (!thumbnail) {
                    Text(
                        "Population (Millions)",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
            horizontalMajorGridLineStyle = null
        ) {
            GroupedHorizontalBarPlot(
                data = barChartEntries,
                bar = { dataIndex, groupIndex, _ ->
                    DefaultBar(
                        brush = SolidColor(colors[groupIndex]),
                        // modifier = Modifier.sizeIn(minWidth = 5.dp, maxWidth = 20.dp),
                    ) {
                        if (!thumbnail) {
                            val borough = PopulationData.Categories.entries[groupIndex]
                            val pop = PopulationData.data[borough]!![dataIndex]
                            HoverSurface { Text("$borough: $pop") }
                        }
                    }
                }
            )
        }
    }
}
