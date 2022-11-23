package io.github.koalaplot.sample

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.ColumnLegend
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.pie.BezierLabelConnector
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.pie.StraightLineConnector
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.ResponsiveText
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toString

private val colors = generateHueColorPalette(fibonacci.size)
private val LabelSpacingSliderRange = 1.05f..1.5f
private val HoleSizeRange = 0f..0.9f

private val strokes = buildList {
    add(Stroke(width = 1f))
    add(Stroke(width = 2f))
    add(Stroke(width = 3f))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))))
}

private data class ConnectorStyleState(
    val strokeStyle: Stroke,
    val straightLine: Boolean
)

private data class OtherOptionsState(
    val showLabels: Boolean = false,
    val holeSize: Float = 0.0f,
    val labelSpacing: Float = 1.1f
)

val pieSampleView = object : SampleView {
    override val name: String = "Pie Chart"

    override val thumbnail = @Composable {
        PieChartThumbnail()
    }

    override val content: @Composable () -> Unit = @Composable {
        var legendLocation by remember { mutableStateOf(LegendLocation.LEFT) }
        var connectorStyle by remember {
            mutableStateOf(
                ConnectorStyleState(
                    strokeStyle = strokes[0],
                    straightLine = false
                )
            )
        }
        var otherOptions by remember { mutableStateOf(OtherOptionsState()) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PieChartSample(
                legendLocation,
                connectorStyle,
                otherOptions,
                modifier = Modifier.sizeIn(200.dp, 200.dp, 600.dp, 600.dp).weight(1.0f),
            )
            Divider(modifier = Modifier.fillMaxWidth())
            LegendPositionSelector(legendLocation) {
                legendLocation = it
            }
            ConnectorStyleSelector(connectorStyle) {
                connectorStyle = it
            }
            OtherOptions(
                otherOptions,
                onShowLabels = {
                    otherOptions = otherOptions.copy(showLabels = it)
                },
                onHoleSize = {
                    otherOptions = otherOptions.copy(holeSize = it)
                },
                onLabelSpacing = {
                    otherOptions = otherOptions.copy(labelSpacing = it)
                }
            )
        }
    }
}

@Composable
fun LegendPositionSelector(value: LegendLocation, onSelection: (LegendLocation) -> Unit) {
    ExpandableCard(
        elevation = 2.dp,
        modifier = paddingMod,
        titleContent = { Text("Legend Location", modifier = paddingMod) }
    ) {
        Column {
            LegendLocation.values().forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(it == value, onClick = { onSelection(it) })
                    Text(it.name)
                }
            }
        }
    }
}

@Composable
private fun ConnectorStyleSelector(
    state: ConnectorStyleState,
    update: (ConnectorStyleState) -> Unit
) {
    ExpandableCard(elevation = 2.dp, modifier = paddingMod, titleContent = {
        Text("Label Connector Style", modifier = paddingMod)
    }) {
        Row {
            Column(modifier = paddingMod) {
                strokes.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(it == state.strokeStyle, onClick = {
                            update(state.copy(strokeStyle = it))
                        })
                        Box(
                            modifier = Modifier.width(80.dp).height(25.dp).drawBehind {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(5.dp.toPx(), size.height / 2),
                                    end = Offset(size.width - 5.dp.toPx(), size.height / 2),
                                    strokeWidth = it.width,
                                    cap = it.cap,
                                    pathEffect = it.pathEffect
                                )
                            }.selectable(selected = false) { update(state.copy(strokeStyle = it)) }
                        ) {}
                    }
                }
            }
            Column(modifier = paddingMod) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Bezier")
                    Switch(state.straightLine, onCheckedChange = { value ->
                        update(state.copy(straightLine = value))
                    })
                    Text("Straight")
                }
            }
        }
    }
}

@Composable
private fun OtherOptions(
    state: OtherOptionsState,
    onShowLabels: (Boolean) -> Unit,
    onHoleSize: (Float) -> Unit,
    onLabelSpacing: (Float) -> Unit
) {
    ExpandableCard(elevation = 2.dp, modifier = paddingMod, titleContent = {
        Text("Other Options", modifier = paddingMod)
    }) {
        Column(modifier = paddingMod) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.showLabels, onCheckedChange = onShowLabels)
                Text("Show labels", modifier = paddingMod)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    state.labelSpacing,
                    onLabelSpacing,
                    valueRange = LabelSpacingSliderRange,
                    modifier = Modifier.width(150.dp)
                )
                Text("Label spacing ${state.labelSpacing}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    state.holeSize,
                    onHoleSize,
                    valueRange = HoleSizeRange,
                    modifier = Modifier.width(150.dp)
                )
                Text("Hole size")
            }
        }
    }
}

