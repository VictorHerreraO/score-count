package com.soyvictorherrera.scorecount.di

import com.soyvictorherrera.scorecount.data.repository.MatchRepositoryImpl
import com.soyvictorherrera.scorecount.data.repository.PlayerProfileRepositoryImpl
import com.soyvictorherrera.scorecount.data.repository.ScoreRepositoryImpl
import com.soyvictorherrera.scorecount.data.repository.SettingsRepositoryImpl
import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import com.soyvictorherrera.scorecount.domain.repository.PlayerProfileRepository
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindScoreRepository(impl: ScoreRepositoryImpl): ScoreRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository

    @Binds
    abstract fun bindPlayerProfileRepository(impl: PlayerProfileRepositoryImpl): PlayerProfileRepository
}
