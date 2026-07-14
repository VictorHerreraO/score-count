package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.ui.extension.shimmering

@Composable
fun PlayerScoreCard(
    state: PlayerScoreCardState,
    showPlayerName: Boolean,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    OutlinedCard(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
            onIncrement()
        },
        enabled = !state.isFinished,
        shape = MaterialTheme.shapes.extraLarge,
        modifier =
            modifier
                .fillMaxWidth()
                .alpha(
                    alpha =
                        when {
                            state.isFinished -> 0.75f
                            state.isServing -> DefaultAlpha
                            else -> 0.85f
                        }
                ),
        border = playerScoreCardBorder(showBorder = state.isServing && !state.isFinished),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = if (state.isServing && !state.isFinished) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                contentColor = MaterialTheme.colorScheme.primary
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!state.isFinished && state.isServing) {
                ServeIndicator(
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(all = 14.dp)
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showPlayerName) {
                    Text(
                        state.playerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = state.score.toString(),
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }
    }
}

@Composable
private fun ServeIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
    ) {
        val style = MaterialTheme.typography.bodySmall
        val boxSize = with(LocalDensity.current) { style.lineHeight.toDp() }
        Icon(
            Icons.Default.RadioButtonChecked,
            contentDescription = null,
            modifier =
                Modifier
                    .size(boxSize)
                    .shimmering()
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.serving),
            style = style
        )
    }
}

data class PlayerScoreCardState(
    val playerName: String,
    val score: Int,
    val isServing: Boolean,
    val isFinished: Boolean,
)

@Composable
private fun playerScoreCardBorder(showBorder: Boolean): BorderStroke {
    val color =
        if (showBorder) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Unspecified
        }
    return remember(color) { BorderStroke(width = 2.dp, color = color) }
}
