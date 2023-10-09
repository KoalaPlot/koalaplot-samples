@file:Suppress("TooManyFunctions")

package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.BulletGraph
import io.github.koalaplot.core.bar.BulletGraphScope
import io.github.koalaplot.core.bar.BulletGraphs
import io.github.koalaplot.core.bar.FixedFraction
import io.github.koalaplot.core.bar.HorizontalBarIndicator
import io.github.koalaplot.core.bar.LineIndicator
import io.github.koalaplot.core.bar.VariableFraction
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xychart.LinearAxisModel

@OptIn(ExperimentalKoalaPlotApi::class)
val bulletGraphSampleView = object : SampleView {
    override val name: String = "Bullet Graph"

    override val thumbnail = @Composable {
        ThumbnailTheme {
            MaterialTheme(lightColorScheme(primary = Color.Black)) {
                BulletGraphThumbnail()
            }
        }
    }

    override val content: @Composable () -> Unit = @Composable {
        Column {
            ChartLayout(
                modifier = Modifier.sizeIn(minHeight = 200.dp, maxHeight = 600.dp).weight(1f)
            ) {
                BulletGraphSample()
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun BulletGraphSample() {
    Column {
        MaterialTheme(lightColorScheme(primary = Color.Black)) {
            BulletGraphs(modifier = Modifier.padding(KoalaPlotTheme.sizes.gap)) {
                @Suppress("MagicNumber")
                labelWidth = VariableFraction(0.25f)
                bulletGraphSample1()
                bulletGraphSample2()
                bulletGraphSample3()
                bulletGraphSample4()
                bulletGraphSample5()
                bulletGraphSample6()
                bulletGraphSample7()
                bulletGraphSample8()
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample1() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Revenue 2005 YTD", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("(US $ in thousands)", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
            }
        }
        axis {
            labels { AxisText("${it.toInt()}") }
        }
        comparativeMeasure(260f)
        comparativeMeasure(210f) { LineIndicator(Color.DarkGray) }
        featuredMeasureBar(275f)
        ranges(0f, 200f, 250f, 300f)
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample2() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Profit", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("%", style = MaterialTheme.typography.bodySmall)
            }
        }
        axis { labels { AxisText("${it.toInt()}%") } }
        comparativeMeasure(27f)
        featuredMeasureBar(22.5f)
        ranges(0f, 20f, 25f, 30f)
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample3() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Avg Order Size", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("U.S. $", style = MaterialTheme.typography.bodySmall)
            }
        }
        axis { labels { AxisText("${it.toInt()}") } }
        comparativeMeasure(550f)
        featuredMeasureBar(325f)
        ranges(0f, 350f, 500f, 600f)
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample4() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("New Customers", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("Count", style = MaterialTheme.typography.bodySmall)
            }
        }
        axis { labels { AxisText("${it.toInt()}") } }
        comparativeMeasure(2100f)
        featuredMeasureBar(1700f)
        ranges(0f, 1400f, 2000f, 2500f)
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample5() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Cust Satisfaction", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("Top Rating of 5", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
            }
        }
        axis {
            labels { AxisText("${it.toInt()}") }
            model = LinearAxisModel(
                0f..5f,
                minimumMajorTickIncrement = 1f,
                minorTickCount = 0,
                allowZooming = false,
                allowPanning = false
            )
        }

        comparativeMeasure(4.5f)
        featuredMeasureBar(4.6f)
        ranges(0f) {
            range(3.5f)
            range(4.3f)
            range(5f)
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample6() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Revenue 2005 YTD", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("(US $ in thousands)", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
            }
        }
        axis { labels { AxisText("${it.toInt()}") } }
        comparativeMeasure(1225f)
        featuredMeasureSymbol(1150f)
        ranges(700f) {
            range(1100f)
            range(1200f)
            range(1300f)
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample7() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Profit", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                Text("(1,000s)", style = MaterialTheme.typography.bodySmall)
            }
        }
        axis { labels { AxisText("${it.toInt()}") } }
        comparativeMeasure(0f) {
            LineIndicator(color = Color.Black, heightFraction = 1f, width = Dp.Hairline)
        }
        comparativeMeasure(200f)
        featuredMeasureBar(-25f) {
            HorizontalBarIndicator(SolidColor(Color.Red), fraction = 0.33f)
        }
        ranges(-50f) {
            range(150f)
            range(200f)
            range(250f)
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
private fun BulletGraphScope.bulletGraphSample8() {
    bullet {
        label {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = KoalaPlotTheme.sizes.gap)
            ) {
                Text("Expenses (1,000s)", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
            }
        }
        axis { labels { AxisText("${it.toInt()}") } }
        comparativeMeasure(-45f)
        featuredMeasureBar(-65f)
        ranges(-120f) {
            range(-80f) {
                HorizontalBarIndicator(SolidColor(Color.Red))
            }
            range(-40f)
            range(0f)
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Suppress("MagicNumber")
@Composable
private fun BulletGraphThumbnail() {
    ChartLayout(paddingMod, title = { Text("Bullet Graph") }) {
        Box(contentAlignment = Alignment.Center) {
            BulletGraph(modifier = Modifier.fillMaxHeight(0.25f)) {
                comparativeMeasure(260f) {
                    LineIndicator(width = 4.dp)
                }
                featuredMeasureBar(275f)
                labelWidth = FixedFraction(0f)

                ranges(0f) {
                    range(200f)
                    range(250f)
                    range(300f)
                }
            }
        }
    }
}

@Composable
private fun AxisText(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall)
}
