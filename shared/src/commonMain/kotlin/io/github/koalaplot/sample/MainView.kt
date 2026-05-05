package io.github.koalaplot.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.sample.polar.polarScatterPlotSample
import io.github.koalaplot.sample.polar.radialLinePlotSample
import io.github.koalaplot.sample.polar.spiderPlotSample
import io.github.vooft.compose.treeview.core.TreeView
import io.github.vooft.compose.treeview.core.node.Branch
import io.github.vooft.compose.treeview.core.node.Leaf
import io.github.vooft.compose.treeview.core.tree.Tree

@Composable
private fun composeSamplesTree(): Tree<SampleContent> = Tree {
    Branch(SampleCategory("Pie Charts")) {
        Leaf(pieSampleView)
    }
    Branch(SampleCategory("Bar Charts")) {
        Leaf(verticalBarSampleView)
        Leaf(horizontalBarSampleView)
        Leaf(groupedVerticalBarSampleView)
        Leaf(groupedHorizontalBarSampleView)
        Leaf(stackedVerticalBarSampleView)
        Leaf(stackedHorizontalBarSampleView)
        Leaf(waterfallChartSampleView)
    }
    Branch(SampleCategory("Line & Area Charts")) {
        Leaf(xyLineSampleView)
        Leaf(stairStepSampleView)
        Leaf(xyLogLineSampleView)
        Leaf(trigSampleView)
        Leaf(areaPlotSample1View)
        Leaf(stackedAreaSampleView)
        Leaf(timeLineSampleView)
        Leaf(liveTimeChartSampleView)
        Leaf(xyLineChartGestureSampleView)
    }
    Branch(SampleCategory("Radial/Polar Plots")) {
        Leaf(radialLinePlotSample)
        Leaf(spiderPlotSample)
        Leaf(polarScatterPlotSample)
    }
    Branch(SampleCategory("Bullet Graphs")) {
        Leaf(bulletGraphSampleView)
    }
    Branch(SampleCategory("Heat Maps")) {
        Leaf(heatMapSampleView)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainView(modifier: Modifier = Modifier) {
    MaterialTheme {
        KoalaPlotTheme {
            // var selectedTabIndex by remember { mutableStateOf(-1) }
            var selectedItem: SampleView? by remember { mutableStateOf(null) }

            val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo(true)).copy(horizontalPartitionSpacerSize = 0.dp)
            val destination = remember(selectedItem) {
                if (selectedItem == null) {
                    ThreePaneScaffoldDestinationItem(
                        ListDetailPaneScaffoldRole.List,
                        contentKey = null,
                    )
                } else {
                    ThreePaneScaffoldDestinationItem(
                        ListDetailPaneScaffoldRole.Detail,
                        contentKey = selectedItem,
                    )
                }
            }

            ListDetailPaneScaffold(
                directive = directive,
                value = computeThreePaneScaffoldValue(directive, destination),
                listPane = {
                    AnimatedPane {
                        TreeView(
                            composeSamplesTree(),
                            onClick = {
                                when (val content = it.content) {
                                    is SampleView -> {
                                        selectedItem = content
                                    }

                                    else -> {}
                                }
                            },
                        )
//                        LazyColumn {
//                            items(samples.size) { index ->
//                                ListItem(
//                                    headlineContent = { Text(samples[index].name) },
//                                    modifier = Modifier.clickable(
//                                        onClick = { selectedItem = samples[index] },
//                                    ),
//                                )
//                            }
//                        }
                    }
                },
                detailPane = {
                    AnimatedPane {
                        Column(Modifier.fillMaxSize()) {
                            TopAppBar(
                                title = { Text(selectedItem?.name ?: "") },
                                navigationIcon = {
                                    IconButton({ selectedItem = null }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                    }
                                },
                            )
                            HorizontalDivider()
                            selectedItem?.content?.invoke()
                        }
                    }
                },
                paneExpansionDragHandle = {
                    VerticalDivider()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun computeThreePaneScaffoldValue(
    directive: PaneScaffoldDirective,
    destination: ThreePaneScaffoldDestinationItem<*>,
): ThreePaneScaffoldValue {
    val isList = destination.pane == ListDetailPaneScaffoldRole.List
    val isDetail = destination.pane == ListDetailPaneScaffoldRole.Detail

    return when (directive.maxHorizontalPartitions) {
        // COMPACT (phone)
        1 -> {
            if (isList) {
                ThreePaneScaffoldValue(
                    primary = PaneAdaptedValue.Hidden,
                    secondary = PaneAdaptedValue.Expanded,
                    tertiary = PaneAdaptedValue.Hidden,
                )
            } else {
                ThreePaneScaffoldValue(
                    primary = PaneAdaptedValue.Expanded,
                    secondary = PaneAdaptedValue.Hidden,
                    tertiary = PaneAdaptedValue.Hidden,
                )
            }
        }

        // MEDIUM (tablet portrait, small desktop)
        else -> {
            ThreePaneScaffoldValue(
                secondary = PaneAdaptedValue.Expanded,
                primary = if (isDetail) {
                    PaneAdaptedValue.Expanded
                } else {
                    PaneAdaptedValue.Hidden
                },
                tertiary = PaneAdaptedValue.Hidden,
            )
        }
    }
}

sealed interface SampleContent

data class SampleCategory(
    val name: String,
) : SampleContent {
    override fun toString(): String = name
}

interface SampleView : SampleContent {
    val name: String
    val content: @Composable () -> Unit
}
