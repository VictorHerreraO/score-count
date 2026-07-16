package com.soyvictorherrera.scorecount.ui.settings

import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.ServingRule
import com.soyvictorherrera.scorecount.util.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = SettingsViewModel(fakeSettingsRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle() // Let init block complete
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial settings are loaded from repository`() =
        runTest {
            val initialRepoSettings = GameSettings(pointsToWinSet = 21, showTitle = false)
            fakeSettingsRepository.emitSettings(initialRepoSettings)

            // Re-initialize ViewModel to trigger load with new emitted settings
            viewModel = SettingsViewModel(fakeSettingsRepository, testDispatcher)
            testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

            assertEquals(initialRepoSettings, viewModel.settings.first())
        }

    @Test
    fun `updateShowTitle updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.showTitle

            viewModel.updateShowTitle(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.showTitle)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateShowNames updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.showNames

            viewModel.updateShowNames(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.showNames)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateShowSets updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.showSets

            viewModel.updateShowSets(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.showSets)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateMarkServe updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.markServe

            viewModel.updateMarkServe(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.markServe)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateMarkDeuce updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.markDeuce

            viewModel.updateMarkDeuce(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.markDeuce)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updatePointsToWinSet updates settings, coerces value, and saves`() =
        runTest {
            viewModel.updatePointsToWinSet(50)
            testDispatcher.scheduler.advanceUntilIdle()
            var updatedSettings = viewModel.settings.first()
            assertEquals(50, updatedSettings.pointsToWinSet)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (min)
            viewModel.updatePointsToWinSet(0)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(1, updatedSettings.pointsToWinSet)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (max - 99 as per ViewModel logic)
            viewModel.updatePointsToWinSet(150)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(99, updatedSettings.pointsToWinSet)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateWinByTwo updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.winByTwo

            viewModel.updateWinByTwo(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.winByTwo)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateNumberOfSets updates settings, coerces value, and saves`() =
        runTest {
            viewModel.updateNumberOfSets(7)
            testDispatcher.scheduler.advanceUntilIdle()
            var updatedSettings = viewModel.settings.first()
            assertEquals(7, updatedSettings.numberOfSets)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (min)
            viewModel.updateNumberOfSets(0)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(1, updatedSettings.numberOfSets)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (max - 99 as per ViewModel logic)
            viewModel.updateNumberOfSets(150)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(99, updatedSettings.numberOfSets)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateServeRotationAfterPoints updates settings, coerces value, and saves`() =
        runTest {
            viewModel.updateServeRotationAfterPoints(5)
            testDispatcher.scheduler.advanceUntilIdle()
            var updatedSettings = viewModel.settings.first()
            assertEquals(5, updatedSettings.serveRotationAfterPoints)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (min)
            viewModel.updateServeRotationAfterPoints(0)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(1, updatedSettings.serveRotationAfterPoints)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (max - 99)
            viewModel.updateServeRotationAfterPoints(150)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(99, updatedSettings.serveRotationAfterPoints)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateServeChangeAfterDeuce updates settings, coerces value, and saves`() =
        runTest {
            viewModel.updateServeChangeAfterDeuce(3)
            testDispatcher.scheduler.advanceUntilIdle()
            var updatedSettings = viewModel.settings.first()
            assertEquals(3, updatedSettings.serveChangeAfterDeuce)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (min - 0 is allowed)
            viewModel.updateServeChangeAfterDeuce(-5)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(0, updatedSettings.serveChangeAfterDeuce)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())

            // Test coercion (max - 99)
            viewModel.updateServeChangeAfterDeuce(150)
            testDispatcher.scheduler.advanceUntilIdle()
            updatedSettings = viewModel.settings.first()
            assertEquals(99, updatedSettings.serveChangeAfterDeuce)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateServingRule updates settings and saves`() =
        runTest {
            val newRule = ServingRule.LOSER_SERVES

            viewModel.updateServingRule(newRule)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newRule, updatedSettings.servingRule)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }

    @Test
    fun `updateServingRule cycles through all options correctly`() =
        runTest {
            // Test PLAYER_ONE_SERVES
            viewModel.updateServingRule(ServingRule.PLAYER_ONE_SERVES)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(ServingRule.PLAYER_ONE_SERVES, viewModel.settings.first().servingRule)

            // Test WINNER_SERVES
            viewModel.updateServingRule(ServingRule.WINNER_SERVES)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(ServingRule.WINNER_SERVES, viewModel.settings.first().servingRule)

            // Test LOSER_SERVES
            viewModel.updateServingRule(ServingRule.LOSER_SERVES)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(ServingRule.LOSER_SERVES, viewModel.settings.first().servingRule)
        }

    @Test
    fun `servingRulePicker visibility state management works correctly`() =
        runTest {
            // Initially hidden
            assertEquals(false, viewModel.servingRulePickerVisible.first())

            // Show picker
            viewModel.showServingRulePicker()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(true, viewModel.servingRulePickerVisible.first())

            // Hide picker
            viewModel.hideServingRulePicker()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(false, viewModel.servingRulePickerVisible.first())
        }

    @Test
    fun `updateChallengerMode updates settings and saves`() =
        runTest {
            val initialSettings = viewModel.settings.first()
            val newValue = !initialSettings.challengerMode

            viewModel.updateChallengerMode(newValue)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedSettings = viewModel.settings.first()
            assertEquals(newValue, updatedSettings.challengerMode)
            assertEquals(updatedSettings, fakeSettingsRepository.getSavedSettings())
        }
}
