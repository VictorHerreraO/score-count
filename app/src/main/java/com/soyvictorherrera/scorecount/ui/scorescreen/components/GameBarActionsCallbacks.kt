package com.soyvictorherrera.scorecount.ui.scorescreen.components

/**
 * Unified callback actions for game bar components (CentralControls and BottomBarActions).
 *
 * Groups all action callbacks to reduce parameter list complexity and code duplication.
 */
data class GameBarActionsCallbacks(
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onUndo: () -> Unit,
    val onSettings: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onManageQueue: () -> Unit,
)

/**
 * Routes a [GameBarAction] to its corresponding callback function.
 */
fun GameBarActionsCallbacks.handleAction(action: GameBarAction) {
    when (action) {
        GameBarAction.OVERFLOW -> Unit // Should not happen
        GameBarAction.RESET -> onReset()
        GameBarAction.SETTINGS -> onSettings()
        GameBarAction.START_NEW_GAME -> onStartNewGame()
        GameBarAction.SWITCH_SERVE -> onSwitchServe()
        GameBarAction.UNDO -> onUndo()
        GameBarAction.MANAGE_QUEUE -> onManageQueue()
    }
}
