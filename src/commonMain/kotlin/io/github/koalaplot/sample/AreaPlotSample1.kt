package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.AreaPlot
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.AnchorPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.HorizontalLineAnnotation
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.VerticalLineAnnotation
import io.github.koalaplot.core.xygraph.XYAnnotation
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

val areaPlotSample1View = object : SampleView {
    override val name: String = "Areas to x-axis"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            AreaPlotSample1Plot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        AreaPlotSample1Plot(false, "Normal Distributions")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun AreaPlotSample1Plot(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod.padding(end = 16.dp),
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = FloatLinearAxisModel(-5f..5.0f),
            yAxisModel = FloatLinearAxisModel(0f..1.0f, minimumMajorTickSpacing = 50.dp),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(1), Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = {
                if (!thumbnail) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AxisTitle("x")
                    }
                }
            },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(1), Modifier.absolutePadding(right = 2.dp))
            },
            horizontalMinorGridLineStyle = null,
            verticalMinorGridLineStyle = null
        ) {
            AreaPlot(
                data = distribution1,
                lineStyle = LineStyle(brush = SolidColor(Color(0xFF00498F)), strokeWidth = 2.dp),
                areaStyle = AreaStyle(
                    brush = SolidColor(Color(0xFF00498F)),
                    alpha = 0.5f,
                ),
                areaBaseline = AreaBaseline.ConstantLine(0f)
            )
            AreaPlot(
                data = distribution2,
                lineStyle = LineStyle(brush = SolidColor(Color(0xFF37A78F)), strokeWidth = 2.dp),
                areaStyle = AreaStyle(
                    brush = SolidColor(Color(0xFF37A78F)),
                    alpha = 0.5f,
                ),
                areaBaseline = AreaBaseline.ConstantLine(0f)
            )

            if (!thumbnail) {
                Annotations()
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun XYGraphScope<Float, Float>.Annotations() {
    val markerLineStyle = LineStyle(
        brush = SolidColor(Color.DarkGray),
        strokeWidth = 2.dp,
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )

    VerticalLineAnnotation(Distribution1Mean, markerLineStyle)
    VerticalLineAnnotation(Distribution2Mean, markerLineStyle)
    HorizontalLineAnnotation(distribution1Max, markerLineStyle)
    HorizontalLineAnnotation(distribution2Max, markerLineStyle)

    XYAnnotation(Point(-5f, distribution1Max), AnchorPoint.BottomLeft) {
        Text(
            "y=${distribution1Max.toString(2)}",
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = KoalaPlotTheme.sizes.gap)
        )
    }
    XYAnnotation(Point(-5f, distribution2Max), AnchorPoint.BottomLeft) {
        Text(
            "y=${distribution2Max.toString(2)}",
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = KoalaPlotTheme.sizes.gap)
        )
    }

    XYAnnotation(Point(Distribution1Mean, 1f), AnchorPoint.TopLeft) {
        Text(
            "x=${Distribution1Mean.toString(2)}",
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = KoalaPlotTheme.sizes.gap)
        )
    }

    XYAnnotation(Point(Distribution2Mean, 1f), AnchorPoint.TopRight) {
        Text(
            "x=${Distribution2Mean.toString(2)}",
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = KoalaPlotTheme.sizes.gap)
        )
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

private const val Distribution1Mean = 1.2f
private val distribution1 = normalDistribution(xAxisValues, 1.0f, Distribution1Mean)
private val distribution1Max = distribution1.maxOf { it.y }

private const val Distribution2Mean = -0.4f

@Suppress("MagicNumber")
private val distribution2 = normalDistribution(xAxisValues, 0.5f, Distribution2Mean)
private val distribution2Max = distribution2.maxOf { it.y }
