package io.github.koalaplot.sample

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.lerp
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
import io.github.koalaplot.core.pie.CircularLabelPositionProvider
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.pie.PieLabelPlacement
import io.github.koalaplot.core.pie.StraightLineConnector
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.ResponsiveText
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toString

private val colors = generateHueColorPalette(fibonacci.size)
private val LabelSpacingSliderRange = 1.01f..1.3f
private val HoleSizeRange = 0f..0.9f
private val SliceGapRange = 0f..2f

private val strokes = buildList {
    add(Stroke(width = 1f))
    add(Stroke(width = 2f))
    add(Stroke(width = 3f))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))))
}

private data class LabelOptionsState(
    val strokeStyle: Stroke = strokes[0],
    val straightLine: Boolean = false,
    val showLabels: Boolean = false,
    val labelSpacing: Float = 1.1f,
    val labelPlacement: PieLabelPlacement = PieLabelPlacement.External
)

private data class OtherOptionsState(
    val holeSize: Float = 0.0f,
    val antiAlias: Boolean = false,
    val borders: Boolean = false,
    val sliceGap: Float = 0.0f,
    val forcePieCentering: Boolean = false
)

val pieSampleView = object : SampleView {
    override val name: String = "Pie Chart"

    override val thumbnail = @Composable {
        PieChartThumbnail()
    }

    override val content: @Composable () -> Unit = @Composable {
        var legendLocation by remember { mutableStateOf(LegendLocation.LEFT) }
        var connectorStyle by remember {
            mutableStateOf(LabelOptionsState())
        }
        var otherOptions by remember { mutableStateOf(OtherOptionsState()) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PieChartSample(
                legendLocation,
                connectorStyle,
                otherOptions,
                modifier = Modifier.weight(1.0f),
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            LegendPositionSelector(legendLocation) {
                legendLocation = it
            }
            LabelOptionsSelector(connectorStyle) {
                connectorStyle = it
            }
            OtherOptions(
                otherOptions,
                onUpdate = { otherOptions = it }
            )
        }
    }
}

@Composable
fun LegendPositionSelector(value: LegendLocation, onSelection: (LegendLocation) -> Unit) {
    ExpandableCard(
        modifier = paddingMod,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        titleContent = { Text("Legend Location", modifier = paddingMod) }
    ) {
        Column {
            LegendLocation.entries.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(it == value, onClick = { onSelection(it) })
                    Text(it.name)
                }
            }
        }
    }
}

