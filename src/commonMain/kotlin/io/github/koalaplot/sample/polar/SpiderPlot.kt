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
import io.github.koalaplot.core.polar.DefaultPolarPoint
import io.github.koalaplot.core.polar.PolarGraph
import io.github.koalaplot.core.polar.PolarGraphDefaults
import io.github.koalaplot.core.polar.PolarPlotSeries2
import io.github.koalaplot.core.polar.PolarPoint
import io.github.koalaplot.core.polar.RadialGridType
import io.github.koalaplot.core.polar.rememberCategoryAngularAxisModel
import io.github.koalaplot.core.polar.rememberFloatRadialAxisModel
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme.axis
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.sample.ChartTitle
import io.github.koalaplot.sample.SampleView
import io.github.koalaplot.sample.ThumbnailTheme
import io.github.koalaplot.sample.padding
import io.github.koalaplot.sample.paddingMod
import kotlin.random.Random

val spiderPlotSample = object : SampleView {
    override val name: String = "Spider Plot"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            SpiderPlotSample(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        SpiderPlotSample(false, "")
    }
}

// Generate random data
private val categories = listOf("Category 1", "Category 2", "Category 3", "Category 4", "Category 5")
private val seriesNames = listOf("Series 1", "Series 2", "Series 3")
private val data: List<List<PolarPoint<Float, String>>> = buildList {
    seriesNames.forEach { _ ->
        add(
            buildList {
                categories.forEach { category ->
                    add(DefaultPolarPoint(Random.nextDouble(1.0, 5.0).toFloat(), category))
                }
            }
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun SpiderPlotSample(thumbnail: Boolean, title: String) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        val angularAxisGridLineStyle = if (thumbnail) {
            LineStyle(SolidColor(Color.LightGray), strokeWidth = 1.dp)
        } else {
            axis.majorGridlineStyle
        }

        PolarGraph(
            rememberFloatRadialAxisModel((0..5).toList().map { it.toFloat() }),
            rememberCategoryAngularAxisModel(categories),
            radialAxisLabels = { if (!thumbnail) Text(it.toString()) },
            { if (!thumbnail) Text(it) },
            polarGraphProperties = PolarGraphDefaults.PolarGraphPropertyDefaults()
                .copy(
                    radialGridType = RadialGridType.LINES,
                    angularAxisGridLineStyle = angularAxisGridLineStyle,
                    radialAxisGridLineStyle = angularAxisGridLineStyle
                )
        ) {
            data.forEachIndexed { index, seriesData ->
                PolarPlotSeries2(
                    seriesData,
                    lineStyle = LineStyle(SolidColor(palette[index]), strokeWidth = 1.5.dp),
                    areaStyle = AreaStyle(SolidColor(palette[index]), alpha = 0.3f),
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