private fun Float.toPercent(precision: Int): String {
    @Suppress("MagicNumber")
    return "${(this * 100.0f).toString(precision)}%"
}

@OptIn(ExperimentalKoalaPlotApi::class)
private val vLegend = @Composable {
    ColumnLegend(
        fibonacci.size,
        symbol = { i ->
            Symbol(
                modifier = Modifier.size(padding),
                fillBrush = SolidColor(colors[i])
            )
        },
        label = { i ->
            Text("Category $i")
        },
        value = { i ->
            Text(
                (fibonacci[i] / fibonacciSum).toPercent(1),
                modifier = Modifier.align(Alignment.End)
            )
        }, modifier = Modifier.padding(padding).border(1.dp, Color.Black).padding(padding)
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
private val hLegend = @Composable {
    FlowLegend(
        fibonacci.size,
        symbol = { i ->
            Symbol(
                modifier = Modifier.size(padding), fillBrush = SolidColor(colors[i])
            )
        },
        label = { i ->
            Text("Category $i")
        },
        modifier = Modifier.padding(padding).border(1.dp, Color.Black).padding(padding)
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
@Composable
private fun PieChartSample(
    legendLocation: LegendLocation,
    connectorStyle: ConnectorStyleState,
    otherOptionsState: OtherOptionsState,
    modifier: Modifier = Modifier,
) {
    val legend = when (legendLocation) {
        LegendLocation.LEFT -> vLegend
        LegendLocation.RIGHT -> vLegend
        LegendLocation.BOTTOM -> hLegend
        LegendLocation.TOP -> hLegend
        else -> vLegend
    }

    ChartLayout(
        modifier = modifier.padding(padding),
        title = {
            Column {
                Text(
                    "Fibonacci Sequence",
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        legend = legend,
        legendLocation = legendLocation,
    ) {
        PieChart(
            fibonacci,
            modifier = modifier.padding(padding),
            slice = { i: Int ->
                DefaultSlice(
                    color = colors[i],
                    hoverExpandFactor = 1.05f,
                    hoverElement = { HoverSurface { Text(fibonacci[i].toString()) } }
                )
            },
            label = { i ->
                if (otherOptionsState.showLabels) {
                    Text((fibonacci[i] / fibonacciSum).toPercent(1))
                }
            },
            labelConnector = { i ->
                if (otherOptionsState.showLabels) {
                    if (connectorStyle.straightLine) {
                        StraightLineConnector(
                            connectorColor = colors[i],
                            connectorStroke = connectorStyle.strokeStyle
                        )
                    } else {
                        BezierLabelConnector(
                            connectorColor = colors[i],
                            connectorStroke = connectorStyle.strokeStyle
                        )
                    }
                }
            },
            holeSize = otherOptionsState.holeSize,
            holeContent = { holeTotalLabel() },
            labelSpacing = if (otherOptionsState.showLabels) otherOptionsState.labelSpacing else 1.0f,
            maxPieDiameter = Dp.Infinity
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
@Composable
private fun PieChartThumbnail(
    modifier: Modifier = Modifier,
) {
    ChartLayout(
        modifier = modifier.padding(padding),
        title = {
            Column {
                Text(
                    "Pie Chart",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    ) {
        PieChart(fibonacci, labelConnector = {})
    }
}

@Suppress("MagicNumber")
@Composable
private fun holeTotalLabel() {
    Column(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
        verticalArrangement = Arrangement.Center
    ) {
        ResponsiveText(
            "Total",
            modifier = Modifier.weight(0.20f).fillMaxSize(),
            style = LocalTextStyle.current.copy(
                fontFamily = FontFamily.SansSerif, color = Color.Black
            )
        )
        ResponsiveText(
            "${fibonacciSum.toInt()}",
            modifier = Modifier.weight(0.80f).fillMaxSize(),
            style = LocalTextStyle.current.copy(
                fontFamily = FontFamily.SansSerif,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )
    }
}
