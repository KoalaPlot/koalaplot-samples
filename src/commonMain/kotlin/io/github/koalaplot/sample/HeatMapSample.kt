@file:Suppress("MagicNumber")

package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.heatmap.discreteColorScale
import io.github.koalaplot.core.heatmap.divergingColorScale
import io.github.koalaplot.core.heatmap.generateHistogram2D
import io.github.koalaplot.core.heatmap.HeatMapGrid
import io.github.koalaplot.core.heatmap.HeatMapPlot
import io.github.koalaplot.core.heatmap.linearColorScale
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.normalize
import io.github.koalaplot.core.xygraph.AxisContent
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberDoubleLinearAxisModel
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.text.format

@OptIn(ExperimentalKoalaPlotApi::class)
val heatMapSampleView = object : SampleView {
    override val name: String = "Heat Map"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            HeapMapSamplePlot(HeapMapSampleState(), thumbnail = true)
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        var sampleState by remember {
            mutableStateOf(
                HeapMapSampleState()
            )
        }
        Column {
            ChartLayout(
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f)
            ) {
                HeapMapSamplePlot(state = sampleState)
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            HeatMapSampleConfigurator(sampleState) {
                sampleState = it
            }
        }
    }
}

/**
 * Configuration for a Gaussian cluster in 2D space
 */
public data class ClusterConfig(
    val centerX: Double,
    val centerY: Double,
    val standardDeviation: Double,
    val weight: Double,
)

/**
 * Simple data class for 2D points
 */
public data class Point2D(
    val x: Double,
    val y: Double,
)

/**
 * Generate 2D points following a Gaussian distribution around a center point
 * Uses Box-Muller transform for normally distributed coordinates
 */
private fun generateGaussianCluster(
    random: Random,
    numPoints: Int,
    config: ClusterConfig,
): List<Point2D> = List(numPoints) {
    val angle = random.nextDouble() * 2 * PI
    // Box-Muller transform for Gaussian distribution
    val u1 = random.nextDouble()
    val u2 = random.nextDouble()
    val radius = abs(sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2) * config.standardDeviation)
    Point2D(
        config.centerX + radius * cos(angle),
        config.centerY + radius * sin(angle),
    )
}

/**
 * Generate 2D points with multiple distinct clusters for histogram demonstration
 */
public fun generateHistogramPoints(
    numPoints: Int,
    clusterConfigs: List<ClusterConfig> = defaultClusterConfigs(),
): List<Point2D> {
    require(numPoints > 0) { "Number of points must be positive" }

    val random = Random(42) // Fixed seed for reproducible results

    // Generate points for each cluster
    val clusterPoints = clusterConfigs.flatMap { config ->
        val pointsInCluster = (numPoints * config.weight).toInt()
        generateGaussianCluster(random, pointsInCluster, config)
    }

    return clusterPoints
}

/**
 * Default cluster configurations that create overlapping patterns
 */
public fun defaultClusterConfigs(): List<ClusterConfig> = listOf(
    ClusterConfig(centerX = 25.0, centerY = 25.0, standardDeviation = 4500.0, weight = 0.4),
    ClusterConfig(centerX = 75.0, centerY = 75.0, standardDeviation = 4200.0, weight = 0.3),
    ClusterConfig(centerX = 10.0, centerY = 80.0, standardDeviation = 4000.0, weight = 0.2),
)

/**
 * Extension function to find maximum value in a HeatMapGrid
 */
fun <T : Comparable<T>> Iterable<T>.range(): ClosedRange<T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null

    var min = iterator.next()
    var max = min

    for (value in iterator) {
        if (value < min) min = value
        if (value > max) max = value
    }

    return min..max
}

/**
 * Generate heatmap data using a continuous function with noise
 */
