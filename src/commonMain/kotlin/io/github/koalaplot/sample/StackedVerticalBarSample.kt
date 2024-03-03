package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.StackedVerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlotStackedPointEntry
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.LinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle

private val colors = generateHueColorPalette(PopulationData.Categories.entries.size)

private const val BarWidth = 0.8f

private val rotationOptions = listOf(0, 30, 45, 60, 90)

private fun barChartEntries(): List<VerticalBarPlotStackedPointEntry<Float, Float>> {
    return PopulationData.years.mapIndexed { yearIndex, year ->
        object : VerticalBarPlotStackedPointEntry<Float, Float> {
            override val x: Float = year
            override val yOrigin: Float = 0f

            override val y: List<Float> = object : AbstractList<Float>() {
                override val size: Int
                    get() = PopulationData.Categories.entries.size

                override fun get(index: Int): Float {
                    return PopulationData.Categories.entries.subList(0, index + 1).fold(0f) { acc, cat ->
                        acc + PopulationData.data[cat]!![yearIndex]
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    if (!thumbnail) {
        Surface(shadowElevation = 2.dp) {
            FlowLegend(
                itemCount = PopulationData.Categories.entries.size,
                symbol = { i ->
                    Symbol(modifier = Modifier.size(padding), fillBrush = SolidColor(colors[i]))
                },
                label = { i ->
                    Text(PopulationData.Categories.entries[i].toString())
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
        var selectedOption by remember { mutableStateOf(rotationOptions[0]) }
        KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(minorGridlineStyle = minorGridLineStyle)) {
            Column {
                StackedBarSamplePlot(
                    false,
                    "New York City Population",
                    Modifier.weight(1f),
                    selectedOption
                )
                ExpandableCard(
                    colors = CardDefaults.elevatedCardColors(),
                    elevation = CardDefaults.elevatedCardElevation(),
                    titleContent = {
                        Text("X-Axis Label Angle", modifier = paddingMod)
                    }
                ) {
                    Column {
                        rotationOptions.forEach {
                            Row(
                                Modifier.fillMaxWidth()
                                    .selectable(selected = (it == selectedOption), onClick = { selectedOption = it })
                            ) {
                                RadioButton(selected = (it == selectedOption), onClick = { selectedOption = it })
                                Text(text = it.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val PopulationScale = 1E6

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun StackedBarSamplePlot(
    thumbnail: Boolean = false,
    title: String,
    modifier: Modifier = Modifier,
    xAxisLabelRotation: Int = 0
) {
    val barChartEntries = remember { barChartEntries() }

    ChartLayout(
        modifier = modifier.then(paddingMod),
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        @Suppress("MagicNumber")
        XYGraph(
            xAxisModel = CategoryAxisModel(PopulationData.years),
            yAxisModel = LinearAxisModel(0f..10000000f, minimumMajorTickIncrement = 1000000f),
            xAxisStyle = rememberAxisStyle(labelRotation = xAxisLabelRotation),
            xAxisLabels = {
                if (!thumbnail) AxisLabel("$it", Modifier.padding(top = 2.dp))
            },
            xAxisTitle = {
                if (!thumbnail) AxisTitle("Year", modifier = paddingMod)
            },
            yAxisStyle = rememberAxisStyle(minorTickSize = 0.dp),
            yAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(
                        (it / PopulationScale).toString(2),
                        Modifier.absolutePadding(right = 2.dp)
                    )
                }
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "Population (Millions)",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            },
            verticalMajorGridLineStyle = null
        ) {
            StackedVerticalBarPlot(
                barChartEntries,
                barWidth = BarWidth,
                bar = { xIndex, barIndex ->
                    DefaultVerticalBar(
                        brush = SolidColor(colors[barIndex]),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (!thumbnail) {
                            HoverSurface {
                                val borough = PopulationData.Categories.entries[barIndex]
                                val pop = PopulationData.data[borough]!![xIndex]
                                Text("$borough: $pop")
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
