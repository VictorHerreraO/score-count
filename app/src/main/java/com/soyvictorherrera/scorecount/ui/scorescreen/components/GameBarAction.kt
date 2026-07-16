package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.R

enum class GameBarAction(
    val icon: ImageVector,
    @get:StringRes val text: Int,
) {
    OVERFLOW(icon = Icons.Default.MoreHoriz, text = R.string.action_more),
    RESET(icon = Icons.Default.RestartAlt, text = R.string.action_reset),
    SETTINGS(icon = Icons.Default.Settings, text = R.string.action_settings),
    START_NEW_GAME(icon = Icons.Default.Replay, text = R.string.action_start_new_game),
    SWITCH_SERVE(icon = Icons.Default.SwapHoriz, text = R.string.action_switch),
    UNDO(icon = Icons.AutoMirrored.Default.Undo, text = R.string.action_undo),
    MANAGE_QUEUE(icon = Icons.Default.Group, text = R.string.action_manage_queue),
}

data class GameBarState(
    private val actions: List<GameBarAction>,
    private val maxActions: Int,
) {
    val visibleActions: List<GameBarAction> =
        if (actions.size > maxActions) {
            actions
                .take(maxActions - 1)
                .plus(GameBarAction.OVERFLOW)
        } else {
            actions
        }

    val overflowActions: List<GameBarAction> =
        actions.drop(maxActions - 1)
}

@Composable
fun GameBarActionButton(
    action: GameBarAction,
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    isActionEnabled: (GameBarAction) -> Boolean,
    onClick: (GameBarAction) -> Unit,
) {
    val finalModifier =
        when (action) {
            GameBarAction.MANAGE_QUEUE -> modifier.testTag("manage_queue_button")
            GameBarAction.RESET -> modifier.testTag("reset_game_button")
            else -> modifier
        }
    OutlinedButton(
        onClick = { onClick(action) },
        enabled = isActionEnabled(action),
        modifier = finalModifier,
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding =
            if (showText) {
                PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            } else {
                PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            },
        content = {
            if (showText) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = action.text))
            } else {
                Icon(
                    imageVector = action.icon,
                    contentDescription = stringResource(id = action.text)
                )
            }
        }
    )
}
