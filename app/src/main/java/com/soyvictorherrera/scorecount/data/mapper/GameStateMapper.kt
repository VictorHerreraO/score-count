package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.PlayerProto
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player

/**
 * Converts domain GameState to proto GameStateProto for persistence.
 */
fun GameState.toProto(): GameStateProto =
    GameStateProto
        .newBuilder()
        .setPlayer1(player1.toProto())
        .setPlayer2(player2.toProto())
        .setServingPlayerId(servingPlayerId ?: 0)
        .setPlayer1SetsWon(player1SetsWon)
        .setPlayer2SetsWon(player2SetsWon)
        .setIsDeuce(isDeuce)
        .setIsFinished(isFinished)
        .addAllChallengerQueue(challengerQueue.map { it.toProto() })
        .build()

/**
 * Converts proto GameStateProto to domain GameState.
 */
fun GameStateProto.toDomain(): GameState =
    GameState(
        player1 = player1.toDomain(),
        player2 = player2.toDomain(),
        servingPlayerId = if (servingPlayerId == 0) null else servingPlayerId,
        player1SetsWon = player1SetsWon,
        player2SetsWon = player2SetsWon,
        isDeuce = isDeuce,
        isFinished = isFinished,
        challengerQueue = challengerQueueList.map { it.toDomain() }
    )

/**
 * Converts domain Player to proto PlayerProto.
 */
private fun Player.toProto(): PlayerProto =
    PlayerProto
        .newBuilder()
        .setId(id)
        .setName(name)
        .setScore(score)
        .build()

/**
 * Converts proto PlayerProto to domain Player.
 */
private fun PlayerProto.toDomain(): Player =
    Player(
        id = id,
        name = name,
        score = score
    )
