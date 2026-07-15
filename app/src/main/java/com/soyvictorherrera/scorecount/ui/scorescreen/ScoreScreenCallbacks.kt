package com.soyvictorherrera.scorecount.ui.scorescreen

import com.soyvictorherrera.scorecount.ui.scorescreen.components.GameBarActionsCallbacks

/**
 * Callback actions for the ScoreScreen composables (Portrait and Landscape).
 *
 * Groups all game action callbacks to reduce parameter list complexity in screen-level composables.
 */
data class ScoreScreenCallbacks(
    val onIncrement: (Int) -> Unit,
    val onDecrement: (Int) -> Unit,
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit,
    val onUndo: () -> Unit,
    val onManageQueue: () -> Unit
)

/**
 * Converts [ScoreScreenCallbacks] to [GameBarActionsCallbacks] by mapping the relevant callbacks.
 */
fun ScoreScreenCallbacks.toGameBarActionsCallbacks() =
    GameBarActionsCallbacks(
        onReset = onReset,
        onSwitchServe = onSwitchServe,
        onStartNewGame = onStartNewGame,
        onUndo = onUndo,
        onSettings = onNavigateToSettings,
        onNavigateToHistory = onNavigateToHistory,
        onManageQueue = onManageQueue
    )
