package com.soyvictorherrera.scorecount.data.datasource

import com.soyvictorherrera.scorecount.data.database.dao.PlayerProfileDao
import com.soyvictorherrera.scorecount.data.mapper.PlayerProfileMapper
import com.soyvictorherrera.scorecount.domain.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlayerProfileDataSource {
    fun getPlayerProfiles(): Flow<List<Player>>

    suspend fun savePlayerProfile(player: Player): Long
}

class LocalPlayerProfileDataSource
    @Inject
    constructor(
        private val playerProfileDao: PlayerProfileDao,
        private val playerProfileMapper: PlayerProfileMapper
    ) : PlayerProfileDataSource {
        override fun getPlayerProfiles(): Flow<List<Player>> =
            playerProfileDao.getAllPlayerProfiles().map { entities ->
                entities.map(playerProfileMapper::mapFromEntity)
            }

        override suspend fun savePlayerProfile(player: Player): Long =
            playerProfileDao.insert(playerProfileMapper.mapToEntity(player))
    }
