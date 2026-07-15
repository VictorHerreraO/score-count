package com.soyvictorherrera.scorecount.domain.model

data class GameSettings(
    val showTitle: Boolean = true,
    val showNames: Boolean = true,
    val showSets: Boolean = false, // Assuming this is not a core feature yet
    val markServe: Boolean = true,
    val markDeuce: Boolean = true,
    val pointsToWinSet: Int = 11,
    val winByTwo: Boolean = true,
    val numberOfSets: Int = 5, // Best of 5 sets
    val serveRotationAfterPoints: Int = 2,
    val serveChangeAfterDeuce: Int = 1,
    val servingRule: ServingRule = ServingRule.DEFAULT,
    val keepScreenOn: Boolean = false,
    val challengerMode: Boolean = false
)
