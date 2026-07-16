package com.soyvictorherrera.scorecount.ui.scorescreen

import android.content.res.Configuration
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import com.soyvictorherrera.scorecount.ui.scorescreen.components.ManageQueueBottomSheet
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenLandscape
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenLandscapeWithBottomBar
import com.soyvictorherrera.scorecount.ui.scorescreen.layout.ScoreScreenPortrait
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme

@Suppress("LongMethod")
@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSettings by viewModel.gameSettings.collectAsState()
    val hasUndoHistory by viewModel.hasUndoHistory.collectAsState()
    val playerProfiles by viewModel.playerProfiles.collectAsState(initial = emptyList())
    var showManageQueueBottomSheet by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val callbacks =
        ScoreScreenCallbacks(
            onIncrement = viewModel::incrementScore,
            onDecrement = viewModel::decrementScore,
            onReset = viewModel::resetGame,
            onSwitchServe = viewModel::manualSwitchServe,
            onStartNewGame = viewModel::resetGame,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSettings = onNavigateToSettings,
            onUndo = viewModel::undoLastChange,
            onManageQueue = { showManageQueueBottomSheet = true }
        )

    ScoreCountTheme {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                val useBottomBarLayout =
                    windowSizeClass.isHeightAtLeastBreakpoint(heightDpBreakpoint = HEIGHT_DP_MEDIUM_LOWER_BOUND)
                if (useBottomBarLayout) {
                    ScoreScreenLandscapeWithBottomBar(
                        gameState = gameState,
                        gameSettings = gameSettings,
                        hasUndoHistory = hasUndoHistory,
                        callbacks = callbacks
                    )
                } else {
                    ScoreScreenLandscape(
                        gameState = gameState,
                        gameSettings = gameSettings,
                        hasUndoHistory = hasUndoHistory,
                        callbacks = callbacks
                    )
                }
            }

            else -> {
                ScoreScreenPortrait(
                    gameState = gameState,
                    gameSettings = gameSettings,
                    hasUndoHistory = hasUndoHistory,
                    callbacks = callbacks
                )
            }
        }
    }

    if (showManageQueueBottomSheet) {
        ManageQueueBottomSheet(
            challengerModeEnabled = gameSettings.challengerMode,
            onChallengerModeToggled = viewModel::toggleChallengerMode,
            queue = gameState.challengerQueue,
            playerProfiles = playerProfiles,
            onAddPlayer = viewModel::addPlayerToQueue,
            onRemovePlayer = viewModel::removePlayerFromQueue,
            onSkipPlayer = viewModel::skipPlayerInQueue,
            onDismissRequest = { showManageQueueBottomSheet = false }
        )
    }
}

object ScoreScreenDefaults {
    const val ALPHA_PRIMARY = 1F
    const val ALPHA_SECONDARY = 0.75F
}
