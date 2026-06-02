package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.api.NetworkClient
import com.example.myapplication.data.pref.AppSettings
import com.example.myapplication.data.pref.AppSettingsManager
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.first
import java.util.Calendar
import kotlin.math.abs

class CryptoRepository(
    private val context: Context,
    private val appSettingsManager: AppSettingsManager = AppSettingsManager(context)
) {
    private val coinGeckoService = NetworkClient.createCoinGeckoService()
    private val coinApiService = NetworkClient.createCoinApiService()
    private val binanceService = NetworkClient.createBinanceService()
    private val alternativeMeService = NetworkClient.createAlternativeMeService()

    companion object {
        private const val TAG = "CryptoRepository"
        private var lastRefreshTime = 0L
    }

    val settingsFlow = appSettingsManager.settingsFlow

    suspend fun refreshData(): Result<Unit> {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTime < 30_000L) {
            Log.d(TAG, "refreshData rate limited. Skipping network update.")
            return Result.success(Unit)
        }
        lastRefreshTime = now

        return try {
            val settings = settingsFlow.first()
            val priceResult = fetchPriceData(settings)
            
            if (priceResult.isFailure) {
                return Result.failure(priceResult.exceptionOrNull() ?: Exception("Failed to fetch price data"))
            }

            val priceData = priceResult.getOrThrow()
            
            // F&G index update logic
            val currentTimestamp = System.currentTimeMillis()
            val fourHoursMs = 4 * 60 * 60 * 1000L
            val shouldFetchFng = (currentTimestamp - settings.fngTimestamp >= fourHoursMs) || settings.fngValue == 50

            val fngValue = if (shouldFetchFng) {
                fetchFngIndex(settings, priceData.currentPrice, priceData.price24h)
            } else {
                settings.fngValue
            }

            // Save prices and F&G index to local datastore
            appSettingsManager.cachePrices(
                currentPrice = priceData.currentPrice,
                price30m = priceData.price30m,
                price24h = priceData.price24h,
                priceStartOfDay = priceData.priceStartOfDay
            )

            if (shouldFetchFng) {
                appSettingsManager.cacheFng(fngValue, currentTimestamp)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in refreshData: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun fetchPriceData(settings: AppSettings): Result<PriceData> {
        // Source 1: CoinGecko Market Chart
        try {
            Log.d(TAG, "Attempting CoinGecko for price data...")
            val chart = coinGeckoService.getBitcoinMarketChart()
            if (chart.prices.isNotEmpty()) {
                val prices = chart.prices
                val current = prices.last()[1]
                val nowMs = System.currentTimeMillis()
                
                val p30m = findClosestPrice(prices, nowMs - 30 * 60 * 1000L)
                val p24h = findClosestPrice(prices, nowMs - 24 * 60 * 60 * 1000L)
                val pStart = findClosestPrice(prices, getStartOfDayMillis())
                
                Log.d(TAG, "CoinGecko Price Data: current=$current, 30m=$p30m, 24h=$p24h, start=$pStart")
                return Result.success(PriceData(current, p30m, p24h, pStart))
            }
        } catch (e: Exception) {
            Log.w(TAG, "CoinGecko failed: ${e.message}")
        }

        // Source 2: Binance public Klines (5m candles for 24h)
        try {
            Log.d(TAG, "Attempting Binance Klines for price data...")
            val klines = binanceService.getKlines(symbol = "BTCUSDT", interval = "5m", limit = 288)
            if (klines.isNotEmpty()) {
                val current = klines.last()[4].asString.toDouble()
                
                // 30 mins ago is roughly 6 candles back (6 * 5m = 30m)
                val p30mIndex = (klines.size - 6).coerceAtLeast(0)
                val p30m = klines[p30mIndex][4].asString.toDouble()
                
                // 24 hours ago is index 0
                val p24h = klines.first()[1].asString.toDouble() // open price of first candle
                
                // Start of day candle
                val startOfDayMs = getStartOfDayMillis()
                val startCandle = klines.minByOrNull { kline ->
                    val openTime = kline[0].asLong
                    abs(openTime - startOfDayMs)
                }
                val pStart = startCandle?.let { it[1].asString.toDouble() } ?: p24h
                
                Log.d(TAG, "Binance Price Data: current=$current, 30m=$p30m, 24h=$p24h, start=$pStart")
                return Result.success(PriceData(current, p30m, p24h, pStart))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Binance Klines failed: ${e.message}")
        }

        // Source 3: CoinAPI (single rate, fallback history to current or cache)
        if (settings.coinApiKey.isNotBlank()) {
            try {
                Log.d(TAG, "Attempting CoinAPI for current price...")
                val response = coinApiService.getBitcoinRate(settings.coinApiKey)
                val current = response.rate
                // Since CoinAPI doesn't easily provide history in one call,
                // we fall back to existing cache values if they exist, otherwise current price.
                val p30m = if (settings.price30m > 0) settings.price30m else current
                val p24h = if (settings.price24h > 0) settings.price24h else current
                val pStart = if (settings.priceStartOfDay > 0) settings.priceStartOfDay else current
                
                Log.d(TAG, "CoinAPI Price Data: current=$current")
                return Result.success(PriceData(current, p30m, p24h, pStart))
            } catch (e: Exception) {
                Log.w(TAG, "CoinAPI failed: ${e.message}")
            }
        }

        return Result.failure(Exception("All price APIs failed"))
    }

    private suspend fun fetchFngIndex(settings: AppSettings, currentPrice: Double, price24h: Double): Int {
        // Source 1: Alternative.me
        try {
            Log.d(TAG, "Attempting Alternative.me for F&G Index...")
            val response = alternativeMeService.getFearAndGreed()
            if (response.data.isNotEmpty()) {
                val value = response.data.first().value.toIntOrNull()
                if (value != null) {
                    Log.d(TAG, "Alternative.me F&G Index: $value")
                    return value
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Alternative.me F&G failed: ${e.message}")
        }

        // Source 2: Local Price-Momentum Sentiment calculation (fallback)
        Log.d(TAG, "All F&G APIs failed. Performing local price change calculation...")
        val percentChange24h = if (price24h > 0.0) {
            ((currentPrice - price24h) / price24h) * 100.0
        } else {
            0.0
        }
        // Map 24h price change to sentiment index [0 - 100]:
        // 0% change = 50 (Neutral)
        // +10% change or more = 80 (Greed)
        // -10% change or less = 20 (Fear)
        // Linearly interpolate between these values
        val localFng = ((percentChange24h / 10.0) * 30.0 + 50.0).coerceIn(0.0, 100.0).toInt()
        Log.d(TAG, "Local calculated F&G Index: $localFng (24h Price Change: ${String.format("%.2f", percentChange24h)}%)")
        return localFng
    }

    private fun findClosestPrice(prices: List<List<Double>>, targetTimeMs: Long): Double {
        if (prices.isEmpty()) return 0.0
        return prices.minByOrNull { abs(it[0] - targetTimeMs) }?.get(1) ?: prices.last()[1]
    }

    private fun getStartOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        if (time1 == 0L || time2 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun shouldForceBigPriceImage(currentPrice: Double, previousPrice: Double, fngValue: Int): Boolean {
        if (fngValue < 76) return false
        val levels = listOf(100000.0, 150000.0, 200000.0, 250000.0, 300000.0, 350000.0, 400000.0, 450000.0, 500000.0)
        for (level in levels) {
            if (previousPrice < level && currentPrice >= level) {
                return true
            }
        }
        return false
    }

    data class PriceData(
        val currentPrice: Double,
        val price30m: Double,
        val price24h: Double,
        val priceStartOfDay: Double
    )
}
