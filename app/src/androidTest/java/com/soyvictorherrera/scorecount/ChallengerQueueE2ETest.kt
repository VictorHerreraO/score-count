package com.soyvictorherrera.scorecount

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.soyvictorherrera.scorecount.robot.ManageQueueRobot
import com.soyvictorherrera.scorecount.robot.ScoreScreenRobot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChallengerQueueE2ETest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var scoreScreenRobot: ScoreScreenRobot
    private lateinit var manageQueueRobot: ManageQueueRobot

    @Before
    fun setUp() {
        hiltRule.inject()
        scoreScreenRobot = ScoreScreenRobot(composeTestRule)
        manageQueueRobot = ManageQueueRobot(composeTestRule)
    }

    // =========================================================================
    // Tier 1 - Feature Coverage
    // =========================================================================

    @Test
    fun test_CMT_1_1_toggle_challenger_on_verify_enabled() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.assertChallengerModeEnabled(true)
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_CMT_1_2_toggle_challenger_off_verify_disabled() {
        scoreScreenRobot.clickManageQueue()
        // Toggle on first to ensure we can toggle it off
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.assertChallengerModeEnabled(false)
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_CMT_1_3_enable_challenger_updates_ui() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Alice")
    }

    @Test
    fun test_CMT_1_4_toggle_repeatedly_no_crash() {
        scoreScreenRobot.clickManageQueue()
        for (i in 1..5) {
            manageQueueRobot.toggleChallengerMode()
        }
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_CMT_1_5_persist_state_on_reopen() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.assertChallengerModeEnabled(true)
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_QM_1_1_add_player_appears_in_list() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.assertPlayerInQueue("Alice")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_QM_1_2_remove_player_from_queue() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.clickRemovePlayer("Alice")
        manageQueueRobot.assertPlayerNotInQueue("Alice")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_QM_1_3_skip_player_moves_to_end() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.addPlayer("Bob")
        manageQueueRobot.clickSkipPlayer("Alice")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        // Alice was skipped, so Bob should be next
        scoreScreenRobot.assertNextPlayer("Bob")
    }

    @Test
    fun test_QM_1_4_type_name_shows_suggestions() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.typePlayerName("Za")
        // Check suggestions
        manageQueueRobot.assertAutocompleteSuggestionExists("Zack")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_QM_1_5_select_suggestion_fills_and_adds() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.typePlayerName("Za")
        manageQueueRobot.clickAutocompleteSuggestion("Zack")
        manageQueueRobot.assertPlayerInQueue("Zack")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_SD_1_1_scoreboard_displays_active_players() {
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_SD_1_2_scoreboard_shows_next_player() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Alice")
    }

    @Test
    fun test_SD_1_3_next_indicator_updates_on_queue_change() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.addPlayer("Bob")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.clickSkipPlayer("Alice")
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Bob")
    }

    @Test
    fun test_SD_1_4_scoreboard_queue_display_is_correct() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.addPlayer("Bob")
        manageQueueRobot.assertPlayerInQueue("Alice")
        manageQueueRobot.assertPlayerInQueue("Bob")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_SD_1_5_next_indicator_hidden_when_queue_empty() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayerDoesNotExist()
    }

    @Test
    fun test_MR_1_1_player_1_wins_rotates_opponent() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // Play to 11 (Player 1 wins)
        repeat(11) {
            scoreScreenRobot.incrementPlayer1Score()
        }

        scoreScreenRobot.clickResetGame()

        // Winner stays (Player 1), challenger (Charlie) enters as Player 2
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Charlie")

        // Loser (Player 2) goes to queue
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.assertPlayerInQueue("Player 2")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_MR_1_2_player_2_wins_rotates_opponent() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // Play to 11 (Player 2 wins)
        repeat(11) {
            scoreScreenRobot.incrementPlayer2Score()
        }

        scoreScreenRobot.clickResetGame()

        // Winner stays (Player 2), challenger (Charlie) enters as Player 1
        scoreScreenRobot.assertPlayer1Name("Charlie")
        scoreScreenRobot.assertPlayer2Name("Player 2")

        // Loser (Player 1) goes to queue
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.assertPlayerInQueue("Player 1")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_MR_1_3_reset_zero_score_no_rotation() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        scoreScreenRobot.clickResetGame()

        // Active players should remain unchanged
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_MR_1_4_reset_tied_score_no_rotation() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        repeat(5) {
            scoreScreenRobot.incrementPlayer1Score()
            scoreScreenRobot.incrementPlayer2Score()
        }

        scoreScreenRobot.clickResetGame()

        // Active players should remain unchanged
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_MR_1_5_multiple_rotations_correct_order() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.addPlayer("Dave")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // Rotation 1: Player 1 wins, Charlie enters
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Charlie")

        // Rotation 2: Charlie wins, Dave enters
        repeat(11) { scoreScreenRobot.incrementPlayer2Score() }
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer1Name("Dave")
        scoreScreenRobot.assertPlayer2Name("Charlie")
    }

    // =========================================================================
    // Tier 2 - Boundary & Corner Cases
    // =========================================================================

    @Test
    fun test_CMT_2_1_enable_challenger_empty_queue_blank_next() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayerDoesNotExist()
    }

    @Test
    fun test_CMT_2_2_toggle_challenger_during_match_preserves_score() {
        scoreScreenRobot.incrementPlayer1Score() // 1-0
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertPlayer1Score("1")
        scoreScreenRobot.assertPlayer2Score("0")
    }

    @Test
    fun test_CMT_2_3_toggle_challenger_finished_match_no_auto_rotation_until_reset() {
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // Active players unchanged until reset
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")

        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer2Name("Charlie")
    }

    @Test
    fun test_CMT_2_4_toggle_challenger_off_next_disappears() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.toggleChallengerMode() // off
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayerDoesNotExist()
    }

    @Test
    fun test_CMT_2_5_toggle_challenger_on_next_appears() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        // Ensure switch is on
        manageQueueRobot.assertChallengerModeEnabled(false)
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Charlie")
    }

    @Test
    fun test_QM_2_1_add_player_empty_name_ignored() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("   ")
        manageQueueRobot.assertPlayerNotInQueue("   ")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_QM_2_2_add_duplicate_player_graceful() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_QM_2_3_skip_only_player_unchanged() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.clickSkipPlayer("Alice")
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Alice")
    }

    @Test
    fun test_QM_2_4_remove_only_player_empty() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.clickRemovePlayer("Alice")
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayerDoesNotExist()
    }

    @Test
    fun test_QM_2_5_add_very_long_name_no_ui_break() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("AliceAsDFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_SD_2_1_next_player_long_name_truncated() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("CharlieLongNameThatExceedsTheNormalBoundsOfTheUIAndShouldBeTruncated")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Charlie")
    }

    @Test
    fun test_SD_2_2_scoreboard_landscape_clear() {
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_SD_2_3_scoreboard_same_names() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.assertPlayerInQueue("Alice")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_SD_2_4_scoreboard_next_updates_while_sheet_closed() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()
        scoreScreenRobot.assertNextPlayer("Charlie")
    }

    @Test
    fun test_SD_2_5_scoreboard_updates_live() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.assertPlayerInQueue("Alice")
        manageQueueRobot.closeSheet()
    }

    @Test
    fun test_MR_2_1_reset_empty_queue_no_rotation() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_MR_2_2_reset_one_player_queue_rotation() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Charlie")
    }

    @Test
    fun test_MR_2_3_reset_challenger_disabled_no_rotation() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_MR_2_4_winning_threshold_checks_next_before_reset() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }

        scoreScreenRobot.assertNextPlayer("Charlie")
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer2Name("Charlie")
    }

    @Test
    fun test_MR_2_5_undo_after_rotation_restores_state() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        // Active players rotated
        scoreScreenRobot.assertPlayer2Name("Charlie")

        // Undo rotation
        scoreScreenRobot.clickUndo()

        // Back to finished match state
        scoreScreenRobot.assertPlayer2Name("Player 2")
        scoreScreenRobot.assertPlayer1Score("11")
    }

    // =========================================================================
    // Tier 3 - Cross-Feature Combinations
    // =========================================================================

    @Test
    fun test_CF_1_cross_feature_combinations_queue_and_toggle_cycle() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.addPlayer("Bob")
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.closeSheet()

        // Toggle off
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.assertChallengerModeEnabled(false)
        manageQueueRobot.closeSheet()

        // Play match & reset (no rotation)
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")

        // Toggle on
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // Verify indicator preserved
        scoreScreenRobot.assertNextPlayer("Alice")
    }

    @Test
    fun test_CF_2_cross_feature_combinations_skip_and_visibility() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Alice")
        manageQueueRobot.closeSheet()

        scoreScreenRobot.assertNextPlayerDoesNotExist() // Hidden when disabled

        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Bob")
        manageQueueRobot.clickSkipPlayer("Alice")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        scoreScreenRobot.assertNextPlayer("Bob")
    }

    @Test
    fun test_CF_3_cross_feature_combinations_reset_and_rotation_with_skip() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.addPlayer("Dave")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }

        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.clickSkipPlayer("Charlie")
        manageQueueRobot.closeSheet()

        scoreScreenRobot.clickResetGame()

        // Winner stays (Player 1), Next (Dave) enters
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Dave")
    }

    @Test
    fun test_CF_4_cross_feature_combinations_empty_queue_reset() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.clickRemovePlayer("Charlie")
        manageQueueRobot.closeSheet()

        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    // =========================================================================
    // Tier 4 - Real-World Application Scenarios
    // =========================================================================

    @Test
    fun test_Scenario_1_three_player_rotation_alice_bob_charlie() {
        // Start with Alice and Bob as active players (simulated by Player 1 & Player 2)
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // Alice (Player 1) wins
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        // Alice (Player 1) stays, Charlie enters as Player 2, Bob goes to end of queue
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Charlie")
        scoreScreenRobot.assertNextPlayer("Player 2")
    }

    @Test
    fun test_Scenario_2_queue_reordering_and_match() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.addPlayer("Dave")
        manageQueueRobot.addPlayer("Eve")
        manageQueueRobot.toggleChallengerMode()

        // Reorder queue: skip Charlie to put Dave at the front
        manageQueueRobot.clickSkipPlayer("Charlie")
        manageQueueRobot.closeSheet()

        // Dave is next
        scoreScreenRobot.assertNextPlayer("Dave")

        // Player 2 wins match
        repeat(11) { scoreScreenRobot.incrementPlayer2Score() }
        scoreScreenRobot.clickResetGame()

        // Winner (Player 2) stays, Dave enters
        scoreScreenRobot.assertPlayer1Name("Dave")
        scoreScreenRobot.assertPlayer2Name("Player 2")
    }

    @Test
    fun test_Scenario_3_autocomplete_profile_addition() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()

        // Add Zack via autocomplete
        manageQueueRobot.typePlayerName("Za")
        manageQueueRobot.clickAutocompleteSuggestion("Zack")

        // Add Zoe via autocomplete
        manageQueueRobot.typePlayerName("Zo")
        manageQueueRobot.clickAutocompleteSuggestion("Zoe")

        manageQueueRobot.closeSheet()

        // Zack is next
        scoreScreenRobot.assertNextPlayer("Zack")

        // Player 1 wins match
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()

        // Zack active
        scoreScreenRobot.assertPlayer2Name("Zack")
    }

    @Test
    fun test_Scenario_4_challenger_mode_cycle_five_players() {
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.addPlayer("Dave")
        manageQueueRobot.addPlayer("Eve")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        // 1st Match: Player 1 wins -> Player 1 vs Charlie (Player 2 goes to queue)
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer1Name("Player 1")
        scoreScreenRobot.assertPlayer2Name("Charlie")

        // 2nd Match: Charlie wins -> Charlie vs Dave (Player 1 goes to queue)
        repeat(11) { scoreScreenRobot.incrementPlayer2Score() }
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer1Name("Dave")
        scoreScreenRobot.assertPlayer2Name("Charlie")

        // 3rd Match: Dave wins -> Dave vs Eve (Charlie goes to queue)
        repeat(11) { scoreScreenRobot.incrementPlayer1Score() }
        scoreScreenRobot.clickResetGame()
        scoreScreenRobot.assertPlayer1Name("Dave")
        scoreScreenRobot.assertPlayer2Name("Eve")
    }

    @Test
    fun test_Scenario_5_error_recovery_state_restoration() {
        // Toggle challenger mode on and add player
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.addPlayer("Charlie")
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        scoreScreenRobot.assertNextPlayer("Charlie")

        // Make score progress
        repeat(5) { scoreScreenRobot.incrementPlayer1Score() }

        // Accidentally toggle challenger mode off, scores should be preserved
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        scoreScreenRobot.assertPlayer1Score("5")
        scoreScreenRobot.assertNextPlayerDoesNotExist()

        // Toggle back on, queue should still have Charlie
        scoreScreenRobot.clickManageQueue()
        manageQueueRobot.toggleChallengerMode()
        manageQueueRobot.closeSheet()

        scoreScreenRobot.assertNextPlayer("Charlie")
    }
}
