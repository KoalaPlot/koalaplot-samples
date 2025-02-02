package io.github.koalaplot.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.CandleStickPlot
import io.github.koalaplot.core.bar.CandleStickPlotEntry
import io.github.koalaplot.core.bar.candleStickPlotEntry
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.*

private val colors = listOf(Color.Green, Color.Red)

private val rotationOptions = listOf(0, 30, 45, 60, 90)

private fun candleStickEntries(): List<CandleStickPlotEntry<Int, Float>> {
    return CandleStickData.dates.mapIndexed { index, date ->
        candleStickPlotEntry(
            x = date,
            open = CandleStickData.open[index],
            close = CandleStickData.close[index],
            high = CandleStickData.high[index],
            low = CandleStickData.low[index]
        )
    }
}

@Composable
private fun Legend(thumbnail: Boolean = false) {
    if (!thumbnail) {
        Surface(shadowElevation = 2.dp) {
            FlowLegend(
                itemCount = 2,
                symbol = { i ->
                    Box(
                        modifier = Modifier
                            .size(padding)
                            .background(color = colors[i])
                    )
                },
                label = { i ->
                    Text(if (i == 0) "Increasing" else "Decreasing")
                },
                modifier = paddingMod
            )
        }
    }
}

val candleStickSampleView = object : SampleView {
    override val name: String = "Candle Stick"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            CandleStickSamplePlot(true, name)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        var selectedOption by remember { mutableStateOf(rotationOptions[0]) }
        KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(minorGridlineStyle = minorGridLineStyle)) {
            Column {
                CandleStickSamplePlot(
                    false,
                    "Stock Price",
                    Modifier.weight(1f),
                    selectedOption
                )
                ExpandableCard(
                    colors = CardDefaults.elevatedCardColors(),
                    elevation = CardDefaults.elevatedCardElevation(),
                    titleContent = {
                        Text("X-Axis Label Angle", modifier = paddingMod)
                    }
                ) {
                    Column {
                        rotationOptions.forEach {
                            Row(
                                Modifier.fillMaxWidth()
                                    .selectable(selected = (it == selectedOption), onClick = { selectedOption = it })
                            ) {
                                RadioButton(selected = (it == selectedOption), onClick = { selectedOption = it })
                                Text(text = it.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun CandleStickSamplePlot(
    thumbnail: Boolean = false,
    title: String,
    modifier: Modifier = Modifier,
    xAxisLabelRotation: Int = 0
) {
    val candleStickEntries = remember { candleStickEntries() }
    var cursorPosition by remember { mutableStateOf<Point<Int, Float>?>(null) }

    ChartLayout(
        modifier = modifier.then(paddingMod),
        title = { ChartTitle(title) },
        legend = { Legend(thumbnail) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = CategoryAxisModel(CandleStickData.dates),
            yAxisModel = FloatLinearAxisModel(CandleStickData.low.min()..CandleStickData.high.max()),
            xAxisStyle = rememberAxisStyle(labelRotation = xAxisLabelRotation),
            xAxisLabels = {
                if (!thumbnail) AxisLabel("$it", Modifier.padding(top = 2.dp))
            },
            xAxisTitle = {
                if (!thumbnail) AxisTitle("Date", modifier = paddingMod)
            },
            yAxisStyle = rememberAxisStyle(minorTickSize = 0.dp),
            yAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
                }
            },
            yAxisTitle = {
                if (!thumbnail) {
                    AxisTitle(
                        "Price",
                        modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                            .padding(bottom = padding)
                    )
                }
            },
            verticalMajorGridLineStyle = null
        ) {
            CandleStickPlot(
                defaultCandle = { entry ->
                    if (!thumbnail) {

                        Box(
                            modifier = Modifier.hoverableElement {
                                val pd = pointerData
                                if (pd != null) {
                                    cursorPosition = Point(pd.x, pd.y)
                                } else {
                                    cursorPosition = null
                                }
                            }
                        ) {

                            HoverSurface {
                                Column(Modifier.padding(1.dp).background(Color.White)) {
                                    Text("Close: ${entry.close}")
                                }
                            }
                        }
                    }
                },
            ) {
                candleStickEntries.forEach { entry ->
                    item(entry)
                }
            }

            pointerData?.let {
                HorizontalLineAnnotation(it.y, LineStyle(SolidColor(Color.Red)))
                XYAnnotation(Point(it.x, it.y), AnchorPoint.TopLeft) {
                    Text("Price: ${it.y.toString()}")
                }
            }
        }
    }
}

private val minorGridLineStyle = LineStyle(
    brush = SolidColor(Color.LightGray),
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f))
)

object CandleStickData {
    val dates = listOf(20230501, 20230502, 20230503, 20230504, 20230505)
    val open = listOf(100f, 150f, 120f, 200f, 180f)
    val close = listOf(120f, 130f, 180f, 190f, 160f)
    val high = listOf(130f, 160f, 200f, 210f, 190f)
    val low = listOf(90f, 110f, 100f, 170f, 150f)
}
