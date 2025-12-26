package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.CubicBezierLinePlot
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.line.catmullRomControlPoints
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import kotlin.math.ceil
import kotlin.math.roundToInt

val xyLineSampleView =
    object : SampleView {
        override val name: String = "XY Line"

        override val thumbnail = @Composable {
            ThumbnailTheme {
                LineChartSample(true, name)
            }
        }

        override val content: @Composable () -> Unit = @Composable {
            LineChartSample(false, "Rainfall")
        }
    }

private val colorMap =
    buildMap {
        val colors = generateHueColorPalette(RainData.rainfall.size)
        var i = 0
        RainData.rainfall.forEach {
            put(it.key, colors[i++])
        }
    }

@Composable
private fun LineChartSample(
    thumbnail: Boolean,
    title: String,
) {
    var bezier by remember { mutableStateOf(false) }

    @Suppress("MagicNumber")
    var tau by remember { mutableFloatStateOf(0.5f) }

    Column {
        if (!thumbnail) {
            BezierOptions(
                bezier,
                tau,
                Modifier.padding(KoalaPlotTheme.sizes.gap),
                { bezier = it },
                { tau = it },
            )
            HorizontalDivider()
        }
        XYSamplePlot(thumbnail, title, bezier, tau)
    }
}

@Composable
internal fun BezierOptions(
    bezierOn: Boolean,
    tau: Float,
    modifier: Modifier = Modifier,
    updateBezier: (Boolean) -> Unit,
    updateTau: (Float) -> Unit,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Bezier")
            Switch(bezierOn, onCheckedChange = updateBezier)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tau")
            Slider(
                value = tau,
                onValueChange = updateTau,
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f),
            )
            @Suppress("MagicNumber")
            Text(((tau * 100.0f).roundToInt() / 100f).toString())
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun XYSamplePlot(
    thumbnail: Boolean,
    title: String,
    bezierOn: Boolean,
    tau: Float,
) {
    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM,
    ) {
        XYGraph(
            xAxisModel = CategoryAxisModel(RainData.months),
            yAxisModel =
                FloatLinearAxisModel(
                    0f..(ceil(RainData.max / 50.0) * 50.0).toFloat(),
                    minimumMajorTickSpacing = 50.dp,
                ),
            xAxisContent =
                AxisContent(
                    labels = {
                        if (!thumbnail) {
                            AxisLabel(it, Modifier.padding(top = 2.dp))
                        }
                    },
                    title = {
                        if (!thumbnail) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                AxisTitle("Month")
                            }
                        }
                    },
                    style = rememberAxisStyle(),
                ),
            yAxisContent =
                AxisContent(
                    labels = {
                        if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
                    },
                    title = {
                        if (!thumbnail) {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.TopStart,
                            ) {
                                AxisTitle(
                                    "Rainfall (mm)",
                                    modifier =
                                        Modifier
                                            .rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                                            .padding(bottom = padding),
                                )
                            }
                        }
                    },
                    style = rememberAxisStyle(),
                ),
        ) {
            RainData.rainfall.entries.sortedBy { it.key }.forEach { (city, rain) ->
                if (bezierOn) {
                    BezierChart(
                        city,
                        rain.mapIndexed { index, d ->
                            DefaultPoint(RainData.months[index], d.toFloat())
                        },
                        thumbnail,
                        tau,
                    )
                } else {
                    Chart(
                        city,
                        rain.mapIndexed { index, d ->
                            DefaultPoint(RainData.months[index], d.toFloat())
                        },
                        thumbnail,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun XYGraphScope<String, Float>.Chart(
    city: String,
    data: List<DefaultPoint<String, Float>>,
    thumbnail: Boolean,
) {
    LinePlot2(
        data = data,
        lineStyle =
            LineStyle(
                brush = SolidColor(colorMap[city] ?: Color.Black),
                strokeWidth = 2.dp,
            ),
        symbol = { point ->
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = {
                    if (!thumbnail) {
                        PlainTooltip {
                            Text(point.y.toString())
                        }
                    }
                },
                state = rememberTooltipState(),
            ) {
                Symbol(
                    shape = CircleShape,
                    fillBrush = SolidColor(colorMap[city] ?: Color.Black),
                )
            }
        },
    )
}

@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun XYGraphScope<String, Float>.BezierChart(
    city: String,
    data: List<DefaultPoint<String, Float>>,
    thumbnail: Boolean,
    tau: Float,
) {
    CubicBezierLinePlot(
        data = data,
        lineStyle =
            LineStyle(
                brush = SolidColor(colorMap[city] ?: Color.Black),
                strokeWidth = 2.dp,
            ),
        symbol = { point ->
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = {
                    if (!thumbnail) {
                        PlainTooltip {
                            Text(point.y.toString())
                        }
                    }
                },
                state = rememberTooltipState(),
            ) {
                Symbol(
                    shape = CircleShape,
                    fillBrush = SolidColor(colorMap[city] ?: Color.Black),
                )
            }
        },
        control = catmullRomControlPoints(tau),
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(thumbnail: Boolean = false) {
    val cities = RainData.rainfall.keys.sorted()

    if (!thumbnail) {
        Surface(shadowElevation = 2.dp) {
            FlowLegend(
                itemCount = cities.size,
                symbol = { i ->
                    Symbol(
                        modifier = Modifier.size(padding),
                        fillBrush = SolidColor(colorMap[cities[i]] ?: Color.Black),
                    )
                },
                label = { i ->
                    Text(cities[i])
                },
                modifier = paddingMod,
            )
        }
    }
}
