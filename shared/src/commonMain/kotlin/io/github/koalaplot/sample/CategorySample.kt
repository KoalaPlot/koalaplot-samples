package io.github.koalaplot.sample

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.DefaultBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleRange
import io.github.koalaplot.core.xygraph.rememberAxisContent
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlin.random.Random

val categoryBarChartSampleView = object : SampleView {
    override val name: String = "Category Bar Chart"

    override fun toString(): String = name

    override val content: @Composable () -> Unit = @Composable {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            CategorySamplePlot(
                title = name,
                modifier = Modifier.weight(1.0f),
            )
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
private fun CategorySamplePlot(
    title: String,
    modifier: Modifier = Modifier,
) {
    ChartLayout(
        modifier = modifier.then(paddingMod),
        title = { ChartTitle(title) },
        legendLocation = LegendLocation.BOTTOM,
    ) {
        val data = GreekAlphabet.entries.associateWith { Random.nextFloat() }
        XYGraph(
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp, end = 20.dp)
                .fillMaxWidth(),
            xAxisModel = remember { CategoryAxisModel(GreekAlphabet.entries) },
            yAxisModel = rememberFloatLinearAxisModel(data.map { it.value }.autoScaleRange()),
            xAxisContent = rememberAxisContent(
                labels = {
                    Text(
                        modifier = Modifier.border(1.dp, Color.Black),
                        text = it.name,
                        maxLines = 1,
                    )
                },
                style = rememberAxisStyle(labelRotation = 45),
            ),
            yAxisContent = rememberAxisContent(),
        ) {
            VerticalBarPlot {
                data.forEach {
                    item(
                        x = it.key,
                        yMin = 0f,
                        yMax = it.value,
                        bar = { _, _, _ ->
                            DefaultBar(SolidColor(Color.Blue))
                        },
                    )
                }
            }
        }
    }
}

enum class GreekAlphabet {
    Alpha,
    Beta,
    Gamma,
    Delta,
    Epsilon,
    Zeta,
    Eta,
    Theta,
    Iota,
    Kappa,
    Lambda,
    Mu,
    Nu,
    Xi,
    Omicron,
    Pi,
    Rho,
    Sigma,
    Tau,
    Upsilon,
    Phi,
    Chi,
    Psi,
    Omega,
}
