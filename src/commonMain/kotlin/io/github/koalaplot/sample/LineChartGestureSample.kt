package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope

private data class GestureOptionsState(
    val panEnabled: Boolean = true,
    val zoomEnabled: Boolean = true,
    val independentZoom: Boolean = true,
    val allowZoomX: Boolean = true,
    val allowZoomY: Boolean = true,
    val allowPanX: Boolean = true,
    val allowPanY: Boolean = true,
)

val xyLineChartGestureSampleView = object : SampleView {

    override val name: String = "Gestures"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            XYSamplePlot(true, name, GestureOptionsState(false, false, false))
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            var gestureOptions by remember { mutableStateOf(GestureOptionsState()) }
            XYSamplePlot(
                thumbnail = false,
                title = "Gestures",
                gestureOptions = gestureOptions,
                modifier = Modifier.weight(1.0f)
            )
            GestureOptionsSelector(state = gestureOptions, onUpdate = { gestureOptions = it })
        }
    }
}

private val colorMap = buildMap {
    val colors = generateHueColorPalette(FakeData.floatData.size)
    var i = 0
    FakeData.floatData.forEachIndexed { index, _ ->
        put(index, colors[i++])
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun XYSamplePlot(
    thumbnail: Boolean,
    title: String,
    gestureOptions: GestureOptionsState,
    modifier: Modifier = Modifier,
) {
    ChartLayout(
        modifier = modifier.then(paddingMod),
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM
    ) {
        val xAxis = remember(gestureOptions.allowPanX, gestureOptions.allowZoomX) {
            FloatLinearAxisModel(
                range = FakeData.DataSetXRange,
                zoomRangeLimit = 10f,
                minimumMajorTickSpacing = 40.dp,
                allowPanning = gestureOptions.allowPanX,
                allowZooming = gestureOptions.allowZoomX,
            )
        }
        val yAxis = remember(gestureOptions.allowPanY, gestureOptions.allowZoomY) {
            FloatLinearAxisModel(
                range = FakeData.DataSetYRange,
                zoomRangeLimit = 10f,
                minimumMajorTickSpacing = 40.dp,
                allowPanning = gestureOptions.allowPanY,
                allowZooming = gestureOptions.allowZoomY,
            )
        }
        XYGraph(
            xAxisModel = xAxis,
            yAxisModel = yAxis,
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString(), Modifier.padding(top = 2.dp))
                }
            },
            xAxisTitle = {
                if (!thumbnail) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AxisTitle("Random data set (X)")
                    }
                }
            },
            yAxisLabels = {
                if (!thumbnail) AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                if (!thumbnail) {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        AxisTitle(
                            "Random data set (Y)",
                            modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                                .padding(bottom = padding)
                        )
                    }
                }
            },
            panEnabled = gestureOptions.panEnabled,
            zoomEnabled = gestureOptions.zoomEnabled,
            allowIndependentZoom = gestureOptions.independentZoom,
        ) {
            FakeData.floatData.forEachIndexed { index, points ->
                chart(
                    dataSetIndex = index,
                    data = points,
                    thumbnail = thumbnail,
                )
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYGraphScope<Float, Float>.chart(
    dataSetIndex: Int,
    data: List<Point<Float, Float>>,
    thumbnail: Boolean,
) {
    LinePlot(
        data = data,
        lineStyle = LineStyle(
            brush = SolidColor(colorMap[dataSetIndex] ?: Color.Black),
            strokeWidth = 2.dp
        ),
        symbol = { point ->
            Symbol(
                shape = CircleShape,
                fillBrush = SolidColor(colorMap[dataSetIndex] ?: Color.Black),
                modifier = Modifier.then(
                    if (!thumbnail) {
                        Modifier.hoverableElement {
                            HoverSurface { Text(point.y.toString()) }
                        }
                    } else {
                        Modifier
                    }
                )
            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GestureOptionsSelector(state: GestureOptionsState, onUpdate: (GestureOptionsState) -> Unit) {
    ExpandableCard(
        modifier = paddingMod,
        colors = CardDefaults.elevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(),
        titleContent = { Text("Gesture options", modifier = paddingMod) }
    ) {
        FlowRow {
            OptionCheckbox(
                title = "Pan enabled",
                checked = state.panEnabled,
                onCheckedChange = { onUpdate(state.copy(panEnabled = it)) }
            )
            OptionCheckbox(
                title = "Zoom enabled",
                checked = state.zoomEnabled,
                onCheckedChange = { onUpdate(state.copy(zoomEnabled = it)) }
            )
            OptionCheckbox(
                title = "Independent zoom",
                checked = state.independentZoom,
                onCheckedChange = { onUpdate(state.copy(independentZoom = it)) }
            )
            OptionCheckbox(
                title = "Allow zoom X",
                checked = state.allowZoomX,
                onCheckedChange = { onUpdate(state.copy(allowZoomX = it)) }
            )
            OptionCheckbox(
                title = "Allow zoom Y",
                checked = state.allowZoomY,
                onCheckedChange = { onUpdate(state.copy(allowZoomY = it)) }
            )
            OptionCheckbox(
                title = "Allow pan X",
                checked = state.allowPanX,
                onCheckedChange = { onUpdate(state.copy(allowPanX = it)) }
            )
            OptionCheckbox(
                title = "Allow pan Y",
                checked = state.allowPanY,
                onCheckedChange = { onUpdate(state.copy(allowPanY = it)) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowScope.OptionCheckbox(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(title, modifier = paddingMod)
    }
}