public fun generateHeatMapData(
    width: Int,
    height: Int,
): Array<Array<Double>> {
    val random = Random(42) // Fixed seed for reproducible results
    val data = Array(width) { Array<Double>(height) { 0.0 } }

    for (x in 0 until width) {
        for (y in 0 until height) {
            // Normalize coordinates to 0-1 range
            val nx = x.toFloat() / width
            val ny = y.toFloat() / height

            // Create interesting pattern using multiple sine waves
            val baseValue = (
                // Large wave pattern
                sin(nx * PI * 2) * cos(ny * PI * 2) * 30 +
                    // Smaller ripples
                    sin(nx * PI * 8) * sin(ny * PI * 8) * 15 +
                    // Diagonal gradient
                    (nx + ny) * 20 +
                    // Center hotspot
                    exp(-((nx - 0.5).pow(2) - (ny - 0.5).pow(2)) * 10) * 25
                )

            // Add random noise
            val noise = (random.nextFloat() - 0.5f) * 20

            // Combine and clamp to 0-100 range
            val finalValue = (baseValue + noise)

            data[x][y] = finalValue
        }
    }

    return data
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
public fun HeapMapSamplePlot(
    state: HeapMapSampleState,
    thumbnail: Boolean = false,
) {
    val gridSize = 100
    val function2D = generateHeatMapData(gridSize, gridSize)

    @Composable
    fun Label(text: Double) {
        Text(
            "%.0f".format(text),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(2.dp),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.weight(1f),
        ) {
            val samples = generateHistogramPoints(300000) + List(8) {
                Point2D(85.0, 25.0)
            }
            val histogramBins = generateHistogram2D(
                samples = samples,
                nBinsX = gridSize / 4,
                nBinsY = gridSize / 4,
                xDomain = 0.0..gridSize.toDouble(),
                yDomain = 0.0..gridSize.toDouble(),
                xGetter = { it.x },
                yGetter = { it.y },
            )

            val maxOccurrences = histogramBins.flatten().maxOrNull()?.takeIf { it > 0 } ?: 1
            val histogramScale = { v: Int -> (0..maxOccurrences).normalize(v) }

            XYGraph(
                xAxisModel = rememberDoubleLinearAxisModel(0.0..gridSize.toDouble()),
                yAxisModel = rememberDoubleLinearAxisModel(0.0..gridSize.toDouble()),
                xAxisContent = AxisContent({ if (!thumbnail) Label(it) }, {}, rememberAxisStyle()),
                yAxisContent = AxisContent({ if (!thumbnail) Label(it) }, {}, rememberAxisStyle()),
                modifier = Modifier.weight(1f),
            ) {
                HeatMapPlot(
                    xDomain = 0.0..gridSize.toDouble(),
                    yDomain = 0.0..gridSize.toDouble(),
                    bins = histogramBins,
                    colorScale = { v -> state.histogramScale.color(histogramScale(v)) },
                    alphaScale = { v -> state.histogramScale.alpha(histogramScale(v)) },
                )
            }

            Spacer(Modifier.width(8.dp))

            val binsRange = function2D.flatten().range() ?: 0.0..100.0

            val heatmapScale = { v: Double -> binsRange.normalize(v) }
            XYGraph(
                xAxisModel = rememberDoubleLinearAxisModel(0.0..gridSize.toDouble()),
                yAxisModel = rememberDoubleLinearAxisModel(0.0..gridSize.toDouble()),
                xAxisContent = AxisContent({ if (!thumbnail) Label(it) }, {}, rememberAxisStyle()),
                yAxisContent = AxisContent({ if (!thumbnail) Label(it) }, {}, rememberAxisStyle()),
                modifier = Modifier.weight(1f),
            ) {
                HeatMapPlot(
                    xDomain = 0.0..gridSize.toDouble(),
                    yDomain = 0.0..gridSize.toDouble(),
                    bins = function2D,
                    colorScale = { v -> state.heatMapScale.color(heatmapScale(v)) },
                    alphaScale = { v -> state.heatMapScale.alpha(heatmapScale(v)) },
                )
            }
        }
    }
}

/**
 * Represents a configurations for the color scale of a heat map
 */
enum class ScaleConfig(
    val color: (Double) -> Color,
    val alpha: (Double) -> Float = { 1f },
) {
    AlphaScale(
        color = { v -> Color(0xFF9944EE) },
        alpha = { v -> v.toFloat().coerceIn(0f, 1f) },
    ),
    DiscreteScale(
        color = discreteColorScale(
            domain = 0.0..1.0,
            colors = listOf(
                Color(0xFF000033), // Dark blue
                Color(0xFF0066FF), // Light blue
                Color(0xFF00FF66), // Green-cyan
                Color(0xFFFFCC00), // Yellow
                Color(0xFFFF6600), // Orange
                Color(0xFFAA0000), // Red
            ),
        ),
    ),
    LinearScale(
        color = linearColorScale(
            domain = 0.0..1.0,
            colors = listOf(
                Color(0xFF000033), // Dark blue
                Color(0xFF000066), // Blue
                Color(0xFF0000CC), // Bright blue
                Color(0xFF0066FF), // Light blue
                Color(0xFF00CCFF), // Cyan
                Color(0xFF00FF66), // Green-cyan
                Color(0xFF66FF00), // Yellow-green
                Color(0xFFCCFF00), // Yellow
                Color(0xFFFFCC00), // Orange-yellow
                Color(0xFFFF6600), // Orange
                Color(0xFFAA0000), // Red
            ),
        ),
    ),
    DivergentScale(
        color = divergingColorScale(
            domain = 0.0..1.0,
            lowColor = Color.Blue,
            midColor = Color.White,
            highColor = Color.Red,
        )
    ),
}

data class HeapMapSampleState(
    val histogramScale: ScaleConfig = ScaleConfig.AlphaScale,
    val heatMapScale: ScaleConfig = ScaleConfig.LinearScale,
)

@Composable
fun LabeledRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
) {
    Row(
        modifier =
        Modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
fun HeatMapSampleConfigurator(
    state: HeapMapSampleState,
    update: (HeapMapSampleState) -> Unit
) {
    ExpandableCard(
        modifier = paddingMod,
        titleContent = { Text("Heat Map Options", modifier = paddingMod) }
    ) {
        Column {
            Text("Scale for 2D histogram data")
            Row {
                ScaleConfig.entries.forEach {
                    LabeledRadioButton(
                        label = it.name,
                        selected = it == state.histogramScale,
                        onClick = { update(state.copy(histogramScale = it)) },
                    )
                }
            }
            Text("Scale for sampled function data")
            Row {
                ScaleConfig.entries.forEach {
                    LabeledRadioButton(
                        label = it.name,
                        selected = it == state.heatMapScale,
                        onClick = { update(state.copy(heatMapScale = it)) },
                    )
                }
            }
        }
    }
}
