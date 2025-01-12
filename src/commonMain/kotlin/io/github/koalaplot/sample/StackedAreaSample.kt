package io.github.koalaplot.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.StackedAreaPlot
import io.github.koalaplot.core.line.StackedAreaPlotDataAdapter
import io.github.koalaplot.core.line.StackedAreaPlotEntry
import io.github.koalaplot.core.line.StackedAreaStyle
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.AnchorPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.IntLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYAnnotation
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope

val stackedAreaSampleView = object : SampleView {
    override val name: String = "Stacked Area Chart"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            StackedAreaSample(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        StackedAreaSample(false, "New York City Population")
    }
}

@Suppress("MagicNumber")
private val colorPalette = listOf(
    Color(0xFF00498F),
    Color(0xFF37A78F),
    Color(0xFFC05050),
    Color(0xFFED7D31),
    Color(0xFF8068A0)
)

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun StackedAreaSample(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod.padding(end = 16.dp),
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = IntLinearAxisModel(
                PopulationData.years.first()..PopulationData.years.last(),
                minimumMajorTickIncrement = 10
            ),
            yAxisModel = FloatLinearAxisModel(0f..10f),
            horizontalMajorGridLineStyle = null,
            horizontalMinorGridLineStyle = null,
            verticalMajorGridLineStyle = null,
            verticalMinorGridLineStyle = null,
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(), Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = {
                if (!thumbnail) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AxisTitle("Year")
                    }
                }
            },
            yAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(0))
                }
            },
            yAxisTitle = {
                if (!thumbnail) {
                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        AxisTitle(
                            "Population (Millions)",
                            modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                        )
                    }
                }
            }
        ) {
            StackedAreaPlot(
                stackedAreaData,
                colorPalette.map {
                    StackedAreaStyle(
                        LineStyle(brush = SolidColor(Color.White), strokeWidth = 8.dp),
                        AreaStyle(brush = SolidColor(it))
                    )
                },
                AreaBaseline.ConstantLine(0f)
            )

            annotations(thumbnail)
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun XYGraphScope<Int, Float>.annotations(thumbnail: Boolean) {
    if (!thumbnail) {
        val entries = PopulationData.data.entries.toList()
        val max = entries.map { it.value.max() }
        val maxIndices = entries.mapIndexed { index, entry ->
            entry.value.indexOfFirst { it == max[index] }
        }

        entries.forEachIndexed { index, (category, data) ->
            val yearIndex = maxIndices[index] // index into the year the max occurred

            var sum = 0
            for (i in 0..<index) {
                sum += entries[i].value[yearIndex]
            }

            val anchorPoint = when (yearIndex) {
                0 -> AnchorPoint.LeftMiddle
                PopulationData.years.lastIndex -> AnchorPoint.RightMiddle
                else -> AnchorPoint.Center
            }

            XYAnnotation(
                Point(PopulationData.years[yearIndex], (sum + data[yearIndex] / 2f) / 1E6f),
                anchorPoint
            ) {
                Text(
                    category.display,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = KoalaPlotTheme.sizes.gap)
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = KoalaPlotTheme.sizes.gap)
                )
            }
        }
    }
}

private val stackedAreaData: List<StackedAreaPlotEntry<Int, Float>> by lazy {
    StackedAreaPlotDataAdapter(
        PopulationData.years,
        PopulationData.data.values.map {
            it.map { it.toFloat() / 1E6f }
        }
    )
}
