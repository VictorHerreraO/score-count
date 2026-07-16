package com.soyvictorherrera.scorecount.domain.model

data class GameState(
    val player1: Player,
    val player2: Player,
    val servingPlayerId: Int?,
    val player1SetsWon: Int = 0,
    val player2SetsWon: Int = 0,
    val isDeuce: Boolean = false,
    val isFinished: Boolean = false,
    val challengerQueue: List<Player> = emptyList()
) {
    val currentSet =
        if (isFinished) {
            player1SetsWon + player2SetsWon
        } else {
            1 + player1SetsWon + player2SetsWon
        }
}
