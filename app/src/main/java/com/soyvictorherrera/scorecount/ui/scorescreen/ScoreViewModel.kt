package com.soyvictorherrera.scorecount.ui.scorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.data.database.dao.PlayerProfileDao
import com.soyvictorherrera.scorecount.data.database.entity.PlayerProfileEntity
import com.soyvictorherrera.scorecount.di.DefaultDispatcher
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.ScoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel
    @Inject
    constructor(
        private val scoreRepository: com.soyvictorherrera.scorecount.domain.repository.ScoreRepository,
        private val scoreUseCases: ScoreUseCases,
        private val settingsRepository: SettingsRepository,
        private val playerProfileDao: PlayerProfileDao,
        @DefaultDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {
        // Directly expose StateFlows from repositories - no need for intermediate copying
        val gameState: StateFlow<GameState> = scoreRepository.getGameState()
        val gameSettings: StateFlow<GameSettings> = settingsRepository.getSettings()
        val hasUndoHistory: StateFlow<Boolean> = scoreRepository.hasUndoHistory()

        val playerProfiles: Flow<List<PlayerProfileEntity>> = playerProfileDao.getAllPlayerProfiles()

        init {
            // Monitor game state changes to auto-save matches
            viewModelScope.launch(dispatcher) {
                var previousState: GameState? = null
                gameState.collect { currentGameState ->
                    if (currentGameState.isFinished && previousState?.isFinished == false) {
                        saveMatch(currentGameState)
                    }
                    previousState = currentGameState
                }
            }
        }

        fun toggleChallengerMode(enabled: Boolean) {
            viewModelScope.launch {
                val currentSettings = gameSettings.value
                settingsRepository.saveSettings(currentSettings.copy(challengerMode = enabled))
            }
        }

        fun addPlayerToQueue(name: String) {
            val trimmedName = name.trim()
            if (trimmedName.isBlank()) return
            viewModelScope.launch {
                val currentState = gameState.value
                val activeAndQueue = listOf(currentState.player1, currentState.player2) + currentState.challengerQueue
                val nextId = (activeAndQueue.maxOfOrNull { it.id } ?: 0) + 1
                val newPlayer = Player(id = nextId, name = trimmedName)
                val updatedQueue = currentState.challengerQueue + newPlayer
                scoreRepository.updateGameState(currentState.copy(challengerQueue = updatedQueue))
                playerProfileDao.insert(PlayerProfileEntity(name = trimmedName))
            }
        }

        fun removePlayerFromQueue(playerId: Int) {
            viewModelScope.launch {
                val currentState = gameState.value
                val updatedQueue = currentState.challengerQueue.filterNot { it.id == playerId }
                scoreRepository.updateGameState(currentState.copy(challengerQueue = updatedQueue))
            }
        }

        fun skipPlayerInQueue(playerId: Int) {
            viewModelScope.launch {
                val currentState = gameState.value
                val queue = currentState.challengerQueue
                if (queue.size <= 1) return@launch
                val playerToSkip = queue.find { it.id == playerId } ?: return@launch
                val updatedQueue = queue.filterNot { it.id == playerId } + playerToSkip
                scoreRepository.updateGameState(currentState.copy(challengerQueue = updatedQueue))
            }
        }

        fun incrementScore(playerId: Int) {
            viewModelScope.launch {
                scoreUseCases.increment(playerId)
            }
        }

        fun decrementScore(playerId: Int) {
            viewModelScope.launch {
                scoreUseCases.decrement(playerId)
            }
        }

        fun manualSwitchServe() {
            viewModelScope.launch {
                scoreUseCases.switchServe()
            }
        }

        fun resetGame() {
            viewModelScope.launch {
                scoreUseCases.reset()
            }
        }

        fun undoLastChange() {
            viewModelScope.launch {
                scoreUseCases.undo()
            }
        }

        private fun saveMatch(gameState: GameState) {
            viewModelScope.launch {
                val match =
                    Match(
                        id = "",
                        playerOneName = gameState.player1.name,
                        playerTwoName = gameState.player2.name,
                        playerOneScore = gameState.player1SetsWon,
                        playerTwoScore = gameState.player2SetsWon,
                        date = System.currentTimeMillis()
                    )
                scoreUseCases.saveMatch(match)
            }
        }
    }
