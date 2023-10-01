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
import io.github.koalaplot.core.line.AreaStyle
import io.github.koalaplot.core.line.DefaultMultiPoint
import io.github.koalaplot.core.line.Point
import io.github.koalaplot.core.line.StackedAreaChart
import io.github.koalaplot.core.line.StackedAreaStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xychart.LineStyle
import io.github.koalaplot.core.xychart.LinearAxisModel
import io.github.koalaplot.core.xychart.XYChart
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

val stackedAreaSampleView = object : SampleView {
    override val name: String = "Stacked Area Chart"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            StackedAreaSample(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        StackedAreaSample(false, "Stacked Normal Distributions")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun StackedAreaSample(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYChart(
            xAxisModel = LinearAxisModel(-5f..5.0f),
            yAxisModel = LinearAxisModel(-0.1f..1.0f, minimumMajorTickSpacing = 50.dp),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(1), Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = { if (!thumbnail) AxisTitle("x") },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(1), Modifier.absolutePadding(right = 2.dp))
            }
        ) {
            StackedAreaChart(
                stackedAreaData,
                listOf(
                    StackedAreaStyle(
                        LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 2.dp),
                        AreaStyle(brush = SolidColor(Color.Blue), alpha = 0.5f)
                    ),
                    StackedAreaStyle(
                        LineStyle(brush = SolidColor(Color.Green), strokeWidth = 2.dp),
                        AreaStyle(brush = SolidColor(Color.Green), alpha = 0.5f)
                    )
                ),
                AreaBaseline.ConstantLine(0f)
            )
        }
    }
}

@Suppress("MagicNumber")
private val xAxisValues: List<Float> = buildList {
    val numSamples = 500
    val min = -5f
    val max = 5f

    for (i in 0..numSamples) {
        add(min + (max - min) * i / numSamples)
    }
}

@Suppress("MagicNumber")
private fun normalDistribution(x: List<Float>, sigma: Float, mu: Float): List<Point<Float, Float>> = buildList {
    x.forEach {
        add(Point(it, normalDistribution(it, sigma, mu)))
    }
}

@Suppress("MagicNumber")
private fun normalDistribution(x: Float, sigma: Float, mu: Float): Float =
    (1.0 / (sigma * sqrt(2.0 * PI)) * exp(-0.5 * ((x - mu) / sigma).pow(2))).toFloat()

private val curve1 = normalDistribution(xAxisValues, 1.0f, 1.0f)

@Suppress("MagicNumber")
private val curve2 = normalDistribution(xAxisValues, 1.5f, -0.5f)

private val stackedAreaData = buildList {
    xAxisValues.forEachIndexed { index, fl ->
        add(DefaultMultiPoint(fl, listOf(curve1[index].y, curve2[index].y)))
    }
}
