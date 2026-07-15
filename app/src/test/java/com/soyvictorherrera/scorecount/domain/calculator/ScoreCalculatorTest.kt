package com.soyvictorherrera.scorecount.domain.calculator

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.model.ServingRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScoreCalculatorTest {
    private lateinit var player1: Player
    private lateinit var player2: Player
    private lateinit var defaultSettings: GameSettings
    private lateinit var initialState: GameState

    @BeforeEach
    fun setup() {
        player1 = Player(id = 1, name = "Player 1", score = 0)
        player2 = Player(id = 2, name = "Player 2", score = 0)

        defaultSettings =
            GameSettings(
                pointsToWinSet = 11,
                winByTwo = true,
                numberOfSets = 5,
                serveRotationAfterPoints = 2,
                serveChangeAfterDeuce = 1,
                servingRule = ServingRule.WINNER_SERVES
            )

        initialState =
            GameState(
                player1 = player1,
                player2 = player2,
                servingPlayerId = player1.id,
                player1SetsWon = 0,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = false
            )
    }

    @Test
    fun `incrementScore increases player score`() {
        val newState = ScoreCalculator.incrementScore(initialState, defaultSettings, player1.id)

        assertEquals(1, newState.player1.score)
        assertEquals(0, newState.player2.score)
    }

    @Test
    fun `incrementScore does not change state if game is finished`() {
        val finishedState = initialState.copy(isFinished = true)
        val newState = ScoreCalculator.incrementScore(finishedState, defaultSettings, player1.id)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `incrementScore detects deuce at 10-10`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 9)
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player2.id)

        assertTrue(newState.isDeuce)
        assertEquals(10, newState.player1.score)
        assertEquals(10, newState.player2.score)
    }

    @Test
    fun `incrementScore wins set at 11-9 without deuce`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 9)
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)

        assertEquals(0, newState.player1.score) // Reset after set win
        assertEquals(0, newState.player2.score)
        assertEquals(1, newState.player1SetsWon)
        assertEquals(0, newState.player2SetsWon)
        assertFalse(newState.isFinished)
    }

    @Test
    fun `incrementScore requires win by 2 in deuce`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10),
                isDeuce = true
            )

        // Score 11-10, should not win yet
        val afterP1Scores = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(11, afterP1Scores.player1.score)
        assertEquals(10, afterP1Scores.player2.score)
        assertEquals(0, afterP1Scores.player1SetsWon) // No set won yet

        // Score 12-10, should win
        val afterP1ScoresAgain = ScoreCalculator.incrementScore(afterP1Scores, defaultSettings, player1.id)
        assertEquals(0, afterP1ScoresAgain.player1.score) // Reset after win
        assertEquals(0, afterP1ScoresAgain.player2.score)
        assertEquals(1, afterP1ScoresAgain.player1SetsWon)
    }

    @Test
    fun `incrementScore finishes match when player wins 3 sets in best of 5`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                player1SetsWon = 2,
                player2SetsWon = 1
            )

        val newState = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)

        assertEquals(3, newState.player1SetsWon)
        assertTrue(newState.isFinished)
    }

    @Test
    fun `server rotates every 2 points`() {
        var state = initialState

        // Point 1: P1 serves (total=1, 1%2!=0, no rotation, P1 serves)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 2: After 2 points scored, rotate (total=2, 2%2==0, rotate to P2)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 3: P2 serves (total=3, 3%2!=0, no rotation, P2 serves)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 4: After 4 points total, rotate (total=4, 4%2==0, rotate to P1)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 5: P1 serves (total=5, 5%2!=0, no rotation, P1 serves)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)
    }

    @Test
    fun `server rotates every point in deuce`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10),
                isDeuce = true,
                servingPlayerId = player1.id
            )

        // First point in deuce: P2 serves (rotation after every point)
        val afterFirst = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, afterFirst.servingPlayerId)

        // Second point in deuce: P1 serves
        val afterSecond = ScoreCalculator.incrementScore(afterFirst, defaultSettings, player2.id)
        assertEquals(player1.id, afterSecond.servingPlayerId)
    }

    @Test
    fun `winner serves next set when servingRule is WINNER_SERVES`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.WINNER_SERVES)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                servingPlayerId = player1.id
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        // Player 1 won the set, should serve first in next set
        assertEquals(player1.id, newState.servingPlayerId)
    }

    @Test
    fun `loser serves next set when servingRule is LOSER_SERVES`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.LOSER_SERVES)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                servingPlayerId = player1.id
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        // Player 1 won, but loser (P2) should serve next set
        assertEquals(player2.id, newState.servingPlayerId)
    }

    @Test
    fun `player 1 serves next set when servingRule is PLAYER_ONE_SERVES`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.PLAYER_ONE_SERVES)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 5),
                servingPlayerId = player2.id // Player 2 was serving
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        // Player 1 won, but Player 1 always serves next set regardless
        assertEquals(player1.id, newState.servingPlayerId)

        // Test when player 2 wins
        val state2 =
            initialState.copy(
                player1 = player1.copy(score = 5),
                player2 = player2.copy(score = 10),
                servingPlayerId = player1.id
            )

        val newState2 = ScoreCalculator.incrementScore(state2, settings, player2.id)

        // Player 2 won, but Player 1 still serves next set
        assertEquals(player1.id, newState2.servingPlayerId)
    }

    @Test
    fun `decrementScore reduces player score`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 5),
                player2 = player2.copy(score = 3)
            )

        val newState = ScoreCalculator.decrementScore(state, player1.id, defaultSettings)

        assertEquals(4, newState.player1.score)
        assertEquals(3, newState.player2.score)
    }

    @Test
    fun `decrementScore does not go below zero`() {
        val state =
            initialState.copy(
                player1 = player1.copy(score = 0),
                player2 = player2.copy(score = 3)
            )

        val newState = ScoreCalculator.decrementScore(state, player1.id, defaultSettings)

        assertEquals(0, newState.player1.score)
    }

    @Test
    fun `decrementScore does not change state if game is finished`() {
        val finishedState =
            initialState.copy(
                isFinished = true,
                player1 = player1.copy(score = 5)
            )

        val newState = ScoreCalculator.decrementScore(finishedState, player1.id, defaultSettings)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `decrementScore recalculates deuce state correctly`() {
        // Start at 11-11 (deuce)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 11),
                player2 = player2.copy(score = 11),
                isDeuce = true
            )

        // Decrement player 1's score to 10-11 (still deuce: both >= 10)
        val newState1 = ScoreCalculator.decrementScore(state, player1.id, defaultSettings)

        assertEquals(10, newState1.player1.score)
        assertEquals(11, newState1.player2.score)
        assertTrue(newState1.isDeuce) // Still deuce since both >= 10

        // Decrement again to 9-11 (no longer deuce)
        val newState2 = ScoreCalculator.decrementScore(newState1, player1.id, defaultSettings)

        assertEquals(9, newState2.player1.score)
        assertEquals(11, newState2.player2.score)
        assertFalse(newState2.isDeuce) // Not deuce anymore
    }

    @Test
    fun `decrementScore transitions from deuce to non-deuce state`() {
        // Start in deuce at 10-10
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10),
                isDeuce = true
            )

        // Decrement player 2's score to 10-9 (no longer deuce)
        val newState = ScoreCalculator.decrementScore(state, player2.id, defaultSettings)

        assertEquals(10, newState.player1.score)
        assertEquals(9, newState.player2.score)
        assertFalse(newState.isDeuce)
    }

    @Test
    fun `incrementScore resets deuce state when set ends after deuce`() {
        // Scenario: Set reaches deuce at 10-10, then player 1 wins 11-9
        val deuceState =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10),
                isDeuce = true
            )

        // Player 1 scores to 11-10 (still deuce)
        val afterFirstPoint = ScoreCalculator.incrementScore(deuceState, defaultSettings, player1.id)
        assertTrue(afterFirstPoint.isDeuce)

        // Player 1 scores to win the set 12-10
        val setWon = ScoreCalculator.incrementScore(afterFirstPoint, defaultSettings, player1.id)

        // Set should be won, scores reset to 0-0, and deuce should be false
        assertEquals(1, setWon.player1SetsWon)
        assertEquals(0, setWon.player2SetsWon)
        assertEquals(0, setWon.player1.score)
        assertEquals(0, setWon.player2.score)
        assertFalse(setWon.isDeuce) // Deuce should be reset when new set starts
    }

    @Test
    fun `switchServe alternates serving player`() {
        val state = initialState.copy(servingPlayerId = player1.id)

        val afterSwitch1 = ScoreCalculator.switchServe(state)
        assertEquals(player2.id, afterSwitch1.servingPlayerId)

        val afterSwitch2 = ScoreCalculator.switchServe(afterSwitch1)
        assertEquals(player1.id, afterSwitch2.servingPlayerId)
    }

    @Test
    fun `switchServe does not change state if game is finished`() {
        val finishedState =
            initialState.copy(
                isFinished = true,
                servingPlayerId = player1.id
            )

        val newState = ScoreCalculator.switchServe(finishedState)

        assertEquals(finishedState, newState)
    }

    @Test
    fun `resetGame creates initial state with player 1 serving by default`() {
        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = defaultSettings
            )

        assertEquals(1, newState.player1.id)
        assertEquals("Alice", newState.player1.name)
        assertEquals(0, newState.player1.score)

        assertEquals(2, newState.player2.id)
        assertEquals("Bob", newState.player2.name)
        assertEquals(0, newState.player2.score)

        assertEquals(1, newState.servingPlayerId)
        assertEquals(0, newState.player1SetsWon)
        assertEquals(0, newState.player2SetsWon)
        assertFalse(newState.isDeuce)
        assertFalse(newState.isFinished)
    }

    @Test
    fun `resetGame respects lastGameWinnerId when winnerServesNextGame is true`() {
        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = defaultSettings,
                lastGameWinnerId = 2
            )

        assertEquals(2, newState.servingPlayerId)
    }

    @Test
    fun `resetGame uses loser when servingRule is LOSER_SERVES`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.LOSER_SERVES)

        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = settings,
                lastGameWinnerId = 2
            )

        assertEquals(1, newState.servingPlayerId) // Loser serves
    }

    @Test
    fun `resetGame with challengerMode rotates player 2 out when player 1 wins`() {
        val settings = defaultSettings.copy(challengerMode = true)
        val queue =
            listOf(
                Player(id = 3, name = "Charlie"),
                Player(id = 4, name = "Dave")
            )

        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = settings,
                lastGameWinnerId = 1,
                completedGames = 1,
                challengerQueue = queue,
                isFinished = true
            )

        // Winner (Alice/1) stays on
        assertEquals(1, newState.player1.id)
        assertEquals("Alice", newState.player1.name)
        assertEquals(0, newState.player1.score)

        // Loser (Bob/2) is rotated out, next in queue (Charlie/3) steps up
        assertEquals(3, newState.player2.id)
        assertEquals("Charlie", newState.player2.name)
        assertEquals(0, newState.player2.score)

        // Loser is appended to the end of the queue
        assertEquals(2, newState.challengerQueue.size)
        assertEquals(4, newState.challengerQueue[0].id)
        assertEquals("Dave", newState.challengerQueue[0].name)
        assertEquals(2, newState.challengerQueue[1].id)
        assertEquals("Bob", newState.challengerQueue[1].name)
    }

    @Test
    fun `resetGame with challengerMode rotates player 1 out when player 2 wins`() {
        val settings = defaultSettings.copy(challengerMode = true)
        val queue =
            listOf(
                Player(id = 3, name = "Charlie"),
                Player(id = 4, name = "Dave")
            )

        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = settings,
                lastGameWinnerId = 2,
                completedGames = 1,
                challengerQueue = queue,
                isFinished = true
            )

        // Loser (Alice/1) is rotated out, next in queue (Charlie/3) steps up
        assertEquals(3, newState.player1.id)
        assertEquals("Charlie", newState.player1.name)
        assertEquals(0, newState.player1.score)

        // Winner (Bob/2) stays on
        assertEquals(2, newState.player2.id)
        assertEquals("Bob", newState.player2.name)
        assertEquals(0, newState.player2.score)

        // Loser is appended to the end of the queue
        assertEquals(2, newState.challengerQueue.size)
        assertEquals(4, newState.challengerQueue[0].id)
        assertEquals("Dave", newState.challengerQueue[0].name)
        assertEquals(1, newState.challengerQueue[1].id)
        assertEquals("Alice", newState.challengerQueue[1].name)
    }

    @Test
    fun `resetGame with challengerMode does not rotate when queue is empty`() {
        val settings = defaultSettings.copy(challengerMode = true)

        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = settings,
                lastGameWinnerId = 1,
                completedGames = 1,
                challengerQueue = emptyList(),
                isFinished = true
            )

        assertEquals(1, newState.player1.id)
        assertEquals(2, newState.player2.id)
        assertTrue(newState.challengerQueue.isEmpty())
    }

    @Test
    fun `resetGame with challengerMode does not rotate when match is not finished`() {
        val settings = defaultSettings.copy(challengerMode = true)
        val queue = listOf(Player(id = 3, name = "Charlie"))

        val newState =
            ScoreCalculator.resetGame(
                player1Id = 1,
                player2Id = 2,
                player1Name = "Alice",
                player2Name = "Bob",
                settings = settings,
                lastGameWinnerId = 1,
                completedGames = 1,
                challengerQueue = queue,
                isFinished = false
            )

        assertEquals(1, newState.player1.id)
        assertEquals(2, newState.player2.id)
        assertEquals(queue, newState.challengerQueue)
    }

    @Test
    fun `best of 3 sets match finishes at 2 sets won`() {
        val settings = defaultSettings.copy(numberOfSets = 3)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player1SetsWon = 1,
                player2SetsWon = 0
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        assertEquals(2, newState.player1SetsWon)
        assertTrue(newState.isFinished)
    }

    @Test
    fun `custom serve rotation interval works correctly`() {
        val settings = defaultSettings.copy(serveRotationAfterPoints = 5)
        var state = initialState

        // Score 5 points, after 5th point should rotate to P2
        repeat(5) {
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
        }
        assertEquals(player2.id, state.servingPlayerId)

        // On 6th point, P2 still serves
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)
    }

    @Test
    fun `win by two disabled allows win at exact points to win`() {
        val settings = defaultSettings.copy(winByTwo = false)
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 10)
            )

        val newState = ScoreCalculator.incrementScore(state, settings, player1.id)

        // Should win immediately at 11-10
        assertEquals(0, newState.player1.score) // Reset after win
        assertEquals(1, newState.player1SetsWon)
        assertFalse(newState.isDeuce) // No deuce when winByTwo is false
    }

    @Test
    fun `invalid player id does not change state`() {
        val newState = ScoreCalculator.incrementScore(initialState, defaultSettings, 999)

        assertEquals(initialState, newState)
    }

    @Test
    fun `determineWinner returns player 1 id when player 1 has more sets`() {
        val state =
            initialState.copy(
                player1SetsWon = 3,
                player2SetsWon = 1
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(player1.id, winnerId)
    }

    @Test
    fun `determineWinner returns player 2 id when player 2 has more sets`() {
        val state =
            initialState.copy(
                player1SetsWon = 1,
                player2SetsWon = 3
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(player2.id, winnerId)
    }

    @Test
    fun `determineWinner returns null when sets are tied`() {
        val state =
            initialState.copy(
                player1SetsWon = 2,
                player2SetsWon = 2
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(null, winnerId)
    }

    @Test
    fun `determineWinner returns null when both players have zero sets`() {
        val state =
            initialState.copy(
                player1SetsWon = 0,
                player2SetsWon = 0
            )

        val winnerId = ScoreCalculator.determineWinner(state)

        assertEquals(null, winnerId)
    }

    // Bug #42: Serve rotation with non-standard "Set to" values
    // Helper function to test serve rotation with different settings
    private fun testServeRotationPattern(
        pointsToWinSet: Int,
        serveRotationAfter: Int
    ) {
        val settings =
            defaultSettings.copy(
                pointsToWinSet = pointsToWinSet,
                serveRotationAfterPoints = serveRotationAfter
            )
        var state = initialState

        // Test multiple rotations to verify pattern
        val pointsToTest = serveRotationAfter * 3 // Test 3 full rotations

        for (pointNum in 1..pointsToTest) {
            state = ScoreCalculator.incrementScore(state, settings, player1.id)

            // Determine expected server based on rotation pattern
            val completedIntervals = pointNum / serveRotationAfter
            val expectedServer = if (completedIntervals % 2 == 0) player1.id else player2.id

            assertEquals(
                expectedServer,
                state.servingPlayerId,
                "Point $pointNum (after $completedIntervals rotations): " +
                    "Expected ${if (completedIntervals % 2 == 0) "P1" else "P2"} to serve"
            )
        }
    }

    @Test
    fun `serve rotation works with various game settings`() {
        // Test different combinations of pointsToWinSet and serveRotationAfter
        testServeRotationPattern(pointsToWinSet = 7, serveRotationAfter = 2)
        testServeRotationPattern(pointsToWinSet = 11, serveRotationAfter = 2)
        testServeRotationPattern(pointsToWinSet = 21, serveRotationAfter = 5)
        testServeRotationPattern(pointsToWinSet = 15, serveRotationAfter = 3)
    }

    @Test
    fun `serve rotation alternates correctly with competitive scores`() {
        val settings = defaultSettings.copy(pointsToWinSet = 7, serveRotationAfterPoints = 2)
        var state = initialState

        // Simulate a back-and-forth game
        // Point 1: P1 serves, P1 scores (1-0)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 2: P1 serves, P1 scores (2-0) -> Rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 3: P2 serves, P2 scores (2-1)
        state = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Point 4: P2 serves, P2 scores (2-2) -> Rotate to P1
        state = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 5: P1 serves, P1 scores (3-2)
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId)

        // Point 6: P1 serves, P2 scores (3-3) -> Rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertEquals(player2.id, state.servingPlayerId)

        // Verify correct score
        assertEquals(3, state.player1.score)
        assertEquals(3, state.player2.score)
    }

    @Test
    fun `deuce transition maintains correct server with Set to 7`() {
        val settings = defaultSettings.copy(pointsToWinSet = 7, serveRotationAfterPoints = 2)

        // Set up state: 6-5, total 11 points played
        // Pattern: P1 served 1-2, P2 served 3-4, P1 served 5-6, P2 served 7-8, P1 served 9-10, P2 serves 11-12
        val state =
            initialState.copy(
                player1 = player1.copy(score = 6),
                player2 = player2.copy(score = 5),
                servingPlayerId = player2.id // P2 should be serving (points 11-12)
            )

        // P2 scores to make it 6-6 (total = 12, deuce)
        val atDeuce = ScoreCalculator.incrementScore(state, settings, player2.id)
        assertTrue(atDeuce.isDeuce, "Should be in deuce at 6-6")

        // In deuce, serve should rotate every point
        // Since P2 just served point 12, next point (13) should be P1
        assertEquals(player1.id, atDeuce.servingPlayerId, "At deuce, should rotate to P1")

        // Point 14 in deuce: should rotate to P2
        val afterDeuce1 = ScoreCalculator.incrementScore(atDeuce, settings, player1.id) // 7-6
        assertEquals(player2.id, afterDeuce1.servingPlayerId, "Deuce point 2: should rotate to P2")
    }

    @Test
    fun `deuce transition at 10-10 with standard settings`() {
        // State at 10-9, total 19 points
        // Pattern: P1 (1-2), P2 (3-4), P1 (5-6), P2 (7-8), P1 (9-10), P2 (11-12),
        // P1 (13-14), P2 (15-16), P1 (17-18), P2 serves 19-20
        val state =
            initialState.copy(
                player1 = player1.copy(score = 10),
                player2 = player2.copy(score = 9),
                servingPlayerId = player2.id // P2 serving points 19-20
            )

        // P2 scores to make it 10-10 (total = 20, enters deuce)
        val atDeuce = ScoreCalculator.incrementScore(state, defaultSettings, player2.id)
        assertTrue(atDeuce.isDeuce, "Should be in deuce at 10-10")

        // P2 just served point 20, in deuce serve rotates every point, so P1 should serve next
        assertEquals(player1.id, atDeuce.servingPlayerId, "At 10-10 deuce, should rotate to P1")

        // Next point: should rotate to P2
        val afterDeuce1 = ScoreCalculator.incrementScore(atDeuce, defaultSettings, player1.id) // 11-10
        assertEquals(player2.id, afterDeuce1.servingPlayerId, "Deuce continues: should rotate to P2")
    }

    @Test
    fun `serve rotation with interval of 3`() {
        val settings = defaultSettings.copy(serveRotationAfterPoints = 3)
        var state = initialState

        // Points 1-2: P1 serves
        repeat(2) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player1.id, state.servingPlayerId, "Point ${i + 1}: P1 should serve")
        }

        // Point 3: After 3 points, rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 3: Rotate to P2")

        // Points 4-5: P2 serves
        repeat(2) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player2.id, state.servingPlayerId, "Point ${i + 4}: P2 should serve")
        }

        // Point 6: After 6 points, rotate to P1
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player1.id, state.servingPlayerId, "Point 6: Rotate to P1")

        // Points 7-8: P1 serves
        repeat(2) { i ->
            state = ScoreCalculator.incrementScore(state, settings, player1.id)
            assertEquals(player1.id, state.servingPlayerId, "Point ${i + 7}: P1 should serve")
        }

        // Point 9: After 9 points, rotate to P2
        state = ScoreCalculator.incrementScore(state, settings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Point 9: Rotate to P2")
    }

    // Bug #43: Reset game should always reset serve indicator to Player 1
    @Test
    fun `consecutive resets always set serve indicator to Player 1`() {
        // Simulate playing to a point where Player 2 is serving
        var state = initialState
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        state = ScoreCalculator.incrementScore(state, defaultSettings, player1.id)
        assertEquals(player2.id, state.servingPlayerId, "Player 2 should be serving after rotation")

        // First reset - should reset to Player 1
        val reset1 =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = defaultSettings.copy(servingRule = ServingRule.PLAYER_ONE_SERVES)
            )
        assertEquals(player1.id, reset1.servingPlayerId, "First reset should set server to Player 1")

        // Second consecutive reset - should STILL reset to Player 1, not toggle
        val reset2 =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = defaultSettings.copy(servingRule = ServingRule.PLAYER_ONE_SERVES)
            )
        assertEquals(player1.id, reset2.servingPlayerId, "Second reset should set server to Player 1")

        // Third consecutive reset - verify pattern holds
        val reset3 =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = defaultSettings.copy(servingRule = ServingRule.PLAYER_ONE_SERVES)
            )
        assertEquals(player1.id, reset3.servingPlayerId, "Third reset should set server to Player 1")
    }

    @Test
    fun `reset from Player 1 serving maintains Player 1 as server`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.PLAYER_ONE_SERVES)

        // Reset when Player 1 is already serving
        val reset =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = settings
            )

        assertEquals(player1.id, reset.servingPlayerId, "Should always reset to Player 1")
    }

    @Test
    fun `reset from Player 2 serving resets to Player 1`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.PLAYER_ONE_SERVES)

        // Reset regardless of who was serving before
        val reset =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = settings
            )

        assertEquals(player1.id, reset.servingPlayerId, "Should always reset to Player 1")
    }

    @Test
    fun `reset with servingRule WINNER_SERVES uses lastGameWinnerId`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.WINNER_SERVES)

        // Player 2 won last game, should serve next
        val reset =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = settings,
                lastGameWinnerId = player2.id
            )

        assertEquals(
            player2.id,
            reset.servingPlayerId,
            "Winner should serve when servingRule is WINNER_SERVES"
        )
    }

    @Test
    fun `reset with servingRule ALTERNATE game 1 player1 serves`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.ALTERNATE)

        // Game 1 (completedGames = 0): Player 1 should serve
        val reset =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = settings,
                lastGameWinnerId = null,
                completedGames = 0
            )

        assertEquals(
            player1.id,
            reset.servingPlayerId,
            "Player 1 should serve in game 1 with ALTERNATE rule"
        )
    }

    @Test
    fun `reset with servingRule ALTERNATE game 2 player2 serves`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.ALTERNATE)

        // Game 2 (completedGames = 1): Player 2 should serve
        val reset =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = settings,
                lastGameWinnerId = player1.id,
                completedGames = 1
            )

        assertEquals(
            player2.id,
            reset.servingPlayerId,
            "Player 2 should serve in game 2 with ALTERNATE rule"
        )
    }

    @Test
    fun `reset with servingRule ALTERNATE game 3 player1 serves`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.ALTERNATE)

        // Game 3 (completedGames = 2): Player 1 should serve again
        val reset =
            ScoreCalculator.resetGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1Name = player1.name,
                player2Name = player2.name,
                settings = settings,
                lastGameWinnerId = player2.id,
                completedGames = 2
            )

        assertEquals(
            player1.id,
            reset.servingPlayerId,
            "Player 1 should serve in game 3 with ALTERNATE rule"
        )
    }

    @Test
    fun `alternate serves next set when servingRule is ALTERNATE`() {
        val settings = defaultSettings.copy(servingRule = ServingRule.ALTERNATE)

        // Game 1 ends, game 2 starts (completedGames = 1): Player 2 serves
        val state1 =
            initialState.copy(
                player1 = player1.copy(score = 11),
                player2 = player2.copy(score = 5),
                servingPlayerId = player1.id
            )

        val result1 = ScoreCalculator.incrementScore(state1, settings, player1.id)
        assertEquals(
            player2.id,
            result1.servingPlayerId,
            "Player 2 should serve second game (completedGames=1 after first game ends)"
        )
        assertEquals(1, result1.player1SetsWon, "Player 1 should have won first set")

        // Game 2 ends, game 3 starts (completedGames = 2): Player 1 serves
        val state2 = result1.copy(player1 = player1.copy(score = 11), player2 = player2.copy(score = 5))
        val result2 = ScoreCalculator.incrementScore(state2, settings, player1.id)
        assertEquals(
            player1.id,
            result2.servingPlayerId,
            "Player 1 should serve third game (completedGames=2 after second game ends)"
        )
        assertEquals(2, result2.player1SetsWon, "Player 1 should have won second set")

        // Game 3 ends, game 4 starts (completedGames = 3): Player 2 serves
        val state3 = result2.copy(player1 = player1.copy(score = 5), player2 = player2.copy(score = 11))
        val result3 = ScoreCalculator.incrementScore(state3, settings, player2.id)
        assertEquals(
            player2.id,
            result3.servingPlayerId,
            "Player 2 should serve fourth game (completedGames=3 after third game ends)"
        )
        assertEquals(1, result3.player2SetsWon, "Player 2 should have won third set")
    }
}
