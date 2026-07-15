package com.soyvictorherrera.scorecount.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soyvictorherrera.scorecount.data.database.entity.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profiles")
    fun getAllPlayerProfiles(): Flow<List<PlayerProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playerProfile: PlayerProfileEntity): Long

    @Delete
    suspend fun delete(playerProfile: PlayerProfileEntity)
}
