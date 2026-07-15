package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.PlayerProto
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameStateMapperTest {
    @Test
    fun `toProto converts GameState to GameStateProto correctly`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "Alice", score = 10),
                player2 = Player(id = 2, name = "Bob", score = 8),
                servingPlayerId = 1,
                player1SetsWon = 2,
                player2SetsWon = 1,
                isDeuce = true,
                isFinished = false
            )

        // When
        val result = gameState.toProto()

        // Then
        assertEquals(1, result.player1.id)
        assertEquals("Alice", result.player1.name)
        assertEquals(10, result.player1.score)
        assertEquals(2, result.player2.id)
        assertEquals("Bob", result.player2.name)
        assertEquals(8, result.player2.score)
        assertEquals(1, result.servingPlayerId)
        assertEquals(2, result.player1SetsWon)
        assertEquals(1, result.player2SetsWon)
        assertTrue(result.isDeuce)
        assertFalse(result.isFinished)
    }

    @Test
    fun `toProto handles null servingPlayerId`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "Player 1", score = 0),
                player2 = Player(id = 2, name = "Player 2", score = 0),
                servingPlayerId = null,
                player1SetsWon = 0,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = false
            )

        // When
        val result = gameState.toProto()

        // Then
        assertEquals(0, result.servingPlayerId) // null converts to 0 in proto
    }

    @Test
    fun `toProto handles initial default state`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "Player 1", score = 0),
                player2 = Player(id = 2, name = "Player 2", score = 0),
                servingPlayerId = 1,
                player1SetsWon = 0,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = false
            )

        // When
        val result = gameState.toProto()

        // Then
        assertEquals(0, result.player1.score)
        assertEquals(0, result.player2.score)
        assertEquals(0, result.player1SetsWon)
        assertEquals(0, result.player2SetsWon)
        assertFalse(result.isDeuce)
        assertFalse(result.isFinished)
    }

    @Test
    fun `toProto handles finished game state`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "Winner", score = 11),
                player2 = Player(id = 2, name = "Loser", score = 9),
                servingPlayerId = 1,
                player1SetsWon = 3,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = true
            )

        // When
        val result = gameState.toProto()

        // Then
        assertTrue(result.isFinished)
        assertEquals(3, result.player1SetsWon)
        assertEquals(0, result.player2SetsWon)
    }

    @Test
    fun `toDomain converts GameStateProto to GameState correctly`() {
        // Given
        val proto =
            GameStateProto
                .newBuilder()
                .setPlayer1(
                    PlayerProto
                        .newBuilder()
                        .setId(1)
                        .setName("Charlie")
                        .setScore(7)
                        .build()
                ).setPlayer2(
                    PlayerProto
                        .newBuilder()
                        .setId(2)
                        .setName("Diana")
                        .setScore(5)
                        .build()
                ).setServingPlayerId(2)
                .setPlayer1SetsWon(1)
                .setPlayer2SetsWon(2)
                .setIsDeuce(false)
                .setIsFinished(false)
                .build()

        // When
        val result = proto.toDomain()

        // Then
        assertEquals(1, result.player1.id)
        assertEquals("Charlie", result.player1.name)
        assertEquals(7, result.player1.score)
        assertEquals(2, result.player2.id)
        assertEquals("Diana", result.player2.name)
        assertEquals(5, result.player2.score)
        assertEquals(2, result.servingPlayerId)
        assertEquals(1, result.player1SetsWon)
        assertEquals(2, result.player2SetsWon)
        assertFalse(result.isDeuce)
        assertFalse(result.isFinished)
    }

    @Test
    fun `toDomain handles servingPlayerId zero as null`() {
        // Given
        val proto =
            GameStateProto
                .newBuilder()
                .setPlayer1(
                    PlayerProto
                        .newBuilder()
                        .setId(1)
                        .setName("Eve")
                        .setScore(0)
                        .build()
                ).setPlayer2(
                    PlayerProto
                        .newBuilder()
                        .setId(2)
                        .setName("Frank")
                        .setScore(0)
                        .build()
                ).setServingPlayerId(0) // 0 should convert to null
                .setPlayer1SetsWon(0)
                .setPlayer2SetsWon(0)
                .setIsDeuce(false)
                .setIsFinished(false)
                .build()

        // When
        val result = proto.toDomain()

        // Then
        assertNull(result.servingPlayerId) // 0 converts back to null
    }

    @Test
    fun `toDomain handles deuce state`() {
        // Given
        val proto =
            GameStateProto
                .newBuilder()
                .setPlayer1(
                    PlayerProto
                        .newBuilder()
                        .setId(1)
                        .setName("Grace")
                        .setScore(10)
                        .build()
                ).setPlayer2(
                    PlayerProto
                        .newBuilder()
                        .setId(2)
                        .setName("Henry")
                        .setScore(10)
                        .build()
                ).setServingPlayerId(1)
                .setPlayer1SetsWon(0)
                .setPlayer2SetsWon(0)
                .setIsDeuce(true)
                .setIsFinished(false)
                .build()

        // When
        val result = proto.toDomain()

        // Then
        assertTrue(result.isDeuce)
        assertEquals(10, result.player1.score)
        assertEquals(10, result.player2.score)
    }

    @Test
    fun `bidirectional mapping preserves all GameState data`() {
        // Given
        val originalGameState =
            GameState(
                player1 = Player(id = 1, name = "Ivy", score = 15),
                player2 = Player(id = 2, name = "Jack", score = 13),
                servingPlayerId = 2,
                player1SetsWon = 3,
                player2SetsWon = 2,
                isDeuce = false,
                isFinished = true,
                challengerQueue =
                    listOf(
                        Player(id = 3, name = "Charlie", score = 0),
                        Player(id = 4, name = "David", score = 0)
                    )
            )

        // When - Convert domain → proto → domain
        val proto = originalGameState.toProto()
        val resultGameState = proto.toDomain()

        // Then - All fields should be preserved
        assertEquals(originalGameState.player1.id, resultGameState.player1.id)
        assertEquals(originalGameState.player1.name, resultGameState.player1.name)
        assertEquals(originalGameState.player1.score, resultGameState.player1.score)
        assertEquals(originalGameState.player2.id, resultGameState.player2.id)
        assertEquals(originalGameState.player2.name, resultGameState.player2.name)
        assertEquals(originalGameState.player2.score, resultGameState.player2.score)
        assertEquals(originalGameState.servingPlayerId, resultGameState.servingPlayerId)
        assertEquals(originalGameState.player1SetsWon, resultGameState.player1SetsWon)
        assertEquals(originalGameState.player2SetsWon, resultGameState.player2SetsWon)
        assertEquals(originalGameState.isDeuce, resultGameState.isDeuce)
        assertEquals(originalGameState.isFinished, resultGameState.isFinished)
        assertEquals(originalGameState.challengerQueue.size, resultGameState.challengerQueue.size)
        assertEquals(originalGameState.challengerQueue[0].id, resultGameState.challengerQueue[0].id)
        assertEquals(originalGameState.challengerQueue[0].name, resultGameState.challengerQueue[0].name)
        assertEquals(originalGameState.challengerQueue[0].score, resultGameState.challengerQueue[0].score)
        assertEquals(originalGameState.challengerQueue[1].id, resultGameState.challengerQueue[1].id)
        assertEquals(originalGameState.challengerQueue[1].name, resultGameState.challengerQueue[1].name)
        assertEquals(originalGameState.challengerQueue[1].score, resultGameState.challengerQueue[1].score)
    }

    @Test
    fun `bidirectional mapping with null servingPlayerId preserves null`() {
        // Given
        val originalGameState =
            GameState(
                player1 = Player(id = 1, name = "Kate", score = 0),
                player2 = Player(id = 2, name = "Leo", score = 0),
                servingPlayerId = null,
                player1SetsWon = 0,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = false
            )

        // When - Convert domain → proto → domain
        val proto = originalGameState.toProto()
        val resultGameState = proto.toDomain()

        // Then - null servingPlayerId should be preserved
        assertNull(resultGameState.servingPlayerId)
    }

    @Test
    fun `bidirectional mapping from proto preserves all data`() {
        // Given
        val originalProto =
            GameStateProto
                .newBuilder()
                .setPlayer1(
                    PlayerProto
                        .newBuilder()
                        .setId(1)
                        .setName("Mia")
                        .setScore(20)
                        .build()
                ).setPlayer2(
                    PlayerProto
                        .newBuilder()
                        .setId(2)
                        .setName("Noah")
                        .setScore(18)
                        .build()
                ).setServingPlayerId(1)
                .setPlayer1SetsWon(4)
                .setPlayer2SetsWon(3)
                .setIsDeuce(true)
                .setIsFinished(false)
                .build()

        // When - Convert proto → domain → proto
        val domain = originalProto.toDomain()
        val resultProto = domain.toProto()

        // Then - All fields should be preserved
        assertEquals(originalProto.player1.id, resultProto.player1.id)
        assertEquals(originalProto.player1.name, resultProto.player1.name)
        assertEquals(originalProto.player1.score, resultProto.player1.score)
        assertEquals(originalProto.player2.id, resultProto.player2.id)
        assertEquals(originalProto.player2.name, resultProto.player2.name)
        assertEquals(originalProto.player2.score, resultProto.player2.score)
        assertEquals(originalProto.servingPlayerId, resultProto.servingPlayerId)
        assertEquals(originalProto.player1SetsWon, resultProto.player1SetsWon)
        assertEquals(originalProto.player2SetsWon, resultProto.player2SetsWon)
        assertEquals(originalProto.isDeuce, resultProto.isDeuce)
        assertEquals(originalProto.isFinished, resultProto.isFinished)
    }

    @Test
    fun `handles empty player names`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "", score = 0),
                player2 = Player(id = 2, name = "", score = 0),
                servingPlayerId = 1,
                player1SetsWon = 0,
                player2SetsWon = 0,
                isDeuce = false,
                isFinished = false
            )

        // When
        val proto = gameState.toProto()
        val result = proto.toDomain()

        // Then
        assertEquals("", result.player1.name)
        assertEquals("", result.player2.name)
    }

    @Test
    fun `handles high score values`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "Player 1", score = 999),
                player2 = Player(id = 2, name = "Player 2", score = 888),
                servingPlayerId = 1,
                player1SetsWon = 50,
                player2SetsWon = 49,
                isDeuce = false,
                isFinished = false
            )

        // When
        val proto = gameState.toProto()
        val result = proto.toDomain()

        // Then
        assertEquals(999, result.player1.score)
        assertEquals(888, result.player2.score)
        assertEquals(50, result.player1SetsWon)
        assertEquals(49, result.player2SetsWon)
    }

    @Test
    fun `toProto maps challengerQueue correctly`() {
        // Given
        val gameState =
            GameState(
                player1 = Player(id = 1, name = "Player 1"),
                player2 = Player(id = 2, name = "Player 2"),
                servingPlayerId = 1,
                challengerQueue =
                    listOf(
                        Player(id = 3, name = "Challenger A"),
                        Player(id = 4, name = "Challenger B")
                    )
            )

        // When
        val proto = gameState.toProto()

        // Then
        assertEquals(2, proto.challengerQueueCount)
        assertEquals(3, proto.getChallengerQueue(0).id)
        assertEquals("Challenger A", proto.getChallengerQueue(0).name)
        assertEquals(4, proto.getChallengerQueue(1).id)
        assertEquals("Challenger B", proto.getChallengerQueue(1).name)
    }

    @Test
    fun `toDomain maps challengerQueue correctly`() {
        // Given
        val proto =
            GameStateProto
                .newBuilder()
                .setPlayer1(
                    PlayerProto
                        .newBuilder()
                        .setId(1)
                        .setName("Player 1")
                        .setScore(0)
                        .build()
                ).setPlayer2(
                    PlayerProto
                        .newBuilder()
                        .setId(2)
                        .setName("Player 2")
                        .setScore(0)
                        .build()
                ).setServingPlayerId(1)
                .addAllChallengerQueue(
                    listOf(
                        PlayerProto
                            .newBuilder()
                            .setId(3)
                            .setName("Challenger A")
                            .setScore(0)
                            .build(),
                        PlayerProto
                            .newBuilder()
                            .setId(4)
                            .setName("Challenger B")
                            .setScore(0)
                            .build()
                    )
                ).build()

        // When
        val domain = proto.toDomain()

        // Then
        assertEquals(2, domain.challengerQueue.size)
        assertEquals(3, domain.challengerQueue[0].id)
        assertEquals("Challenger A", domain.challengerQueue[0].name)
        assertEquals(4, domain.challengerQueue[1].id)
        assertEquals("Challenger B", domain.challengerQueue[1].name)
    }
}
