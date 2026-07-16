package com.soyvictorherrera.scorecount.ui.scorescreen

import com.soyvictorherrera.scorecount.data.database.dao.PlayerProfileDao
import com.soyvictorherrera.scorecount.data.database.entity.PlayerProfileEntity
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.usecase.DecrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.IncrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ManualSwitchServeUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ResetGameUseCase
import com.soyvictorherrera.scorecount.domain.usecase.SaveMatchUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ScoreUseCases
import com.soyvictorherrera.scorecount.domain.usecase.UndoScoreUseCase
import com.soyvictorherrera.scorecount.util.fakes.FakeMatchRepository
import com.soyvictorherrera.scorecount.util.fakes.FakeScoreRepository
import com.soyvictorherrera.scorecount.util.fakes.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class ScoreViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ScoreViewModel
    private lateinit var fakeScoreRepository: FakeScoreRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var fakeMatchRepository: FakeMatchRepository
    private lateinit var fakePlayerProfileDao: FakePlayerProfileDao
    private lateinit var saveMatchUseCase: SaveMatchUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeScoreRepository = FakeScoreRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        fakeMatchRepository = FakeMatchRepository()
        fakePlayerProfileDao = FakePlayerProfileDao()

        // Create use cases
        val incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
        val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
        val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
        val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
        saveMatchUseCase = SaveMatchUseCase(fakeMatchRepository)
        val undoScoreUseCase = UndoScoreUseCase(fakeScoreRepository)

        // Create ScoreUseCases container
        val scoreUseCases =
            ScoreUseCases(
                increment = incrementScoreUseCase,
                decrement = decrementScoreUseCase,
                switchServe = manualSwitchServeUseCase,
                reset = resetGameUseCase,
                saveMatch = saveMatchUseCase,
                undo = undoScoreUseCase
            )

        viewModel =
            ScoreViewModel(
                scoreRepository = fakeScoreRepository,
                scoreUseCases = scoreUseCases,
                settingsRepository = fakeSettingsRepository,
                playerProfileDao = fakePlayerProfileDao,
                dispatcher = testDispatcher
            )
        testDispatcher.scheduler.advanceUntilIdle() // Let init block complete
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel exposes gameState from repository`() =
        runTest {
            // Given
            val expectedState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 5),
                    player2 = Player(id = 2, name = "Bob", score = 3),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(expectedState)

            // When
            val actualState = viewModel.gameState.first()

            // Then
            assertEquals(expectedState, actualState)
        }

    @Test
    fun `viewModel exposes gameSettings from repository`() =
        runTest {
            // Given
            val expectedSettings =
                GameSettings(
                    pointsToWinSet = 21,
                    numberOfSets = 3,
                    showTitle = false
                )
            fakeSettingsRepository.setSettings(expectedSettings)

            // When
            val actualSettings = viewModel.gameSettings.first()

            // Then
            assertEquals(expectedSettings, actualSettings)
        }

    @Test
    fun `incrementScore delegates to IncrementScoreUseCase`() =
        runTest {
            // Given
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            viewModel.incrementScore(playerId = 1)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val newState = viewModel.gameState.first()
            assertEquals(1, newState.player1.score)
            assertEquals(0, newState.player2.score)
        }

    @Test
    fun `decrementScore delegates to DecrementScoreUseCase`() =
        runTest {
            // Given
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 5),
                    player2 = Player(id = 2, name = "Bob", score = 3),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            viewModel.decrementScore(playerId = 1)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val newState = viewModel.gameState.first()
            assertEquals(4, newState.player1.score)
            assertEquals(3, newState.player2.score)
        }

    @Test
    fun `manualSwitchServe delegates to ManualSwitchServeUseCase`() =
        runTest {
            // Given
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            viewModel.manualSwitchServe()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val newState = viewModel.gameState.first()
            assertEquals(2, newState.servingPlayerId)
        }

    @Test
    fun `resetGame delegates to ResetGameUseCase`() =
        runTest {
            // Given - Player 1 has more sets won
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 10),
                    player2 = Player(id = 2, name = "Bob", score = 8),
                    servingPlayerId = 1,
                    player1SetsWon = 3,
                    player2SetsWon = 1
                )
            fakeScoreRepository.setState(initialState)

            // When
            viewModel.resetGame()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Game is reset (UseCase auto-determines winner internally)
            val newState = viewModel.gameState.first()
            assertEquals(0, newState.player1.score)
            assertEquals(0, newState.player2.score)
            assertEquals(0, newState.player1SetsWon)
            assertEquals(0, newState.player2SetsWon)
            // Winner determination happens in UseCase, not ViewModel
        }

    @Test
    fun `auto-saves match when game finishes`() =
        runTest {
            // Given - Set up initial state (not finished)
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 10),
                    player2 = Player(id = 2, name = "Bob", score = 5),
                    servingPlayerId = 1,
                    player1SetsWon = 2,
                    player2SetsWon = 1,
                    isFinished = false
                )
            fakeScoreRepository.setState(initialState)
            testDispatcher.scheduler.advanceUntilIdle()

            // When - Change state to finished
            val finishedState = initialState.copy(isFinished = true)
            fakeScoreRepository.setState(finishedState)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Match should be auto-saved
            val savedMatches = fakeMatchRepository.getMatchList().first()
            assertEquals(1, savedMatches.size)
            val savedMatch = savedMatches.first()
            assertEquals("Alice", savedMatch.playerOneName)
            assertEquals("Bob", savedMatch.playerTwoName)
            assertEquals(2, savedMatch.playerOneScore)
            assertEquals(1, savedMatch.playerTwoScore)
        }

    @Test
    fun `does not auto-save match when game is already finished`() =
        runTest {
            // Given - Create a fresh match repository to avoid interference from setUp() ViewModel
            val isolatedMatchRepository = FakeMatchRepository()

            // Game starts already finished
            val finishedState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1,
                    player1SetsWon = 3,
                    player2SetsWon = 0,
                    isFinished = true
                )
            fakeScoreRepository.setState(finishedState)

            // Create a new ViewModel that will see the finished state as the first emission
            val incrementScoreUseCase = IncrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
            val decrementScoreUseCase = DecrementScoreUseCase(fakeScoreRepository, fakeSettingsRepository)
            val manualSwitchServeUseCase = ManualSwitchServeUseCase(fakeScoreRepository)
            val resetGameUseCase = ResetGameUseCase(fakeScoreRepository, fakeSettingsRepository)
            val isolatedSaveMatchUseCase = SaveMatchUseCase(isolatedMatchRepository)

            val undoScoreUseCase = UndoScoreUseCase(fakeScoreRepository)

            val isolatedScoreUseCases =
                ScoreUseCases(
                    increment = incrementScoreUseCase,
                    decrement = decrementScoreUseCase,
                    switchServe = manualSwitchServeUseCase,
                    reset = resetGameUseCase,
                    saveMatch = isolatedSaveMatchUseCase,
                    undo = undoScoreUseCase
                )

            @Suppress("UNUSED_VARIABLE")
            val isolatedViewModel =
                ScoreViewModel(
                    scoreRepository = fakeScoreRepository,
                    scoreUseCases = isolatedScoreUseCases,
                    settingsRepository = fakeSettingsRepository,
                    playerProfileDao = fakePlayerProfileDao,
                    dispatcher = testDispatcher
                )
            testDispatcher.scheduler.advanceUntilIdle()

            // When - State remains finished (no transition from false to true)
            // Do nothing, just let init block run

            testDispatcher.scheduler.advanceUntilIdle()

            // Then - No match should be saved to the isolated repository (because there was no transition)
            val savedMatches = isolatedMatchRepository.getMatchList().first()
            assertEquals(0, savedMatches.size)
        }

    @Test
    fun `undoLastChange calls undo use case`() =
        runTest {
            // Given
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)
            fakeScoreRepository.clearHistory()

            // Make a change to create history
            fakeScoreRepository.updateGameState(
                initialState.copy(player1 = initialState.player1.copy(score = 1))
            )

            // When
            viewModel.undoLastChange()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertEquals(initialState, fakeScoreRepository.getGameState().value)
        }

    @Test
    fun `hasUndoHistory exposes repository state flow`() =
        runTest {
            // Given
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice", score = 0),
                    player2 = Player(id = 2, name = "Bob", score = 0),
                    servingPlayerId = 1
                )
            fakeScoreRepository.setState(initialState)
            fakeScoreRepository.clearHistory()

            // When - No history initially
            assertFalse(viewModel.hasUndoHistory.value)

            // When - State change creates history
            fakeScoreRepository.updateGameState(
                initialState.copy(player1 = initialState.player1.copy(score = 1))
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Has undo history
            assertTrue(viewModel.hasUndoHistory.value)
        }

    @Test
    fun `toggleChallengerMode saves setting to repository`() =
        runTest {
            fakeSettingsRepository.setSettings(GameSettings(challengerMode = false))
            viewModel.toggleChallengerMode(enabled = true)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.gameSettings.first().challengerMode)
        }

    @Test
    fun `addPlayerToQueue inserts profile to DB and appends to queue with unique ID`() =
        runTest {
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice"),
                    player2 = Player(id = 2, name = "Bob"),
                    servingPlayerId = 1,
                    challengerQueue = listOf(Player(id = 3, name = "Charlie"))
                )
            fakeScoreRepository.setState(initialState)
            viewModel.addPlayerToQueue("  Dave  ")
            testDispatcher.scheduler.advanceUntilIdle()

            val newState = viewModel.gameState.first()
            assertEquals(2, newState.challengerQueue.size)
            assertEquals("Charlie", newState.challengerQueue[0].name)
            assertEquals("Dave", newState.challengerQueue[1].name)
            assertEquals(4, newState.challengerQueue[1].id)

            val savedProfiles = fakePlayerProfileDao.getAllPlayerProfiles().first()
            assertEquals(1, savedProfiles.size)
            assertEquals("Dave", savedProfiles[0].name)
        }

    @Test
    fun `addPlayerToQueue ignores blank names`() =
        runTest {
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice"),
                    player2 = Player(id = 2, name = "Bob"),
                    servingPlayerId = 1,
                    challengerQueue = emptyList()
                )
            fakeScoreRepository.setState(initialState)
            viewModel.addPlayerToQueue("   ")
            testDispatcher.scheduler.advanceUntilIdle()

            val newState = viewModel.gameState.first()
            assertTrue(newState.challengerQueue.isEmpty())
            assertTrue(fakePlayerProfileDao.getAllPlayerProfiles().first().isEmpty())
        }

    @Test
    fun `removePlayerFromQueue removes player and updates game state`() =
        runTest {
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice"),
                    player2 = Player(id = 2, name = "Bob"),
                    servingPlayerId = 1,
                    challengerQueue = listOf(Player(id = 3, name = "Charlie"), Player(id = 4, name = "Dave"))
                )
            fakeScoreRepository.setState(initialState)
            viewModel.removePlayerFromQueue(playerId = 3)
            testDispatcher.scheduler.advanceUntilIdle()

            val newState = viewModel.gameState.first()
            assertEquals(1, newState.challengerQueue.size)
            assertEquals("Dave", newState.challengerQueue[0].name)
        }

    @Test
    fun `skipPlayerInQueue moves player to end of queue`() =
        runTest {
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice"),
                    player2 = Player(id = 2, name = "Bob"),
                    servingPlayerId = 1,
                    challengerQueue =
                        listOf(
                            Player(id = 3, name = "Charlie"),
                            Player(id = 4, name = "Dave"),
                            Player(id = 5, name = "Eve")
                        )
                )
            fakeScoreRepository.setState(initialState)
            viewModel.skipPlayerInQueue(playerId = 3)
            testDispatcher.scheduler.advanceUntilIdle()

            val newState = viewModel.gameState.first()
            assertEquals(3, newState.challengerQueue.size)
            assertEquals("Dave", newState.challengerQueue[0].name)
            assertEquals("Eve", newState.challengerQueue[1].name)
            assertEquals("Charlie", newState.challengerQueue[2].name)
        }

    @Test
    fun `skipPlayerInQueue is no-op if queue size is 1 or fewer`() =
        runTest {
            val initialState =
                GameState(
                    player1 = Player(id = 1, name = "Alice"),
                    player2 = Player(id = 2, name = "Bob"),
                    servingPlayerId = 1,
                    challengerQueue = listOf(Player(id = 3, name = "Charlie"))
                )
            fakeScoreRepository.setState(initialState)
            viewModel.skipPlayerInQueue(playerId = 3)
            testDispatcher.scheduler.advanceUntilIdle()

            val newState = viewModel.gameState.first()
            assertEquals(1, newState.challengerQueue.size)
            assertEquals("Charlie", newState.challengerQueue[0].name)
        }
}

class FakePlayerProfileDao : PlayerProfileDao {
    private val profiles = mutableListOf<PlayerProfileEntity>()

    override fun getAllPlayerProfiles(): Flow<List<PlayerProfileEntity>> =
        flow {
            emit(profiles)
        }

    override suspend fun insert(playerProfile: PlayerProfileEntity): Long {
        profiles.add(playerProfile)
        return profiles.size.toLong()
    }

    override suspend fun delete(playerProfile: PlayerProfileEntity) {
        profiles.remove(playerProfile)
    }
}