@Composable
private fun LabelOptionsSelector(state: LabelOptionsState, update: (LabelOptionsState) -> Unit) {
    ExpandableCard(
        modifier = paddingMod,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        titleContent = { Text("Label Options", modifier = paddingMod) }
    ) {
        Row {
            Column(modifier = paddingMod) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.showLabels, onCheckedChange = { update(state.copy(showLabels = it)) })
                    Text("Show labels", modifier = paddingMod)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        state.labelSpacing,
                        { update(state.copy(labelSpacing = it)) },
                        valueRange = LabelSpacingSliderRange,
                        modifier = Modifier.width(150.dp)
                    )
                    Text("Label spacing ${state.labelSpacing}")
                }
                Column {
                    Text("Position")
                    RadioButtonRow(
                        state.labelPlacement is PieLabelPlacement.External,
                        onClick = { update(state.copy(labelPlacement = PieLabelPlacement.External)) },
                        "External"
                    )
                    RadioButtonRow(
                        state.labelPlacement is PieLabelPlacement.InternalOrExternal,
                        onClick = { update(state.copy(labelPlacement = PieLabelPlacement.InternalOrExternal())) },
                        "Internal or External"
                    )
                    RadioButtonRow(
                        state.labelPlacement is PieLabelPlacement.Internal,
                        onClick = { update(state.copy(labelPlacement = PieLabelPlacement.Internal())) },
                        "Internal"
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val r = when (state.labelPlacement) {
                            is PieLabelPlacement.External -> 0f
                            is PieLabelPlacement.Internal -> state.labelPlacement.radius
                            is PieLabelPlacement.InternalOrExternal -> state.labelPlacement.radius
                        }
                        Slider(
                            r,
                            {
                                when (state.labelPlacement) {
                                    is PieLabelPlacement.Internal -> update(
                                        state.copy(labelPlacement = PieLabelPlacement.Internal(it))
                                    )

                                    is PieLabelPlacement.InternalOrExternal -> update(
                                        state.copy(labelPlacement = PieLabelPlacement.InternalOrExternal(it))
                                    )

                                    else -> {}
                                }
                            },
                            valueRange = 0.1f..0.95f,
                            modifier = Modifier.width(150.dp),
                            enabled = state.labelPlacement !is PieLabelPlacement.External
                        )
                        Text("Internal label position $r")
                    }
                }
            }
            ConnectorStyleSelector(update, state)
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
private fun ConnectorStyleSelector(update: (LabelOptionsState) -> Unit, state: LabelOptionsState) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = paddingMod) {
        Button(onClick = { expanded = true }) {
            Text("Connector Style")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            strokes.forEachIndexed { index, stroke ->
                DropdownMenuItem(
                    text = {
                        Box(
                            modifier = Modifier.width(80.dp).height(25.dp).drawBehind {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(5.dp.toPx(), size.height / 2),
                                    end = Offset(size.width - 5.dp.toPx(), size.height / 2),
                                    strokeWidth = stroke.width,
                                    cap = stroke.cap,
                                    pathEffect = stroke.pathEffect
                                )
                            }
                        ) {}
                    },
                    onClick = {
                        update(state.copy(strokeStyle = stroke))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RadioButtonRow(enabled: Boolean, onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        RadioButton(enabled, onClick)
        Text(text)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OtherOptions(
    state: OtherOptionsState,
    onUpdate: (OtherOptionsState) -> Unit,
) {
    ExpandableCard(
        modifier = paddingMod,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        titleContent = {
            Text("Other Options", modifier = paddingMod)
        }
    ) {
        Column(modifier = paddingMod) {
            FlowRow {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Checkbox(
                        checked = state.antiAlias,
                        onCheckedChange = { onUpdate(state.copy(antiAlias = it)) }
                    )
                    Text("Antialias", modifier = paddingMod)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Checkbox(
                        checked = state.borders,
                        onCheckedChange = { onUpdate(state.copy(borders = it)) }
                    )
                    Text("Show slice borders", modifier = paddingMod)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Checkbox(
                        checked = state.forcePieCentering,
                        onCheckedChange = { onUpdate(state.copy(forcePieCentering = it)) }
                    )
                    Text("Center Pie", modifier = paddingMod)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    state.holeSize,
                    { onUpdate(state.copy(holeSize = it)) },
                    valueRange = HoleSizeRange,
                    modifier = Modifier.width(150.dp)
                )
                Text("Hole size ${state.holeSize}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    state.sliceGap,
                    { onUpdate(state.copy(sliceGap = it)) },
                    valueRange = SliceGapRange,
                    modifier = Modifier.width(150.dp)
                )
                Text("Slice gap ${state.sliceGap}")
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
        },
        modifier = Modifier.border(1.dp, Color.Black).padding(padding)
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
private val hLegend = @Composable {
    FlowLegend(
        fibonacci.size,
        symbol = { i ->
            Symbol(modifier = Modifier.size(padding), fillBrush = SolidColor(colors[i]))
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
    labelOptions: LabelOptionsState,
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
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        legend = legend,
        legendLocation = legendLocation,
    ) {
        PieChart(
            values = fibonacci,
            labelPositionProvider = CircularLabelPositionProvider(
                labelSpacing = if (labelOptions.showLabels) labelOptions.labelSpacing else 1.0f,
                labelPlacement = labelOptions.labelPlacement,
            ),
            modifier = modifier.padding(start = padding).border(1.dp, Color.Black).padding(padding),
            slice = { i: Int ->
                DefaultSlice(
                    color = colors[i],
                    border = if (otherOptionsState.borders) {
                        BorderStroke(
                            6.dp,
                            lerp(colors[i], Color.White, 0.2f)
                        )
                    } else {
                        null
                    },
                    hoverExpandFactor = 1.05f,
                    hoverElement = { HoverSurface { Text(fibonacci[i].toString()) } },
                    antiAlias = otherOptionsState.antiAlias,
                    gap = otherOptionsState.sliceGap
                )
            },
            label = { i ->
                if (labelOptions.showLabels) {
                    Text((fibonacci[i] / fibonacciSum).toPercent(1))
                }
            },
            labelConnector = { i ->
                if (labelOptions.showLabels) {
                    if (labelOptions.straightLine) {
                        StraightLineConnector(
                            connectorColor = colors[i],
                            connectorStroke = labelOptions.strokeStyle
                        )
                    } else {
                        BezierLabelConnector(
                            connectorColor = colors[i],
                            connectorStroke = labelOptions.strokeStyle
                        )
                    }
                }
            },
            holeSize = otherOptionsState.holeSize,
            holeContent = {
                HoleTotalLabel(Modifier.fillMaxSize().padding(it))
            },
            maxPieDiameter = Dp.Infinity,
            forceCenteredPie = otherOptionsState.forcePieCentering
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
private fun HoleTotalLabel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        ResponsiveText(
            "Total",
            modifier = Modifier.weight(0.20f).fillMaxSize(),
            style = LocalTextStyle.current.copy(fontFamily = FontFamily.SansSerif, color = Color.Black)
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
