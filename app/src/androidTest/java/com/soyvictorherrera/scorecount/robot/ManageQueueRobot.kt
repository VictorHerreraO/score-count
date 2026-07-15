package com.soyvictorherrera.scorecount.robot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.espresso.Espresso

class ManageQueueRobot(
    private val composeTestRule: ComposeTestRule
) {
    fun toggleChallengerMode() {
        composeTestRule.onNodeWithTag("challenger_mode_switch").performClick()
    }

    fun assertChallengerModeEnabled(enabled: Boolean) {
        if (enabled) {
            composeTestRule.onNodeWithTag("challenger_mode_switch").assertIsOn()
        } else {
            composeTestRule.onNodeWithTag("challenger_mode_switch").assertIsOff()
        }
    }

    fun typePlayerName(name: String) {
        composeTestRule.onNodeWithTag("queue_player_name_input").performTextInput(name)
    }

    fun clickAddPlayer() {
        composeTestRule.onNodeWithTag("add_player_button").performClick()
    }

    fun addPlayer(name: String) {
        typePlayerName(name)
        clickAddPlayer()
    }

    fun assertAutocompleteSuggestionExists(name: String) {
        try {
            composeTestRule.onNodeWithTag("autocomplete_suggestion_$name").assertExists()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText(name).assertExists()
        }
    }

    fun clickAutocompleteSuggestion(name: String) {
        try {
            composeTestRule.onNodeWithTag("autocomplete_suggestion_$name").performClick()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText(name).performClick()
        }
    }

    fun assertPlayerInQueue(name: String) {
        composeTestRule.onNodeWithTag("queue_item_$name").assertExists()
    }

    fun assertPlayerNotInQueue(name: String) {
        composeTestRule.onNodeWithTag("queue_item_$name").assertDoesNotExist()
    }

    fun clickSkipPlayer(name: String) {
        composeTestRule.onNodeWithTag("skip_player_$name").performClick()
    }

    fun clickRemovePlayer(name: String) {
        composeTestRule.onNodeWithTag("remove_player_$name").performClick()
    }

    fun closeSheet() {
        Espresso.pressBack()
        composeTestRule.waitForIdle()
    }
}
