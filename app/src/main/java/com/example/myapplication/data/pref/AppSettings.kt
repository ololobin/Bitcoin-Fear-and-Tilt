package com.example.myapplication.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import com.example.myapplication.util.MemeManager

enum class ColorMode {
    SENTIMENT, PRICE_MOVEMENT
}

enum class Timeframe(val value: String) {
    T_30M("30m"),
    T_24H("24h"),
    START_OF_DAY("start_of_day");

    companion object {
        fun fromValue(value: String): Timeframe {
            return values().firstOrNull { it.value == value } ?: T_24H
        }
    }
}

data class AppSettings(
    val colorMode: ColorMode = ColorMode.PRICE_MOVEMENT,
    val timeframe: Timeframe = Timeframe.T_24H,
    val sensitivityK: Float = 14.0f,
    val speedThresholdX: Float = 2.0f,
    
    // Sentiment Thresholds
    val thresholdExtremeFear: Int = 24,
    val thresholdFear: Int = 39,
    val thresholdNeutral: Int = 59,
    val thresholdGreed: Int = 74,
    val thresholdExtremeGreed: Int = 100,

    // Caches
    val currentPrice: Double = 0.0,
    val price30m: Double = 0.0,
    val price24h: Double = 0.0,
    val priceStartOfDay: Double = 0.0,
    
    val fngValue: Int = 50,
    val fngTimestamp: Long = 0L, // Time in milliseconds when F&G index was fetched
    val fngNextUpdateTime: Long = 0L,
    
    val previousPrice: Double = 0.0,
    val coinApiKey: String = "",
    val isColorInverted: Boolean = false,
    val isWidgetBackgroundTransparent: Boolean = false,
    val language: String = "en",
    val memePhrase: String = "Study Bitcoin."
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "btc_fear_and_tilt_settings")

class AppSettingsManager(private val context: Context) {

