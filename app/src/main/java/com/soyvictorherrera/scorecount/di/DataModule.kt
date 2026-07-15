package com.soyvictorherrera.scorecount.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.soyvictorherrera.scorecount.GameStateProto
import com.soyvictorherrera.scorecount.data.database.AppDatabase
import com.soyvictorherrera.scorecount.data.database.dao.MatchDao
import com.soyvictorherrera.scorecount.data.database.dao.PlayerProfileDao
import com.soyvictorherrera.scorecount.data.datasource.GameStateSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStore instance for settings (created via delegate)
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val Context.gameStateDataStore: DataStore<GameStateProto> by dataStore(
    fileName = "game_state.pb",
    serializer = GameStateSerializer
)

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "score-count-database"
            ).addMigrations(AppDatabase.MIGRATION_1_2)
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT OR IGNORE INTO player_profiles (name) VALUES ('Zack')")
                        db.execSQL("INSERT OR IGNORE INTO player_profiles (name) VALUES ('Zoe')")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL("INSERT OR IGNORE INTO player_profiles (name) VALUES ('Zack')")
                        db.execSQL("INSERT OR IGNORE INTO player_profiles (name) VALUES ('Zoe')")
                    }
                }
            ).build()

    @Provides
    @Singleton
    fun provideMatchDao(database: AppDatabase): MatchDao = database.matchDao()

    @Provides
    @Singleton
    fun providePlayerProfileDao(database: AppDatabase): PlayerProfileDao = database.playerProfileDao()

    @Provides
    @Singleton
    fun provideGameStateDataStore(
        @ApplicationContext context: Context
    ): DataStore<GameStateProto> = context.gameStateDataStore

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.settingsDataStore
}
