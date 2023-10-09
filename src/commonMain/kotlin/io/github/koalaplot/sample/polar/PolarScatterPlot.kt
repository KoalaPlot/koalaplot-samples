@file:Suppress("MagicNumber")

package io.github.koalaplot.sample.polar

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import io.github.koalaplot.core.polar.AngularValueAxisModel
import io.github.koalaplot.core.polar.DefaultPolarPoint
import io.github.koalaplot.core.polar.PolarPlot
import io.github.koalaplot.core.polar.PolarPlotDefaults
import io.github.koalaplot.core.polar.PolarPlotSeries
import io.github.koalaplot.core.polar.PolarPoint
import io.github.koalaplot.core.polar.RadialAxisModel
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.AngularValue
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.deg
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toDegrees
import io.github.koalaplot.sample.ChartTitle
import io.github.koalaplot.sample.SampleView
import io.github.koalaplot.sample.ThumbnailTheme
import io.github.koalaplot.sample.padding
import io.github.koalaplot.sample.paddingMod
import kotlin.random.Random

val polarScatterPlotSample = object : SampleView {
    override val name: String = "Polar Scatter"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            PolarScatterPlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        PolarScatterPlot(false, "")
    }
}

// Generate random data
private val seriesNames = listOf("Series 1", "Series 2", "Series 3")
private val data: List<List<PolarPoint<Float, AngularValue>>> = buildList {
    seriesNames.forEach { _ ->
        add(
            buildList {
                for (i in 1..10) {
                    add(DefaultPolarPoint(Random.nextDouble(1.0, 10.0).toFloat(), Random.nextDouble(0.0, 360.0).deg))
                }
            }
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun PolarScatterPlot(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        val angularAxisGridLineStyle = if (thumbnail) {
            LineStyle(SolidColor(Color.LightGray), strokeWidth = 1.dp)
        } else {
            LineStyle(SolidColor(Color.LightGray), strokeWidth = 1.dp)
        }

        PolarPlot(
            RadialAxisModel(listOf(0f, 5f, 10f)),
            AngularValueAxisModel(),
            { if (!thumbnail) Text(it.toString()) },
            { if (!thumbnail) Text("${it.toDegrees().value}\u00B0") },
            polarPlotProperties = PolarPlotDefaults.PolarPlotPropertyDefaults()
                .copy(
                    angularAxisGridLineStyle = angularAxisGridLineStyle,
                    radialAxisGridLineStyle = angularAxisGridLineStyle,
                    background = AreaStyle(
                        SolidColor(Color.Yellow),
                        alpha = 0.1f
                    )
                )
        ) {
            data.forEachIndexed { index, seriesData ->
                PolarPlotSeries(
                    seriesData,
                    symbols = {
                        Symbol(shape = CircleShape, fillBrush = SolidColor(palette[index]))
                    }
                )
            }
        }
    }
}

private val palette = generateHueColorPalette(seriesNames.size)

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    if (!thumbnail) {
        Surface(shadowElevation = 2.dp) {
            FlowLegend(
                itemCount = seriesNames.size,
                symbol = { i ->
                    Symbol(
                        shape = CircleShape,
                        modifier = Modifier.size(padding),
                        fillBrush = SolidColor(palette[i])
                    )
                },
                label = { i ->
                    Text(seriesNames[i])
                },
                modifier = paddingMod
            )
        }
    }
}
