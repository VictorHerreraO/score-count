package com.soyvictorherrera.scorecount.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Title
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soyvictorherrera.scorecount.R
import com.soyvictorherrera.scorecount.di.DefaultDispatcher
import com.soyvictorherrera.scorecount.domain.model.GameSettings
import com.soyvictorherrera.scorecount.domain.model.ServingRule
import com.soyvictorherrera.scorecount.domain.model.toDisplayStringRes
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingItemData {
    data class ToggleItem(
        val textRes: Int,
        val icon: ImageVector,
        val isChecked: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItemData()

    data class StepperItem(
        val textRes: Int,
        val subtitleRes: Int? = null,
        val icon: ImageVector,
        val value: Int,
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit,
        val valueRange: IntRange
    ) : SettingItemData()

    data class SwitchSetting(
        val textRes: Int,
        val subtitleRes: Int? = null,
        val icon: ImageVector,
        val isChecked: Boolean,
        val onToggle: (Boolean) -> Unit
    ) : SettingItemData()

    data class PickerSetting(
        val textRes: Int,
        val icon: ImageVector,
        val currentValueLabelRes: Int,
        val onClick: () -> Unit
    ) : SettingItemData()
}

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        @DefaultDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {
        private val _settings = MutableStateFlow(GameSettings()) // Initialize with default settings
        val settings: StateFlow<GameSettings> = _settings.asStateFlow()

        private val _servingRulePickerVisible = MutableStateFlow(false)
        val servingRulePickerVisible: StateFlow<Boolean> = _servingRulePickerVisible.asStateFlow()

        init {
            loadSettings()
        }

        fun showServingRulePicker() {
            _servingRulePickerVisible.value = true
        }

        fun hideServingRulePicker() {
            _servingRulePickerVisible.value = false
        }

        private fun loadSettings() {
            viewModelScope.launch(dispatcher) {
                settingsRepository.getSettings().collectLatest { loadedSettings ->
                    _settings.value = loadedSettings
                }
            }
        }

        fun updateShowTitle(show: Boolean) {
            _settings.value = _settings.value.copy(showTitle = show)
            saveSettings()
        }

        fun updateShowNames(show: Boolean) {
            _settings.value = _settings.value.copy(showNames = show)
            saveSettings()
        }

        fun updateShowSets(show: Boolean) {
            _settings.value = _settings.value.copy(showSets = show)
            saveSettings()
        }

        fun updateMarkServe(mark: Boolean) {
            _settings.value = _settings.value.copy(markServe = mark)
            saveSettings()
        }

        fun updateMarkDeuce(mark: Boolean) {
            _settings.value = _settings.value.copy(markDeuce = mark)
            saveSettings()
        }

        fun updatePointsToWinSet(points: Int) {
            val newPoints = points.coerceIn(1, 99)
            if (_settings.value.pointsToWinSet != newPoints) {
                _settings.value = _settings.value.copy(pointsToWinSet = newPoints)
                saveSettings()
            }
        }

        fun updateWinByTwo(winByTwo: Boolean) {
            if (_settings.value.winByTwo != winByTwo) {
                _settings.value = _settings.value.copy(winByTwo = winByTwo)
                saveSettings()
            }
        }

        fun updateNumberOfSets(sets: Int) {
            val newSets = sets.coerceIn(1, 99) // Best of X, so X must be odd, or allow even?
            if (_settings.value.numberOfSets != newSets) {
                _settings.value = _settings.value.copy(numberOfSets = newSets)
                saveSettings()
            }
        }

        fun updateServeRotationAfterPoints(points: Int) {
            val newPoints = points.coerceIn(1, 99)
            if (_settings.value.serveRotationAfterPoints != newPoints) {
                _settings.value = _settings.value.copy(serveRotationAfterPoints = newPoints)
                saveSettings()
            }
        }

        fun updateServeChangeAfterDeuce(points: Int) {
            val newPoints = points.coerceIn(0, 99) // Allow 0 to disable
            if (_settings.value.serveChangeAfterDeuce != newPoints) {
                _settings.value = _settings.value.copy(serveChangeAfterDeuce = newPoints)
                saveSettings()
            }
        }

        fun updateServingRule(rule: ServingRule) {
            _settings.value = _settings.value.copy(servingRule = rule)
            saveSettings()
        }

        fun updateKeepScreenOn(keepOn: Boolean) {
            _settings.value = _settings.value.copy(keepScreenOn = keepOn)
            saveSettings()
        }

        fun updateChallengerMode(enabled: Boolean) {
            _settings.value = _settings.value.copy(challengerMode = enabled)
            saveSettings()
        }

        private fun saveSettings() {
            viewModelScope.launch(dispatcher) {
                settingsRepository.saveSettings(_settings.value)
            }
        }

        fun getGameControls(currentSettings: GameSettings): List<SettingItemData> =
            listOf(
                SettingItemData.ToggleItem(
                    R.string.setting_show_title,
                    Icons.Filled.Title,
                    currentSettings.showTitle
                ) { updateShowTitle(it) },
                SettingItemData.ToggleItem(
                    R.string.setting_show_names,
                    Icons.Filled.Badge,
                    currentSettings.showNames
                ) { updateShowNames(it) },
                SettingItemData.ToggleItem(
                    R.string.setting_show_sets,
                    Icons.Filled.CalendarToday,
                    currentSettings.showSets
                ) { updateShowSets(it) },
                SettingItemData.ToggleItem(
                    R.string.setting_mark_serve,
                    Icons.Filled.PersonSearch,
                    currentSettings.markServe
                ) { updateMarkServe(it) },
                SettingItemData.ToggleItem(
                    R.string.setting_mark_deuce,
                    Icons.Filled.Info,
                    currentSettings.markDeuce
                ) { updateMarkDeuce(it) },
                SettingItemData.ToggleItem(
                    R.string.setting_keep_screen_on,
                    Icons.Filled.ScreenLockPortrait,
                    currentSettings.keepScreenOn
                ) {
                    updateKeepScreenOn(it)
                },
                SettingItemData.ToggleItem(
                    R.string.setting_challenger_mode,
                    Icons.Filled.Group,
                    currentSettings.challengerMode
                ) {
                    updateChallengerMode(it)
                }
            )

        fun getTableTennisRules(currentSettings: GameSettings): List<SettingItemData> =
            listOf(
                SettingItemData.StepperItem(
                    textRes = R.string.setting_set_to,
                    subtitleRes = R.string.setting_win_by_two,
                    icon = Icons.Filled.EmojiEvents,
                    value = currentSettings.pointsToWinSet,
                    onIncrement = { updatePointsToWinSet(currentSettings.pointsToWinSet + 1) },
                    onDecrement = { updatePointsToWinSet(currentSettings.pointsToWinSet - 1) },
                    valueRange = 1..100
                ),
                SettingItemData.StepperItem(
                    textRes = R.string.setting_match,
                    subtitleRes = R.string.setting_best_of_x,
                    icon = Icons.Filled.MilitaryTech,
                    value = currentSettings.numberOfSets,
                    onIncrement = { updateNumberOfSets(currentSettings.numberOfSets + 2) },
                    onDecrement = { updateNumberOfSets(currentSettings.numberOfSets - 2) },
                    valueRange = 1..20
                ),
                SettingItemData.StepperItem(
                    textRes = R.string.setting_serve_rotation_after,
                    subtitleRes = R.string.setting_serve_after_deuce,
                    icon = Icons.AutoMirrored.Filled.RotateRight,
                    value = currentSettings.serveRotationAfterPoints,
                    onIncrement = { updateServeRotationAfterPoints(currentSettings.serveRotationAfterPoints + 1) },
                    onDecrement = { updateServeRotationAfterPoints(currentSettings.serveRotationAfterPoints - 1) },
                    valueRange = 1..10
                ),
                SettingItemData.PickerSetting(
                    textRes = R.string.setting_serving_rule,
                    icon = Icons.Filled.Person,
                    currentValueLabelRes = currentSettings.servingRule.toDisplayStringRes(),
                    onClick = { showServingRulePicker() }
                )
            )
    }
