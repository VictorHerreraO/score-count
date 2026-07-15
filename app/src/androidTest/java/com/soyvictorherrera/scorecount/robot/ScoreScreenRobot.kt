package com.soyvictorherrera.scorecount.robot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

class ScoreScreenRobot(
    private val composeTestRule: ComposeTestRule
) {
    fun clickManageQueue() {
        composeTestRule.onNodeWithTag("manage_queue_button").performClick()
    }

    fun assertPlayer1Name(name: String) {
        composeTestRule.onNodeWithTag("player_1_name").assertTextContains(name, substring = true)
    }

    fun assertPlayer2Name(name: String) {
        composeTestRule.onNodeWithTag("player_2_name").assertTextContains(name, substring = true)
    }

    fun assertNextPlayer(name: String) {
        composeTestRule.onNodeWithTag("next_player_indicator").assertTextContains(name, substring = true)
    }

    fun assertNextPlayerDoesNotExist() {
        composeTestRule.onNodeWithTag("next_player_indicator").assertDoesNotExist()
    }

    fun incrementPlayer1Score() {
        composeTestRule.onNodeWithTag("player_1_score").performClick()
    }

    fun incrementPlayer2Score() {
        composeTestRule.onNodeWithTag("player_2_score").performClick()
    }

    fun clickResetGame() {
        composeTestRule.onNodeWithTag("reset_game_button").performClick()
    }

    fun assertPlayer1Score(score: String) {
        composeTestRule.onNodeWithTag("player_1_score").assertTextContains(score, substring = true)
    }

    fun assertPlayer2Score(score: String) {
        composeTestRule.onNodeWithTag("player_2_score").assertTextContains(score, substring = true)
    }

    fun clickUndo() {
        val context =
            androidx.test.platform.app.InstrumentationRegistry
                .getInstrumentation()
                .targetContext
        val undoText = context.getString(com.soyvictorherrera.scorecount.R.string.action_undo)
        composeTestRule.onNodeWithText(undoText).performClick()
    }
}
