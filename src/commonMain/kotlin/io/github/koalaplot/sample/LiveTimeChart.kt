package io.github.koalaplot.sample

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot2
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

val liveTimeChartSampleView = object : SampleView {
    override val name: String = "Live Time Chart"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            LiveTimeChart(true)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        LiveTimeChart(false)
    }
}

data class GraphData<X, Y : Comparable<Y>>(
    val xAxis: ImmutableList<X>,
    val yAxis: ImmutableList<Y>,
    val points: ImmutableList<DefaultPoint<X, Y>>,
    val yRange: ClosedFloatingPointRange<Y>,
)

private const val HistorySize = 20
private const val UpdateDelay = 500L

@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalTime::class)
@Composable
fun LiveTimeChart(thumbnail: Boolean) {
    val info = remember {
        val x = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val y = if (Random.nextBoolean()) {
            1f
        } else {
            -1f
        }
        mutableStateOf(
            GraphData(
                persistentListOf(Instant.fromEpochMilliseconds(x).toString()),
                persistentListOf(y),
                persistentListOf(DefaultPoint(Instant.fromEpochMilliseconds(x).toString(), y)),
                -1f..1f
            )
        )
    }

    LaunchedEffect(thumbnail) {
        withContext(Dispatchers.Main) {
            var count = 0
            while (isActive) {
                count++
                delay(UpdateDelay)
                val yLast = info.value.yAxis.last()
                val yNext = if (Random.nextBoolean()) {
                    yLast + 1
                } else {
                    yLast - 1
                }

                val x = kotlin.time.Clock.System.now().toEpochMilliseconds()
                info.value = info.value.copy(
                    info.value.xAxis.toPersistentList().mutate {
                        it.add(Instant.fromEpochMilliseconds(x).toString())
                    }.takeLast(HistorySize).toImmutableList(),
                    info.value.yAxis.toPersistentList().mutate {
                        it.add(yNext)
                    }.takeLast(HistorySize).toImmutableList(),
                    info.value.points.toPersistentList().mutate {
                        it.add(DefaultPoint(Instant.fromEpochMilliseconds(x).toString(), yNext))
                    }.takeLast(HistorySize).toImmutableList(),
                    min(info.value.yRange.start, yNext)..max(info.value.yRange.endInclusive, yNext)
                )
            }
        }
    }

    ChartLayout(
        modifier = paddingMod.padding(horizontal = 8.dp),
        title = { ChartTitle("Live Time Chart") },
        legendLocation = LegendLocation.NONE,
    ) {
        XYGraph(
            xAxisModel = CategoryAxisModel(info.value.xAxis),
            yAxisModel = FloatLinearAxisModel(
                range = info.value.yRange,
                minimumMajorTickSpacing = 50.dp,
            ),
            yAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it.toString())
                }
            },
            xAxisLabels = {
                if (!thumbnail) {
                    AxisLabel(it, Modifier.padding(top = 2.dp))
                }
            },
            yAxisTitle = { },
            xAxisTitle = { },
            xAxisStyle = rememberAxisStyle(labelRotation = 45),
        ) {
            LinePlot2(
                info.value.points,
                symbol = { Symbol(fillBrush = SolidColor(Color.Black)) },
                animationSpec = TweenSpec(0)
            )
        }
    }
}
