package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

private object BottomBarActionsDefaults {
    const val MAX_BOTTOM_BAR_ACTIONS = 4
    const val MAX_BOTTOM_BAR_ACTIONS_LARGE = 5

    val gameBarActions: List<GameBarAction> =
        listOf(
            GameBarAction.UNDO,
            GameBarAction.SWITCH_SERVE,
            GameBarAction.RESET,
            GameBarAction.SETTINGS,
            GameBarAction.MANAGE_QUEUE,
        )
}

@Composable
fun BottomBarActions(
    isFinished: Boolean,
    showSwitchServe: Boolean,
    hasUndoHistory: Boolean,
    callbacks: GameBarActionsCallbacks,
) {
    var showOverflowMenu by rememberSaveable { mutableStateOf(false) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isAtLeastMediumWidth = windowSizeClass.isWidthAtLeastBreakpoint(widthDpBreakpoint = WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isAtLeastLargeWidth =
        windowSizeClass.isWidthAtLeastBreakpoint(widthDpBreakpoint = WIDTH_DP_EXPANDED_LOWER_BOUND)
    val barState =
        rememberBottomBarState(
            isFinished = isFinished,
            showSwitchServe = showSwitchServe,
            maxBarActions =
                if (isAtLeastLargeWidth) {
                    BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS_LARGE
                } else {
                    BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS
                }
        )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        Row(
            modifier =
                Modifier
                    .widthIn(
                        max =
                            if (isAtLeastLargeWidth) {
                                WIDTH_DP_EXPANDED_LOWER_BOUND.dp
                            } else {
                                WIDTH_DP_MEDIUM_LOWER_BOUND.dp
                            }
                    ).padding(all = 16.dp)
                    .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            barState.visibleActions.forEach { action ->
                val showText =
                    isAtLeastMediumWidth ||
                        (barState.visibleActions.size < BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS)

                GameBarActionButton(
                    action = action,
                    showText = showText,
                    modifier = Modifier.weight(1f),
                    isActionEnabled = {
                        when (it) {
                            GameBarAction.UNDO -> hasUndoHistory
                            else -> true
                        }
                    }
                ) {
                    when (it) {
                        GameBarAction.OVERFLOW -> showOverflowMenu = true
                        else -> callbacks.handleAction(action = it)
                    }
                }
            }
        }
    }

    OverflowGameActionPicker(
        isVisible = showOverflowMenu,
        onDismiss = { showOverflowMenu = false },
        actions = barState.overflowActions,
        onActionSelected = { action -> callbacks.handleAction(action) }
    )
}

@Composable
private fun rememberBottomBarState(
    isFinished: Boolean,
    showSwitchServe: Boolean,
    maxBarActions: Int = BottomBarActionsDefaults.MAX_BOTTOM_BAR_ACTIONS
): GameBarState =
    remember(isFinished, showSwitchServe) {
        if (isFinished) {
            GameBarState(
                actions = listOf(GameBarAction.START_NEW_GAME),
                maxActions = maxBarActions
            )
        } else {
            GameBarState(
                actions =
                    if (showSwitchServe) {
                        BottomBarActionsDefaults.gameBarActions
                    } else {
                        BottomBarActionsDefaults.gameBarActions.filterNot {
                            it == GameBarAction.SWITCH_SERVE
                        }
                    },
                maxActions = maxBarActions
            )
        }
    }
