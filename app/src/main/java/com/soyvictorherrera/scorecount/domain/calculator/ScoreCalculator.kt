package com.soyvictorherrera.scorecount.domain.calculator

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.model.ServingRule
import kotlin.math.abs

/**
 * Parameters for determining the next server.
 * Groups related data to reduce parameter list complexity.
 */
private data class ServerDeterminationParams(
    val currentScores: Pair<Int, Int>,
    val currentServingPlayerId: Int,
    val playerIds: Pair<Int, Int>,
    val settings: GameSettings,
    val setEnded: Boolean,
    val lastSetWinnerId: Int?,
    val completedGames: Int = 0
)

/**
 * Pure business logic for score calculation.
 * Contains all game rules for table tennis scoring, server rotation, and match progression.
 * This class has no dependencies and is fully testable.
 */
object ScoreCalculator {
    /**
     * Calculate the new game state after a player scores a point.
     *
     * @param currentState The current game state
     * @param settings The game settings (rules)
     * @param playerId The ID of the player who scored
     * @return The new game state after the point is scored
     */
    fun incrementScore(
        currentState: GameState,
        settings: GameSettings,
        playerId: Int
    ): GameState {
        // Cannot score if game is already finished
        if (currentState.isFinished) return currentState

        // Increment the score for the player who scored
        var newP1Score = currentState.player1.score
        var newP2Score = currentState.player2.score

        when (playerId) {
            currentState.player1.id -> newP1Score++
            currentState.player2.id -> newP2Score++
            else -> return currentState // Invalid player ID
        }

        // Check if a player won the current set
        val p1CanWinSet = newP1Score >= settings.pointsToWinSet
        val p2CanWinSet = newP2Score >= settings.pointsToWinSet
        val scoreDiff = abs(newP1Score - newP2Score)

        val p1WinsSet = p1CanWinSet && (!settings.winByTwo || scoreDiff >= 2)
        val p2WinsSet = p2CanWinSet && (!settings.winByTwo || scoreDiff >= 2)

        var newP1Sets = currentState.player1SetsWon
        var newP2Sets = currentState.player2SetsWon
        var setJustEnded = false
        var lastSetWinnerId: Int? = null

        // Determine deuce state
        var isDeuce = calculateDeuceState(newP1Score, newP2Score, settings)

        // Handle set completion
        if (p1WinsSet || p2WinsSet) {
            if (p1WinsSet) {
                newP1Sets++
                lastSetWinnerId = currentState.player1.id
            } else {
                newP2Sets++
                lastSetWinnerId = currentState.player2.id
            }
            setJustEnded = true
        }

        // Reset scores if set ended
        if (setJustEnded) {
            newP1Score = 0
            newP2Score = 0
            // Recalculate deuce state based on reset scores (should be false at 0-0)
            isDeuce = calculateDeuceState(newP1Score, newP2Score, settings)
        }

        // Check if match is finished
        val setsToWinMatch = (settings.numberOfSets / 2) + 1
        val gameIsFinished = newP1Sets >= setsToWinMatch || newP2Sets >= setsToWinMatch

        // Determine next server
        val newServingPlayerId =
            determineNextServer(
                ServerDeterminationParams(
                    currentScores = Pair(newP1Score, newP2Score),
                    currentServingPlayerId = currentState.servingPlayerId ?: currentState.player1.id,
                    playerIds = Pair(currentState.player1.id, currentState.player2.id),
                    settings = settings,
                    setEnded = setJustEnded,
                    lastSetWinnerId = lastSetWinnerId,
                    completedGames = newP1Sets + newP2Sets
                )
            )

        return currentState.copy(
            player1 = currentState.player1.copy(score = newP1Score),
            player2 = currentState.player2.copy(score = newP2Score),
            player1SetsWon = newP1Sets,
            player2SetsWon = newP2Sets,
            servingPlayerId = newServingPlayerId,
            isDeuce = isDeuce,
            isFinished = gameIsFinished
        )
    }

