package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.BarPlotEntry
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.DefaultVerticalBarPosition
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.LinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private val BOROUGH = PopulationData.Categories.Manhattan

private fun barChartEntries(): List<BarPlotEntry<Int, Float>> {
    var last = 0f

    return PopulationData.data[BOROUGH]!!.mapIndexed { index, population ->
        val entry = if (index == PopulationData.data[BOROUGH]!!.lastIndex) {
            object : BarPlotEntry<Int, Float> {
                override val x = PopulationData.years[index]
                override val y = DefaultVerticalBarPosition(0f, population.toFloat())
            }
        } else {
            object : BarPlotEntry<Int, Float> {
                override val x = PopulationData.years[index]
                override val y = DefaultVerticalBarPosition(
                    min(last, population.toFloat()),
                    max(last, population.toFloat())
                )
            }
        }

        last = population.toFloat()
        entry
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
val waterfallChartSampleView = object : SampleView {
    override val name: String = "Waterfall Chart"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            ChartLayout(title = { Text(name) }) {
                WaterfallChart(true)
            }
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        Column {
            ChartLayout(
                title = {
                    Text(
                        "Population Changes in Manhattan Over 70 Years",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f)
            ) {
                WaterfallChart(false)
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun WaterfallChart(thumbnail: Boolean) {
    val barChartEntries = remember { barChartEntries() }

    @Suppress("MagicNumber")
    val p = ceil(PopulationData.data[BOROUGH]!!.last() / 1E6f) * 1E6f

    XYGraph(
        xAxisModel = CategoryAxisModel(PopulationData.years),
        yAxisModel = LinearAxisModel(0f..p),
        xAxisTitle = "Year",
        yAxisTitle = "Population",
        xAxisLabels = {
            if (!thumbnail) {
                it.toString()
            } else {
                ""
            }
        },
        yAxisLabels = {
            if (!thumbnail) {
                it.toString(0)
            } else {
                ""
            }
        },
        verticalMajorGridLineStyle = null,
        verticalMinorGridLineStyle = null,
        horizontalMinorGridLineStyle = null,
        horizontalMajorGridLineStyle = null,
    ) {
        @Suppress("MagicNumber")
        VerticalBarPlot(
            barChartEntries,
            bar = { index ->
                val color = when {
                    index == 0 -> SolidColor(Color(0xFF00498F))
                    index == PopulationData.data[BOROUGH]!!.lastIndex -> SolidColor(Color(0xFF00498F))
                    PopulationData.data[BOROUGH]!![index] > PopulationData.data[BOROUGH]!![index - 1] -> SolidColor(
                        Color(0xFF37A78F)
                    )

                    PopulationData.data[BOROUGH]!![index] < PopulationData.data[BOROUGH]!![index - 1] -> SolidColor(
                        Color(0xFFED7D31)
                    )

                    else -> SolidColor(Color.Black) // Shouldn't happen
                }
                DefaultVerticalBar(brush = color, modifier = Modifier.fillMaxWidth())
            }
        )
    }
}
