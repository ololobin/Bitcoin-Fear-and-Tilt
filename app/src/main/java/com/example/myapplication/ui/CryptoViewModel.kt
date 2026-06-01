package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.pref.AppSettings
import com.example.myapplication.data.pref.AppSettingsManager
import com.example.myapplication.data.pref.ColorMode
import com.example.myapplication.data.pref.Timeframe
import com.example.myapplication.data.repository.CryptoRepository
import com.example.myapplication.widget.BtcWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.myapplication.util.IconManager

class CryptoViewModel(application: Application) : AndroidViewModel(application) {

    private val appSettingsManager = AppSettingsManager(application)
    private val repository = CryptoRepository(application, appSettingsManager)

    private val _uiState = MutableStateFlow(AppSettings())
    val uiState: StateFlow<AppSettings> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsManager.settingsFlow.collectLatest { settings ->
                _uiState.value = settings
                
                // Synchronize launcher app icon with the Fear & Greed Index stage
                val sentiment = when {
                    settings.fngValue <= settings.thresholdExtremeFear -> "extreme_fear"
                    settings.fngValue <= settings.thresholdFear -> "fear"
                    settings.fngValue <= settings.thresholdNeutral -> "neutral"
                    settings.fngValue <= settings.thresholdGreed -> "greed"
                    else -> "extreme_greed"
                }
                IconManager.changeAppIcon(getApplication(), sentiment)
            }
        }
        // Run initial data refresh on startup
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshData()
            BtcWidget().updateAll(getApplication())
            _isRefreshing.value = false
        }
    }

    fun updateColorMode(colorMode: ColorMode) {
        viewModelScope.launch {
            appSettingsManager.updateColorMode(colorMode)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateTimeframe(timeframe: Timeframe) {
        viewModelScope.launch {
            appSettingsManager.updateTimeframe(timeframe)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateSensitivityK(k: Float) {
        viewModelScope.launch {
            appSettingsManager.updateSensitivityK(k)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateSpeedThresholdX(x: Float) {
        viewModelScope.launch {
            appSettingsManager.updateSpeedThresholdX(x)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateThresholds(extremeFear: Int, fear: Int, neutral: Int, greed: Int, extremeGreed: Int) {
        viewModelScope.launch {
            appSettingsManager.updateThresholds(extremeFear, fear, neutral, greed, extremeGreed)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateCoinApiKey(key: String) {
        viewModelScope.launch {
            appSettingsManager.updateCoinApiKey(key)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateIsColorInverted(isInverted: Boolean) {
        viewModelScope.launch {
            appSettingsManager.updateIsColorInverted(isInverted)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateIsWidgetBackgroundTransparent(isTransparent: Boolean) {
        viewModelScope.launch {
            appSettingsManager.updateIsWidgetBackgroundTransparent(isTransparent)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            appSettingsManager.updateLanguage(language)
            BtcWidget().updateAll(getApplication())
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            appSettingsManager.updateColorMode(ColorMode.PRICE_MOVEMENT)
            appSettingsManager.updateTimeframe(Timeframe.T_24H)
            appSettingsManager.updateSensitivityK(9.0f)
            appSettingsManager.updateSpeedThresholdX(2.0f)
            appSettingsManager.updateIsColorInverted(false)
            appSettingsManager.updateIsWidgetBackgroundTransparent(false)
            appSettingsManager.updateLanguage("en")
            appSettingsManager.updateThresholds(24, 39, 59, 74, 100)
            BtcWidget().updateAll(getApplication())
        }
    }
}