    companion object {
        private val KEY_COLOR_MODE = stringPreferencesKey("color_mode")
        private val KEY_TIMEFRAME = stringPreferencesKey("timeframe")
        private val KEY_SENSITIVITY_K = floatPreferencesKey("sensitivity_k")
        private val KEY_SPEED_THRESHOLD_X = floatPreferencesKey("speed_threshold_x")
        
        private val KEY_THRESHOLD_EXTREME_FEAR = intPreferencesKey("threshold_extreme_fear")
        private val KEY_THRESHOLD_FEAR = intPreferencesKey("threshold_fear")
        private val KEY_THRESHOLD_NEUTRAL = intPreferencesKey("threshold_neutral")
        private val KEY_THRESHOLD_GREED = intPreferencesKey("threshold_greed")
        private val KEY_THRESHOLD_EXTREME_GREED = intPreferencesKey("threshold_extreme_greed")

        private val KEY_CURRENT_PRICE = doublePreferencesKey("current_price")
        private val KEY_PRICE_30M = doublePreferencesKey("price_30m")
        private val KEY_PRICE_24H = doublePreferencesKey("price_24h")
        private val KEY_PRICE_START_OF_DAY = doublePreferencesKey("price_start_of_day")
        
        private val KEY_FNG_VALUE = intPreferencesKey("fng_value")
        private val KEY_FNG_TIMESTAMP = longPreferencesKey("fng_timestamp")
        private val KEY_FNG_NEXT_UPDATE_TIME = longPreferencesKey("fng_next_update_time")
        
        private val KEY_PREVIOUS_PRICE = doublePreferencesKey("previous_price")
        private val KEY_COIN_API_KEY = stringPreferencesKey("coin_api_key")
        private val KEY_IS_COLOR_INVERTED = booleanPreferencesKey("is_color_inverted")
        private val KEY_IS_WIDGET_BACKGROUND_TRANSPARENT = booleanPreferencesKey("is_widget_background_transparent")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_MEME_PHRASE = stringPreferencesKey("meme_phrase")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .map { preferences ->
            AppSettings(
                colorMode = ColorMode.valueOf(preferences[KEY_COLOR_MODE] ?: ColorMode.PRICE_MOVEMENT.name),
                timeframe = Timeframe.fromValue(preferences[KEY_TIMEFRAME] ?: Timeframe.T_24H.value),
                sensitivityK = preferences[KEY_SENSITIVITY_K] ?: 14.0f,
                speedThresholdX = preferences[KEY_SPEED_THRESHOLD_X] ?: 2.0f,
                
                thresholdExtremeFear = preferences[KEY_THRESHOLD_EXTREME_FEAR] ?: 24,
                thresholdFear = preferences[KEY_THRESHOLD_FEAR] ?: 39,
                thresholdNeutral = preferences[KEY_THRESHOLD_NEUTRAL] ?: 59,
                thresholdGreed = preferences[KEY_THRESHOLD_GREED] ?: 74,
                thresholdExtremeGreed = preferences[KEY_THRESHOLD_EXTREME_GREED] ?: 100,

                currentPrice = preferences[KEY_CURRENT_PRICE] ?: 0.0,
                price30m = preferences[KEY_PRICE_30M] ?: 0.0,
                price24h = preferences[KEY_PRICE_24H] ?: 0.0,
                priceStartOfDay = preferences[KEY_PRICE_START_OF_DAY] ?: 0.0,
                
                fngValue = preferences[KEY_FNG_VALUE] ?: 50,
                fngTimestamp = preferences[KEY_FNG_TIMESTAMP] ?: 0L,
                fngNextUpdateTime = preferences[KEY_FNG_NEXT_UPDATE_TIME] ?: 0L,
                
                previousPrice = preferences[KEY_PREVIOUS_PRICE] ?: 0.0,
                coinApiKey = preferences[KEY_COIN_API_KEY] ?: "",
                isColorInverted = preferences[KEY_IS_COLOR_INVERTED] ?: false,
                isWidgetBackgroundTransparent = preferences[KEY_IS_WIDGET_BACKGROUND_TRANSPARENT] ?: false,
                language = preferences[KEY_LANGUAGE] ?: "en",
                memePhrase = preferences[KEY_MEME_PHRASE] ?: "Study Bitcoin."
            )
        }

    suspend fun updateColorMode(colorMode: ColorMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_COLOR_MODE] = colorMode.name
        }
    }

    suspend fun updateTimeframe(timeframe: Timeframe) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TIMEFRAME] = timeframe.value
        }
    }

    suspend fun updateSensitivityK(sensitivityK: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SENSITIVITY_K] = sensitivityK
        }
    }

    suspend fun updateSpeedThresholdX(speedThresholdX: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SPEED_THRESHOLD_X] = speedThresholdX
        }
    }

    suspend fun updateThresholds(extremeFear: Int, fear: Int, neutral: Int, greed: Int, extremeGreed: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THRESHOLD_EXTREME_FEAR] = extremeFear
            preferences[KEY_THRESHOLD_FEAR] = fear
            preferences[KEY_THRESHOLD_NEUTRAL] = neutral
            preferences[KEY_THRESHOLD_GREED] = greed
            preferences[KEY_THRESHOLD_EXTREME_GREED] = extremeGreed
        }
    }

    suspend fun updateCoinApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_COIN_API_KEY] = key
        }
    }

    suspend fun updateIsColorInverted(isColorInverted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_COLOR_INVERTED] = isColorInverted
        }
    }

    suspend fun updateIsWidgetBackgroundTransparent(isTransparent: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_WIDGET_BACKGROUND_TRANSPARENT] = isTransparent
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
    }

    suspend fun cachePrices(
        currentPrice: Double,
        price30m: Double,
        price24h: Double,
        priceStartOfDay: Double
    ) {
        context.dataStore.edit { preferences ->
            // Shift current price to previous price before caching new current price
            val oldCurrent = preferences[KEY_CURRENT_PRICE] ?: currentPrice
            preferences[KEY_PREVIOUS_PRICE] = oldCurrent
            
            preferences[KEY_CURRENT_PRICE] = currentPrice
            preferences[KEY_PRICE_30M] = price30m
            preferences[KEY_PRICE_24H] = price24h
            preferences[KEY_PRICE_START_OF_DAY] = priceStartOfDay
            
            // Recalculate and cache meme phrase based on the updated prices and current settings
            val timeframeVal = preferences[KEY_TIMEFRAME] ?: Timeframe.T_24H.value
            val timeframe = Timeframe.fromValue(timeframeVal)
            val basePrice = when (timeframe) {
                Timeframe.T_30M -> price30m
                Timeframe.T_24H -> price24h
                Timeframe.START_OF_DAY -> priceStartOfDay
            }
            val percent = if (basePrice > 0.0) {
                ((currentPrice - basePrice) / basePrice) * 100.0
            } else {
                0.0
            }
            val speedThreshold = preferences[KEY_SPEED_THRESHOLD_X] ?: 2.0f
            val language = preferences[KEY_LANGUAGE] ?: "en"
            
            val newMemePhrase = MemeManager.getMemePhrase(
                percent = percent,
                speedThreshold = speedThreshold.toDouble(),
                currentPrice = currentPrice,
                language = language
            )
            preferences[KEY_MEME_PHRASE] = newMemePhrase
        }
    }

    suspend fun updateMemePhrase(memePhrase: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_MEME_PHRASE] = memePhrase
        }
    }

    suspend fun resetMemePhrase() {
        context.dataStore.edit { preferences ->
            preferences[KEY_MEME_PHRASE] = "Study Bitcoin."
        }
    }

    suspend fun cacheFng(fngValue: Int, timestamp: Long, nextUpdateTime: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FNG_VALUE] = fngValue
            preferences[KEY_FNG_TIMESTAMP] = timestamp
            preferences[KEY_FNG_NEXT_UPDATE_TIME] = nextUpdateTime
        }
    }

    suspend fun cacheFngNextUpdateTime(nextUpdateTime: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FNG_NEXT_UPDATE_TIME] = nextUpdateTime
        }
    }
}

fun getLocalizedString(context: android.content.Context, resId: Int, language: String, vararg formatArgs: Any): String {
    val locale = java.util.Locale.forLanguageTag(language)
    val config = android.content.res.Configuration(context.resources.configuration)
    config.setLocale(locale)
    val localizedContext = context.createConfigurationContext(config)
    return if (formatArgs.isEmpty()) {
        localizedContext.resources.getString(resId)
    } else {
        localizedContext.resources.getString(resId, *formatArgs)
    }
}
