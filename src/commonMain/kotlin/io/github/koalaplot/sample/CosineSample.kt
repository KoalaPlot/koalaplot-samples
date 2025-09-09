package io.github.koalaplot.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.AreaPlot
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.AnchorPoint
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.HorizontalLineAnnotation
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.VerticalLineAnnotation
import io.github.koalaplot.core.xygraph.XYAnnotation
import io.github.koalaplot.core.xygraph.XYGraph
import kotlin.math.PI
import kotlin.math.cos

val trigSampleView = object : SampleView {
    override val name: String = "Area to x-axis"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            CosineSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        CosineSamplePlot(false, "Cosine")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun CosineSamplePlot(thumbnail: Boolean, title: String) {
    val x: MutableState<Float?> = remember { mutableStateOf(null as Float?) }
    val y: MutableState<Float?> = remember { mutableStateOf(null as Float?) }

    ChartLayout(
        modifier = paddingMod.padding(end = 16.dp),
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = FloatLinearAxisModel(0f..4.0.toFloat()), // units of PI
            yAxisModel = FloatLinearAxisModel(-1.1f..1.1f, minimumMajorTickSpacing = 50.dp),
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel("$it \u03C0", Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = { if (!thumbnail) AxisTitle("Angle (radians)") },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "cosine",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            },
            onPointerMove = { xp: Float, yp: Float ->
                x.value = xp
                y.value = interpolate(xp)
            }
        ) {
            AreaPlot(
                data = cosineData,
                lineStyle = LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 2.dp),
                areaStyle = AreaStyle(
                    brush = SolidColor(Color.Blue),
                    alpha = 0.5f,
                ),
                areaBaseline = AreaBaseline.ConstantLine(0f)
            )
            val lx = x.value
            val ly = y.value
            lx?.let {
                VerticalLineAnnotation(it, lineStyle = LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 1.dp))
                XYAnnotation(
                    Point(it, -1.1f),
                    AnchorPoint.BottomCenter
                ) {
                    Text((it * PI).toString())
                }
            }

            ly?.let {
                HorizontalLineAnnotation(it, LineStyle(brush = SolidColor(Color.Blue), strokeWidth = 1.dp))
                XYAnnotation(
                    Point(0f, it),
                    AnchorPoint.BottomLeft
                ) {
                    Text(it.toString())
                }
            }
            if (lx != null && ly != null) {
                XYAnnotation(
                    Point(lx, ly),
                    AnchorPoint.Center
                ) {
                    Box(modifier = Modifier.clip(CircleShape).size(8.dp).background(Color.Cyan)) {
                    }
                }
            }
        }
    }
}

/**
 * Finds the y-value from [cosineData] for the given x-axis value. Interpolates between the two closest
 * points if needed.
 */
private fun interpolate(xp: Float): Float {
    val i = cosineData.binarySearch { point ->
        point.x.compareTo(xp)
    }
    return if (i < 0) {
        val m = -i - 1

        when (m) {
            0 -> cosineData[m].y
            cosineData.lastIndex -> cosineData[m].y
            else -> {
                // interpolation between points
                val l = m - 1
                val h = m + 1
                val slope = (cosineData[h].y - cosineData[l].y) / (cosineData[h].x - cosineData[l].x)
                (cosineData[l].y + slope * (xp - cosineData[l].x))
            }
        }
    } else {
        cosineData[i].y
    }
}

@Suppress("MagicNumber")
private val cosineData: List<Point<Float, Float>> = buildList {
    for (i in 0..500) {
        add(DefaultPoint((i * 4.0 / 500.0).toFloat(), cos(i * 4.0 * PI / 500.0).toFloat()))
    }
}
