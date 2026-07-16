package com.soyvictorherrera.scorecount.ui.scorescreen.preview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.soyvictorherrera.scorecount.data.database.dao.PlayerProfileDao
import com.soyvictorherrera.scorecount.data.database.entity.PlayerProfileEntity
import com.soyvictorherrera.scorecount.domain.model.GameState
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.usecase.DecrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.IncrementScoreUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ManualSwitchServeUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ResetGameUseCase
import com.soyvictorherrera.scorecount.domain.usecase.SaveMatchUseCase
import com.soyvictorherrera.scorecount.domain.usecase.ScoreUseCases
import com.soyvictorherrera.scorecount.domain.usecase.UndoScoreUseCase
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreScreen
import com.soyvictorherrera.scorecount.ui.scorescreen.ScoreViewModel
import com.soyvictorherrera.scorecount.ui.theme.ScoreCountTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@PreviewLightDark
@Composable
fun ScoreScreenPreview() {
    val previewViewModel = createPreviewViewModel()

    ScoreCountTheme {
        ScoreScreen(
            viewModel = previewViewModel,
            onNavigateToHistory = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape"
)
@Preview(
    name = "Dark",
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun ScoreScreenLandscapePreview() {
    val previewViewModel = createPreviewViewModel()

    ScoreCountTheme {
        ScoreScreen(
            viewModel = previewViewModel,
            onNavigateToHistory = {},
            onNavigateToSettings = {}
        )
    }
}

@PreviewLightDark
@Composable
fun ScoreScreenFinishedPreview() {
    val previewViewModel = createPreviewViewModel(finished = true)

    ScoreCountTheme {
        ScoreScreen(
            viewModel = previewViewModel,
            onNavigateToHistory = {},
            onNavigateToSettings = {}
        )
    }
}

private fun createPreviewViewModel(finished: Boolean = false): ScoreViewModel {
    val dummyP1 = Player(id = 1, name = "Player 1", score = if (finished) 0 else 9)
    val dummyP2 = Player(id = 2, name = "Player 2", score = if (finished) 0 else 10)
    val previewGameState =
        GameState(
            player1 = dummyP1,
            player2 = dummyP2,
            servingPlayerId = 1,
            player1SetsWon = 3,
            player2SetsWon = 1,
            isDeuce = !finished,
            isFinished = finished
        )
    val fakeScoreRepo = FakeScoreRepository(initialState = previewGameState)
    val fakeSettingsRepo = FakeSettingsRepository()
    val fakeMatchRepo = FakeMatchRepository()

    val scoreUseCases =
        ScoreUseCases(
            increment = IncrementScoreUseCase(fakeScoreRepo, fakeSettingsRepo),
            decrement = DecrementScoreUseCase(fakeScoreRepo, fakeSettingsRepo),
            switchServe = ManualSwitchServeUseCase(fakeScoreRepo),
            reset = ResetGameUseCase(fakeScoreRepo, fakeSettingsRepo),
            saveMatch = SaveMatchUseCase(fakeMatchRepo),
            undo = UndoScoreUseCase(fakeScoreRepo)
        )

    val fakePlayerProfileDao =
        object : PlayerProfileDao {
            override fun getAllPlayerProfiles(): Flow<List<PlayerProfileEntity>> = flowOf(emptyList())

            override suspend fun insert(playerProfile: PlayerProfileEntity): Long = 0L

            override suspend fun delete(playerProfile: PlayerProfileEntity) {}
        }

    return ScoreViewModel(
        scoreRepository = fakeScoreRepo,
        scoreUseCases = scoreUseCases,
        settingsRepository = fakeSettingsRepo,
        playerProfileDao = fakePlayerProfileDao,
        dispatcher = Dispatchers.Unconfined
    )
}