    /**
     * Calculate the new game state after decrementing a player's score.
     * Note: This is a simplified implementation that only decrements within the current set.
     *
     * @param currentState The current game state
     * @param playerId The ID of the player whose score should be decremented
     * @param settings The game settings (needed to recalculate deuce state)
     * @return The new game state after the score is decremented
     */
    fun decrementScore(
        currentState: GameState,
        playerId: Int,
        settings: GameSettings
    ): GameState {
        // Cannot decrement if game is finished
        if (currentState.isFinished) return currentState

        val newPlayer1 =
            if (playerId == currentState.player1.id && currentState.player1.score > 0) {
                currentState.player1.copy(score = currentState.player1.score - 1)
            } else {
                currentState.player1
            }

        val newPlayer2 =
            if (playerId == currentState.player2.id && currentState.player2.score > 0) {
                currentState.player2.copy(score = currentState.player2.score - 1)
            } else {
                currentState.player2
            }

        // Recalculate deuce state based on new scores
        val isDeuce = calculateDeuceState(newPlayer1.score, newPlayer2.score, settings)

        return currentState.copy(
            player1 = newPlayer1,
            player2 = newPlayer2,
            isDeuce = isDeuce
        )
    }

    /**
     * Switch the serving player manually.
     *
     * @param currentState The current game state
     * @return The new game state with the server switched
     */
    fun switchServe(currentState: GameState): GameState {
        // Cannot switch if game is finished
        if (currentState.isFinished) return currentState

        val nextServer =
            if (currentState.servingPlayerId == currentState.player1.id) {
                currentState.player2.id
            } else {
                currentState.player1.id
            }

        return currentState.copy(servingPlayerId = nextServer)
    }

    /**
     * Reset the game to initial state.
     *
     * @param player1Id The ID of player 1
     * @param player2Id The ID of player 2
     * @param player1Name The name of player 1
     * @param player2Name The name of player 2
     * @param settings The game settings
     * @param lastGameWinnerId Optional: ID of the last game winner
     *   (determines first server when winnerServesNextGame is true)
     * @return A new game state in initial configuration
     */
    fun resetGame(
        player1Id: Int,
        player2Id: Int,
        player1Name: String,
        player2Name: String,
        settings: GameSettings,
        lastGameWinnerId: Int? = null,
        completedGames: Int = 0,
        challengerQueue: List<Player> = emptyList(),
        isFinished: Boolean = false
    ): GameState {
        val (finalPlayer1, finalPlayer2, finalQueue) =
            rotatePlayers(
                players =
                    Pair(
                        Player(id = player1Id, name = player1Name, score = 0),
                        Player(id = player2Id, name = player2Name, score = 0)
                    ),
                settings = settings,
                lastGameWinnerId = lastGameWinnerId,
                challengerQueue = challengerQueue,
                isFinished = isFinished
            )

        val firstServer =
            when {
                // Check serving rule first before null check
                settings.servingRule == ServingRule.PLAYER_ONE_SERVES -> finalPlayer1.id
                settings.servingRule == ServingRule.ALTERNATE -> {
                    // Alternate between players based on completed games
                    // Game 1 (completedGames=0): player1, Game 2 (completedGames=1): player2, etc.
                    if (completedGames % 2 == 0) finalPlayer1.id else finalPlayer2.id
                }
                settings.servingRule == ServingRule.WINNER_SERVES && lastGameWinnerId != null -> lastGameWinnerId
                settings.servingRule == ServingRule.LOSER_SERVES && lastGameWinnerId != null -> {
                    if (lastGameWinnerId == finalPlayer1.id) finalPlayer2.id else finalPlayer1.id
                }
                // Fallback for initial game when no winner is set (WINNER_SERVES or LOSER_SERVES)
                else -> finalPlayer1.id
            }

        return GameState(
            player1 = finalPlayer1,
            player2 = finalPlayer2,
            servingPlayerId = firstServer,
            player1SetsWon = 0,
            player2SetsWon = 0,
            isDeuce = false,
            isFinished = false,
            challengerQueue = finalQueue
        )
    }

