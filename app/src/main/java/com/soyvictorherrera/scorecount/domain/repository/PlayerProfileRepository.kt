package com.soyvictorherrera.scorecount.domain.repository

import com.soyvictorherrera.scorecount.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerProfileRepository {
    fun getPlayerProfiles(): Flow<List<Player>>

    suspend fun savePlayerProfile(player: Player): Long
}
