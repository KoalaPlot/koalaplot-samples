package io.github.koalaplot.sample

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

private val LabelSpacingSliderRange = 1.01f..1.3f
private val HoleSizeRange = 0f..0.9f
private val SliceGapRange = 0f..2f

internal val strokes = buildList {
    add(Stroke(width = 1f))
    add(Stroke(width = 2f))
    add(Stroke(width = 3f))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))))
    add(Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))))
}

data class LabelOptionsState(
    val strokeStyle: Stroke = strokes[0],
    val straightLine: Boolean = false,
    val showLabels: Boolean = false,
    val labelSpacing: Float = 1.1f,
    val labelPlacement: PieLabelPlacement = PieLabelPlacement.External,
)

data class OtherOptionsState(
    val holeSize: Float = 0.0f,
    val antiAlias: Boolean = false,
    val borders: Boolean = false,
    val sliceGap: Float = 0.0f,
    val forcePieCentering: Boolean = true,
)

class PieSampleState(
    val pieValues: SnapshotStateList<Float>,
    val legendLocation: MutableState<LegendLocation>,
    val connectorStyle: MutableState<LabelOptionsState>,
    val otherOptions: MutableState<OtherOptionsState>,
    val pieSum: State<Float>,
    val colors: State<List<Color>>,
)