    private fun rotatePlayers(
        players: Pair<Player, Player>,
        settings: GameSettings,
        lastGameWinnerId: Int?,
        challengerQueue: List<Player>,
        isFinished: Boolean
    ): Triple<Player, Player, List<Player>> {
        val (player1, player2) = players
        val isRotationActive =
            settings.challengerMode && isFinished && lastGameWinnerId != null && challengerQueue.isNotEmpty()

        return if (isRotationActive) {
            val nextPlayer = challengerQueue.first().copy(score = 0)
            if (lastGameWinnerId == player1.id) {
                // Player 1 won, Player 2 lost.
                // Player 1 stays as Player 1. Next player becomes Player 2.
                // Player 2 goes to the end of the queue.
                val loser = player2.copy(score = 0)
                Triple(
                    player1.copy(score = 0),
                    nextPlayer,
                    challengerQueue.drop(1) + loser
                )
            } else if (lastGameWinnerId == player2.id) {
                // Player 2 won, Player 1 lost.
                // Player 2 stays as Player 2. Next player becomes Player 1.
                // Player 1 goes to the end of the queue.
                val loser = player1.copy(score = 0)
                Triple(
                    nextPlayer,
                    player2.copy(score = 0),
                    challengerQueue.drop(1) + loser
                )
            } else {
                Triple(
                    player1.copy(score = 0),
                    player2.copy(score = 0),
                    challengerQueue
                )
            }
        } else {
            Triple(
                player1.copy(score = 0),
                player2.copy(score = 0),
                challengerQueue
            )
        }
    }

    /**
     * Determine the winner of the match based on sets won.
     *
     * @param gameState The current game state
     * @return The ID of the winning player, or null if there's a tie or no clear winner
     */
    fun determineWinner(gameState: GameState): Int? =
        when {
            gameState.player1SetsWon > gameState.player2SetsWon -> gameState.player1.id
            gameState.player2SetsWon > gameState.player1SetsWon -> gameState.player2.id
            else -> null // Tie or no clear winner
        }

    /**
     * Determine if the current scores represent a deuce situation.
     *
     * @param p1Score Player 1's score
     * @param p2Score Player 2's score
     * @param settings The game settings
     * @return True if the game is in deuce state
     */
    private fun calculateDeuceState(
        p1Score: Int,
        p2Score: Int,
        settings: GameSettings
    ): Boolean {
        if (!settings.winByTwo) return false

        return p1Score >= settings.pointsToWinSet - 1 &&
            p2Score >= settings.pointsToWinSet - 1
    }

    /**
     * Determine who should serve the next point based on game rules.
     *
     * @param params Parameters for server determination
     * @return The ID of the player who should serve next
     */
    private fun determineNextServer(params: ServerDeterminationParams): Int {
        val (p1Id, p2Id) = params.playerIds

        // If a set just ended, determine server for the new set
        if (params.setEnded) {
            return when {
                params.lastSetWinnerId == null -> params.currentServingPlayerId // Shouldn't happen, but safe fallback
                params.settings.servingRule == ServingRule.PLAYER_ONE_SERVES -> p1Id
                params.settings.servingRule == ServingRule.WINNER_SERVES -> params.lastSetWinnerId
                params.settings.servingRule == ServingRule.LOSER_SERVES -> {
                    if (params.lastSetWinnerId == p1Id) p2Id else p1Id
                }
                params.settings.servingRule == ServingRule.ALTERNATE -> {
                    // Alternate between players based on completed games
                    // Game 1 (completedGames=0): p1, Game 2 (completedGames=1): p2, etc.
                    if (params.completedGames % 2 == 0) p1Id else p2Id
                }
                else -> params.currentServingPlayerId // Fallback
            }
        }

        // Determine if we're in deuce (at or above pointsToWinSet - 1)
        val atDeuce =
            params.currentScores.first >= params.settings.pointsToWinSet - 1 &&
                params.currentScores.second >= params.settings.pointsToWinSet - 1

        // Determine serve rotation interval
        val serveInterval =
            if (atDeuce && params.settings.serveChangeAfterDeuce > 0) {
                params.settings.serveChangeAfterDeuce
            } else {
                params.settings.serveRotationAfterPoints
            }

        // Check if it's time to rotate server
        // Note: currentScores represents the state AFTER scoring the point
        // servingPlayerId indicates who will serve the NEXT point
        // Example with interval=2: P1 serves points 1-2, P2 serves points 3-4, P1 serves points 5-6
        // After point 2 is scored (total=2), we rotate so P2 will serve point 3
        val totalPointsInCurrentSet = params.currentScores.first + params.currentScores.second

        if (serveInterval <= 0) {
            return params.currentServingPlayerId
        }

        // Rotate when we've completed a serve interval (total points is a multiple of interval)
        // This ensures the server changes for the next point to be played
        val shouldRotate =
            totalPointsInCurrentSet > 0 &&
                totalPointsInCurrentSet % serveInterval == 0

        return if (shouldRotate) {
            if (params.currentServingPlayerId == p1Id) p2Id else p1Id
        } else {
            params.currentServingPlayerId
        }
    }
}
