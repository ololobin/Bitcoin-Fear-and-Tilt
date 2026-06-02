package com.example.myapplication.data.api

import com.google.gson.JsonElement
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

// --- CoinGecko Models & Service ---
data class CoinGeckoMarketChartResponse(
    val prices: List<List<Double>> // [ [timestamp, price], ... ]
)

interface CoinGeckoService {
    @GET("coins/bitcoin/market_chart")
    suspend fun getBitcoinMarketChart(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: String = "1"
    ): CoinGeckoMarketChartResponse
}

// --- CoinAPI Models & Service ---
data class CoinApiResponse(
    val asset_id_base: String,
    val asset_id_quote: String,
    val rate: Double
)

interface CoinApiService {
    @GET("v1/exchangerate/BTC/USD")
    suspend fun getBitcoinRate(
        @Header("X-CoinAPI-Key") apiKey: String
    ): CoinApiResponse
}

// --- Binance Service ---
interface BinanceService {
    @GET("api/v3/klines")
    suspend fun getKlines(
        @Query("symbol") symbol: String = "BTCUSDT",
        @Query("interval") interval: String = "5m",
        @Query("limit") limit: Int = 288
    ): List<List<JsonElement>>
}

// --- Alternative.me F&G Models & Service ---
data class AltMeFngResponse(
    val data: List<AltMeFngData>
)

data class AltMeFngData(
    val value: String,
    val value_classification: String,
    val timestamp: String
)

interface AlternativeMeService {
    @GET("fng/")
    suspend fun getFearAndGreed(
        @Query("limit") limit: Int = 1
    ): AltMeFngResponse
}

// --- Retrofit Clients Builder ---
object NetworkClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    fun createCoinGeckoService(): CoinGeckoService {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoService::class.java)
    }

    fun createCoinApiService(): CoinApiService {
        return Retrofit.Builder()
            .baseUrl("https://rest.coinapi.io/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinApiService::class.java)
    }

    fun createBinanceService(): BinanceService {
        return Retrofit.Builder()
            .baseUrl("https://api.binance.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BinanceService::class.java)
    }

    fun createAlternativeMeService(): AlternativeMeService {
        return Retrofit.Builder()
            .baseUrl("https://api.alternative.me/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlternativeMeService::class.java)
    }
}