val pieSampleView = object : SampleView<PieSampleState> {
    override val name: String = "Pie Chart"

    override fun toString(): String = name

    override val hasOptions: Boolean = true

    @Composable
    override fun rememberState(): PieSampleState {
        val pieValues = remember { mutableStateListOf<Float>().apply { addAll(fibonacci) } }
        val legendLocation = remember { mutableStateOf(LegendLocation.LEFT) }
        val connectorStyle = remember {
            mutableStateOf(LabelOptionsState())
        }
        val otherOptions = remember { mutableStateOf(OtherOptionsState()) }

        val pieSum = remember { derivedStateOf { pieValues.sum() } }
        val colors = remember { derivedStateOf { generateHueColorPalette(pieValues.size) } }

        return remember(pieValues, legendLocation, connectorStyle, otherOptions, pieSum, colors) {
            PieSampleState(pieValues, legendLocation, connectorStyle, otherOptions, pieSum, colors)
        }
    }

    @Composable
    override fun Content(state: PieSampleState) {
        PieChartSample(
            state.pieValues,
            state.pieSum.value,
            state.colors.value,
            state.legendLocation.value,
            state.connectorStyle.value,
            state.otherOptions.value,
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Composable
    override fun Options(state: PieSampleState) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            LegendPositionSelector(
                state.legendLocation.value,
                onSelection = {
                    state.legendLocation.value = it
                },
            )
            LabelOptionsSelector(state.connectorStyle.value) {
                state.connectorStyle.value = it
            }
            OtherOptions(
                state.otherOptions.value,
            ) { state.otherOptions.value = it }
            PieDataSelector(state.pieValues)
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun PieDataSelector(
    values: SnapshotStateList<Float>,
    modifier: Modifier = Modifier,
) {
    ExpandableCard(
        titleContent = { Text("Pie Data", modifier = paddingMod) },
        modifier = modifier.then(paddingMod),
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        content = {
            Column(modifier = paddingMod) {
                values.forEachIndexed { index, value ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(padding),
                        modifier = Modifier.padding(bottom = padding),
                    ) {
                        var textValue by remember(value) { mutableStateOf(value.toString()) }

                        OutlinedTextField(
                            value = textValue,
                            onValueChange = {
                                textValue = it
                                it.toFloatOrNull()?.let { newValue ->
                                    values[index] = newValue
                                }
                            },
                            label = { Text("Value ${index + 1}") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                        )
                        IconButton(onClick = { values.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete item")
                        }
                    }
                }
                Button(
                    onClick = { values.add(1.0f) },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(padding))
                    Text("Add Item")
                }
            }
        },
    )
}

@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3Api::class)
@Suppress("MagicNumber")
@Composable
private fun PieChartSample(
    pieValues: List<Float>,
    pieSum: Float,
    colors: List<Color>,
    legendLocation: LegendLocation,
    labelOptions: LabelOptionsState,
    otherOptionsState: OtherOptionsState,
    modifier: Modifier = Modifier,
) {
    ChartLayout(
        modifier = modifier.padding(padding),
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Fibonacci Sequence",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        },
        legend = {
            PieLegend(pieValues, pieSum, colors, legendLocation)
        },
        legendLocation = legendLocation,
    ) {
        PieChart(
            labelPositionProvider = CircularLabelPositionProvider(
                labelSpacing = if (labelOptions.showLabels) labelOptions.labelSpacing else 1.0f,
                labelPlacement = labelOptions.labelPlacement,
            ),
            holeSize = otherOptionsState.holeSize,
            holeContent = {
                HoleTotalLabel(pieSum, Modifier.fillMaxSize().padding(it))
            },
            maxPieDiameter = Dp.Infinity,
            centeredAlignment = otherOptionsState.forcePieCentering,
        ) {
            pieValues.forEachIndexed { index, f ->
                item(
                    f,
                    label = {
                        if (labelOptions.showLabels) {
                            Text((pieValues[index] / pieSum).toPercent(1))
                        }
                    },
                    connector = {
                        if (labelOptions.showLabels) {
                            if (labelOptions.straightLine) {
                                StraightLineConnector(
                                    connectorColor = colors[index],
                                    connectorStroke = labelOptions.strokeStyle,
                                )
                            } else {
                                BezierLabelConnector(
                                    connectorColor = colors[index],
                                    connectorStroke = labelOptions.strokeStyle,
                                )
                            }
                        }
                    },
                    slice = {
                        DefaultSlice(
                            color = colors[index],
                            border = if (otherOptionsState.borders) {
                                BorderStroke(
                                    6.dp,
                                    lerp(colors[index], Color.White, 0.2f),
                                )
                            } else {
                                null
                            },
                            hoverExpandFactor = 1.05f,
                            antiAlias = otherOptionsState.antiAlias,
                            gap = otherOptionsState.sliceGap,
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun PieLegend(
    values: List<Float>,
    sum: Float,
    colors: List<Color>,
    legendLocation: LegendLocation,
) {
    when (legendLocation) {
        LegendLocation.LEFT, LegendLocation.RIGHT -> {
            ColumnLegend(
                values.size,
                symbol = { i ->
                    Symbol(
                        modifier = Modifier.size(padding),
                        fillBrush = SolidColor(colors[i]),
                    )
                },
                label = { i ->
                    Text("Category $i")
                },
                value = { i ->
                    Text(
                        (values[i] / sum).toPercent(1),
                        modifier = Modifier.align(Alignment.End),
                    )
                },
                modifier = Modifier.padding(padding),
            )
        }

        LegendLocation.BOTTOM, LegendLocation.TOP -> {
            FlowLegend(
                values.size,
                symbol = { i ->
                    Symbol(modifier = Modifier.size(padding), fillBrush = SolidColor(colors[i]))
                },
                label = { i ->
                    Text("Category $i")
                },
                modifier = Modifier.padding(padding),
            )
        }

        else -> {}
    }
}

@Composable
fun LegendPositionSelector(
    value: LegendLocation,
    onSelection: (LegendLocation) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExpandableCard(
        titleContent = { Text("Legend Location", modifier = paddingMod) },
        modifier = modifier.then(paddingMod),
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        content = {
            Column {
                LegendLocation.entries.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(it == value, onClick = { onSelection(it) })
                        Text(it.name)
                    }
                }
            }
        },
    )
}

@Suppress("LongMethod")
@Composable
internal fun LabelOptionsSelector(
    state: LabelOptionsState,
    update: (LabelOptionsState) -> Unit,
) {
    ExpandableCard(
        titleContent = { Text("Label Options", modifier = paddingMod) },
        modifier = paddingMod,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        content = {
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
                        modifier = Modifier.width(150.dp),
                    )
                    Text("Label spacing ${state.labelSpacing}")
                }
                Column {
                    Text("Position")
                    RadioButtonRow(
                        state.labelPlacement is PieLabelPlacement.External,
                        onClick = { update(state.copy(labelPlacement = PieLabelPlacement.External)) },
                        "External",
                    )
                    RadioButtonRow(
                        state.labelPlacement is PieLabelPlacement.InternalOrExternal,
                        onClick = { update(state.copy(labelPlacement = PieLabelPlacement.InternalOrExternal())) },
                        "Internal or External",
                    )
                    RadioButtonRow(
                        state.labelPlacement is PieLabelPlacement.Internal,
                        onClick = { update(state.copy(labelPlacement = PieLabelPlacement.Internal())) },
                        "Internal",
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
                                    is PieLabelPlacement.Internal -> {
                                        update(
                                            state.copy(labelPlacement = PieLabelPlacement.Internal(it)),
                                        )
                                    }

                                    is PieLabelPlacement.InternalOrExternal -> {
                                        update(
                                            state.copy(labelPlacement = PieLabelPlacement.InternalOrExternal(it)),
                                        )
                                    }

                                    else -> {}
                                }
                            },
                            valueRange = 0.1f..0.95f,
                            modifier = Modifier.width(150.dp),
                            enabled = state.labelPlacement !is PieLabelPlacement.External,
                        )
                        Text("Internal label position $r")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ConnectorStyleSelector(update, state)
                    Spacer(Modifier.width(padding))
                    Text("Bezier")
                    Switch(state.straightLine, onCheckedChange = { value ->
                        update(state.copy(straightLine = value))
                    })
                    Text("Straight")
                }
            }
        },
    )
}

@Composable
private fun ConnectorStyleSelector(
    update: (LabelOptionsState) -> Unit,
    state: LabelOptionsState,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = paddingMod) {
        Button(onClick = { expanded = true }) {
            Text("Connector Style")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            strokes.forEachIndexed { _, stroke ->
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
                                    pathEffect = stroke.pathEffect,
                                )
                            },
                        ) {}
                    },
                    onClick = {
                        update(state.copy(strokeStyle = stroke))
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun RadioButtonRow(
    enabled: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        RadioButton(enabled, onClick)
        Text(text)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun OtherOptions(
    state: OtherOptionsState,
    onUpdate: (OtherOptionsState) -> Unit,
) {
    ExpandableCard(
        titleContent = {
            Text("Other Options", modifier = paddingMod)
        },
        modifier = paddingMod,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        content = {
            Column(modifier = paddingMod) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(padding)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.antiAlias,
                            onCheckedChange = { onUpdate(state.copy(antiAlias = it)) },
                        )
                        Text("Antialias", modifier = paddingMod, maxLines = 1)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.borders,
                            onCheckedChange = { onUpdate(state.copy(borders = it)) },
                        )
                        Text("Show slice borders", modifier = paddingMod)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.forcePieCentering,
                            onCheckedChange = { onUpdate(state.copy(forcePieCentering = it)) },
                        )
                        Text("Center Pie", modifier = paddingMod)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        state.holeSize,
                        { onUpdate(state.copy(holeSize = it)) },
                        valueRange = HoleSizeRange,
                        modifier = Modifier.width(150.dp),
                    )
                    Text("Hole size ${state.holeSize}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        state.sliceGap,
                        { onUpdate(state.copy(sliceGap = it)) },
                        valueRange = SliceGapRange,
                        modifier = Modifier.width(150.dp),
                    )
                    Text("Slice gap ${state.sliceGap}")
                }
            }
        },
    )
}

internal fun Float.toPercent(precision: Int): String {
    @Suppress("MagicNumber")
    return "${(this * 100.0f).toString(precision)}%"
}

@Suppress("MagicNumber")
@Composable
internal fun HoleTotalLabel(
    sum: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        ResponsiveText(
            "Total",
            modifier = Modifier.weight(0.20f).fillMaxSize(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.SansSerif,
                color = Color.Black,
            ),
        )
        ResponsiveText(
            sum.toInt().toString(),
            modifier = Modifier.weight(0.80f).fillMaxSize(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.SansSerif,
                color = Color.Black,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
