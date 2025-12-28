package io.github.koalaplot.sample

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape

private const val DegreesHalfCircle: Float = 180.0f

@Composable
fun ExpandableCard(
    titleContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null,
    elevation: CardElevation = CardDefaults.cardElevation(),
    initExpandedState: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expandedState by remember { mutableStateOf(initExpandedState) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) DegreesHalfCircle else 0f,
    )

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                titleContent.invoke()
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    modifier = Modifier.rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    },
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Drop-Down Arrow",
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing,
                    ),
                ),
            ) {
                if (expandedState) {
                    content()
                }
            }
        }
    }
}
