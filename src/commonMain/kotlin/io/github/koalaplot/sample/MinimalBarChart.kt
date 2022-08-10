package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.BarChartEntry
import io.github.koalaplot.core.bar.VerticalBarChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xychart.CategoryAxisModel
import io.github.koalaplot.core.xychart.LinearAxisModel
import io.github.koalaplot.core.xychart.XYChart
import io.github.koalaplot.core.xychart.autoScaleRange

private val BOROUGH = PopulationData.Categories.Manhattan

private fun barChartEntries(): List<List<BarChartEntry<Int, Float>>> {
    return listOf(
        PopulationData.data[BOROUGH]!!.mapIndexed { index, population ->
            object : BarChartEntry<Int, Float> {
                override val xValue = PopulationData.years[index]
                override val yMin = 0f
                override val yMax = population.toFloat()
            }
        }
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
val minimalBarChartSampleView = object : SampleView {
    override val name: String = "Simple Vertical Bar"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            MinimalBarChartThumbnail()
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        Column {
            ChartLayout(
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f)
            ) {
                MinimalBarChart()
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun MinimalBarChart() {
    val barChartEntries = remember { barChartEntries() }

    XYChart(
        xAxisModel = CategoryAxisModel(PopulationData.years),
        yAxisModel = LinearAxisModel(PopulationData.data[BOROUGH]!!.autoScaleRange()),
        xAxisTitle = "Year",
        yAxisTitle = "Population"
    ) {
        VerticalBarChart(series = barChartEntries)
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun MinimalBarChartThumbnail() {
    val barChartEntries = remember { barChartEntries() }

    XYChart(
        xAxisModel = CategoryAxisModel(PopulationData.years),
        yAxisModel = LinearAxisModel(
            PopulationData.data[BOROUGH]!!.autoScaleRange()
        ),
        xAxisTitle = "Year",
        yAxisTitle = "Population",
        xAxisLabels = { "" },
        yAxisLabels = { "" }
    ) {
        VerticalBarChart(series = barChartEntries)
    }
}
