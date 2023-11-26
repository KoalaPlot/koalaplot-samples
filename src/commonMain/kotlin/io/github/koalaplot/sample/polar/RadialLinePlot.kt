package io.github.koalaplot.sample.polar

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import io.github.koalaplot.core.polar.DefaultPolarPoint
import io.github.koalaplot.core.polar.PolarGraph
import io.github.koalaplot.core.polar.PolarGraphDefaults
import io.github.koalaplot.core.polar.PolarPlotSeries
import io.github.koalaplot.core.polar.PolarPoint
import io.github.koalaplot.core.polar.rememberCategoryAngularAxisModel
import io.github.koalaplot.core.polar.rememberFloatRadialAxisModel
import io.github.koalaplot.core.style.KoalaPlotTheme.axis
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.sample.ChartTitle
import io.github.koalaplot.sample.HoverSurface
import io.github.koalaplot.sample.PopulationData
import io.github.koalaplot.sample.SampleView
import io.github.koalaplot.sample.ThumbnailTheme
import io.github.koalaplot.sample.padding
import io.github.koalaplot.sample.paddingMod

val radialLinePlotSample = object : SampleView {
    override val name: String = "Radial Line Plot"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            RadialLinePlotSample(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        RadialLinePlotSample(false, "Population (Millions)")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun RadialLinePlotSample(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        val ram = rememberFloatRadialAxisModel(listOf(0f, 0.5f, 1f, 1.5f, 2f, 2.5f, 3f)) // population in millions
        val aam = rememberCategoryAngularAxisModel(PopulationData.years)

        val angularAxisGridLineStyle = if (thumbnail) {
            LineStyle(SolidColor(Color.LightGray), strokeWidth = 1.dp)
        } else {
            axis.majorGridlineStyle
        }

        PolarGraph(
            ram,
            aam,
            radialAxisLabels = { if (!thumbnail) Text(it.toString(1)) },
            { if (!thumbnail) Text(it.toString()) },
            polarGraphProperties = PolarGraphDefaults.PolarGraphPropertyDefaults()
                .copy(
                    angularAxisGridLineStyle = angularAxisGridLineStyle,
                    radialAxisGridLineStyle = angularAxisGridLineStyle
                )
        ) {
            PopulationData.data.forEach { (category, data) ->
                PolarPlotSeries(
                    object : AbstractList<PolarPoint<Float, Int>>() {
                        override val size: Int
                            get() = PopulationData.years.size

                        override fun get(index: Int): PolarPoint<Float, Int> {
                            return DefaultPolarPoint(data[index] / 1E6f, PopulationData.years[index])
                        }
                    },
                    lineStyle = LineStyle(SolidColor(colorMap[category]!!), strokeWidth = 1.dp),
                    symbols = {
                        Symbol(
                            shape = CircleShape,
                            fillBrush = SolidColor(colorMap[category]!!),
                            modifier = Modifier.then(
                                if (!thumbnail) {
                                    Modifier.hoverableElement {
                                        HoverSurface {
                                            Text(
                                                "${(it.r * 1E6).toInt()}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                } else {
                                    Modifier
                                }
                            )
                        )
                    }
                )
            }
        }
    }
}

private val colorMap = buildMap {
    val colors = generateHueColorPalette(PopulationData.data.size)
    var i = 0
    PopulationData.data.forEach {
        put(it.key, colors[i++])
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    val cities = PopulationData.data.keys.sorted()

    if (!thumbnail) {
        Surface(shadowElevation = 2.dp) {
            FlowLegend(
                itemCount = cities.size,
                symbol = { i ->
                    Symbol(
                        shape = CircleShape,
                        modifier = Modifier.size(padding),
                        fillBrush = SolidColor(colorMap[cities[i]] ?: Color.Black)
                    )
                },
                label = { i ->
                    Text(cities[i].display)
                },
                modifier = paddingMod
            )
        }
    }
}
