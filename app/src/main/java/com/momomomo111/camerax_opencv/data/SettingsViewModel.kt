package com.momomomo111.camerax_opencv.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _settingsUiState = MutableStateFlow<SettingsUiState>(SettingsUiState.None)
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState

    fun onVibrationChange(value: Boolean) {
        _settingsUiState.value = SettingsUiState.Success(vibration = value)
    }
}

sealed class SettingsUiState {
    data class Success(val vibration: Boolean) : SettingsUiState()
    object None : SettingsUiState()
}
