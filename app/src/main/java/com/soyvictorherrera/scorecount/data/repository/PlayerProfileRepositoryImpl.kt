package com.soyvictorherrera.scorecount.data.repository

import com.soyvictorherrera.scorecount.data.datasource.PlayerProfileDataSource
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayerProfileRepositoryImpl
    @Inject
    constructor(
        private val dataSource: PlayerProfileDataSource
    ) : PlayerProfileRepository {
        override fun getPlayerProfiles(): Flow<List<Player>> = dataSource.getPlayerProfiles()

        override suspend fun savePlayerProfile(player: Player): Long = dataSource.savePlayerProfile(player)
    }
