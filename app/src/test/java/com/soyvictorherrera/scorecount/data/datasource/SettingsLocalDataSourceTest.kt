package com.soyvictorherrera.scorecount.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@ExperimentalCoroutinesApi
class SettingsLocalDataSourceTest {
    @TempDir
    lateinit var tmpDir: File

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var dataSource: SettingsLocalDataSource

    @BeforeEach
    fun setUp() {
        testDataStore =
            PreferenceDataStoreFactory.create(
                produceFile = { File(tmpDir, "test_settings.preferences_pb") }
            )

        dataSource = SettingsLocalDataSource(testDataStore)
    }

    @Test
    fun `initial challengerMode is false`() =
        runTest {
            // Give a moment for initial load to complete on background thread
            var settings = dataSource.settings.value
            var attempts = 0
            while (attempts < 20) {
                settings = dataSource.settings.value
                if (!settings.challengerMode) break
                attempts++
                kotlinx.coroutines.delay(50)
            }
            assertFalse(settings.challengerMode)
        }

    @Test
    fun `saveSettings persists challengerMode`() =
        runTest {
            val updated = GameSettings(challengerMode = true)
            dataSource.saveSettings(updated)

            // Poll the StateFlow value until the change is reflected
            var persisted: GameSettings? = null
            var attempts = 0
            while (attempts < 50) {
                persisted = dataSource.settings.value
                if (persisted.challengerMode) break
                attempts++
                kotlinx.coroutines.delay(50)
            }

            assertTrue(persisted?.challengerMode == true)
        }
}
