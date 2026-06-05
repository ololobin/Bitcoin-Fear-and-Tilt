package com.example.myapplication.ui.screen

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.pref.AppSettings
import com.example.myapplication.data.pref.ColorMode
import com.example.myapplication.data.pref.Timeframe
import com.example.myapplication.data.pref.getLocalizedString
import com.example.myapplication.ui.CryptoViewModel
import com.example.myapplication.ui.component.*
import com.example.myapplication.ui.theme.CartoonBlack
import com.example.myapplication.ui.theme.LcdGlowText
import com.example.myapplication.ui.theme.SentimentFear
import com.example.myapplication.ui.theme.SentimentGreed
import com.example.myapplication.ui.theme.SentimentNeutral
import com.example.myapplication.util.MemeManager
import kotlin.math.abs

@Composable
fun MainScreen(
    viewModel: CryptoViewModel,
    onNavigateToSettings: () -> Unit
) {
    val settings by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Calculate parameters
    val percent = calculatePriceChange(settings)
    val angle = -(percent.toFloat() * settings.sensitivityK).coerceIn(-89f, 89f)
    
    // Smooth physics-based spring rotation animation
    val animatedAngle by animateFloatAsState(
        targetValue = angle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CartTilt"
    )

    val imageRes = getCartImageRes(context, settings, percent)
    val classification = getSentimentClassification(context, settings.fngValue, settings)
    val themeColor = getSentimentColor(settings, percent)
    val memePhrase = MemeManager.getMemePhrase(percent, settings.currentPrice, settings.language)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E1B0F), Color(0xFF190F09))
                )
            )
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = getLocalizedString(context, R.string.bitcoin_label, settings.language),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFE5A93B)
                    )
                    Text(
                        text = getLocalizedString(context, R.string.fear_tilt_system, settings.language),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.LightGray
                    )
                }

                Row {
                    IconButton(
                        onClick = { viewModel.refresh() },
                        enabled = !isRefreshing,
                        modifier = Modifier
                            .background(Color(0xFF422C1D), RoundedCornerShape(8.dp))
                            .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFFE5A93B),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = getLocalizedString(context, R.string.refresh_data, settings.language),
                                tint = Color(0xFFE5A93B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .background(Color(0xFF422C1D), RoundedCornerShape(8.dp))
                            .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = getLocalizedString(context, R.string.settings_desc, settings.language),
                            tint = Color(0xFFE5A93B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Tilting visualizer screen
            WoodCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(460.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .height(360.dp)
                            .background(Color(0xFF1E130B), RoundedCornerShape(10.dp))
                            .border(3.dp, CartoonBlack, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background slope lines drawn in canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw static slope line
                            drawLine(
                                color = Color(0xFF3A2312),
                                start = Offset(0f, size.height * 0.85f),
                                end = Offset(size.width, size.height * 0.85f),
                                strokeWidth = 4.dp.toPx()
                            )
                            // Warning markers
                            drawLine(
                                color = Color(0xFF552211),
                                start = Offset(size.width * 0.2f, size.height * 0.85f),
                                end = Offset(size.width * 0.2f, size.height * 0.95f),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = Color(0xFF552211),
                                start = Offset(size.width * 0.8f, size.height * 0.85f),
                                end = Offset(size.width * 0.8f, size.height * 0.95f),
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        // Cartoon Cart Image with spring tilt - enlarged to 320.dp
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = getLocalizedString(context, R.string.tilted_cart, settings.language),
                            modifier = Modifier
                                .size(320.dp)
                                .graphicsLayer {
                                    rotationZ = animatedAngle
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LCD Monitor Screens (Double display)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LcdDisplay(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = "",
                    value = String.format("$%,.0f", settings.currentPrice),
                    valueColor = themeColor
                )
                
                LcdDisplay(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = "",
                    value = formatChangePercent(percent),
                    valueColor = themeColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large LCD for Sentiment Details
            LcdDisplay(
                modifier = Modifier.fillMaxWidth(),
                label = "",
                value = "${settings.fngValue} - $classification",
                valueColor = getFngColor(settings)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Large LCD for Meme Phrase
            LcdDisplay(
                modifier = Modifier.fillMaxWidth(),
                label = "",
                value = "\"$memePhrase\"",
                valueColor = themeColor
            )



            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Calculations and mapping
fun calculatePriceChange(settings: AppSettings): Double {
    val basePrice = when (settings.timeframe) {
        Timeframe.T_30M -> settings.price30m
        Timeframe.T_24H -> settings.price24h
        Timeframe.START_OF_DAY -> settings.priceStartOfDay
    }
    if (basePrice <= 0.0) return 0.0
    return ((settings.currentPrice - basePrice) / basePrice) * 100.0
}

fun getSentimentClassification(context: Context, fngValue: Int, settings: AppSettings): String {
    val resId = when {
        fngValue <= settings.thresholdExtremeFear -> R.string.extreme_fear
        fngValue <= settings.thresholdFear -> R.string.fear
        fngValue <= settings.thresholdNeutral -> R.string.neutral
        fngValue <= settings.thresholdGreed -> R.string.greed
        else -> R.string.extreme_greed
    }
    return getLocalizedString(context, resId, settings.language)
}

fun getSentimentColor(settings: AppSettings, percent: Double): Color {
    val isGreen = percent > 0.0
    val isRed = percent < 0.0
    
    val primaryColor = SentimentGreed
    val secondaryColor = SentimentFear
    
    val greenResult = if (settings.isColorInverted) secondaryColor else primaryColor
    val redResult = if (settings.isColorInverted) primaryColor else secondaryColor

    return when {
        isGreen -> greenResult
        isRed -> redResult
        else -> SentimentNeutral
    }
}

fun getCartImageRes(context: Context, settings: AppSettings, percent: Double): Int {
    val repository = com.example.myapplication.data.repository.CryptoRepository(context)
    if (repository.shouldForceBigPriceImage(settings.currentPrice, settings.previousPrice, settings.fngValue)) {
        val resId = context.resources.getIdentifier("fast_extreme_big_price", "drawable", context.packageName)
        if (resId != 0) return resId
    }

    val isFast = abs(percent) >= settings.speedThresholdX
    val prefix = if (isFast) "fast_" else "slow_"
    
    val sentimentStr = when {
        settings.fngValue <= settings.thresholdExtremeFear -> "extreme_fear"
        settings.fngValue <= settings.thresholdFear -> "fear"
        settings.fngValue <= settings.thresholdNeutral -> "neutral"
        settings.fngValue <= settings.thresholdGreed -> "greed"
        else -> "extreme_greed"
    }
    
    val resName = "$prefix$sentimentStr"
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    return if (resId != 0) resId else R.drawable.slow_neutral
}

fun formatChangePercent(percent: Double): String {
    val sign = if (percent > 0.0) "+" else ""
    return String.format("%s%.2f%%", sign, percent)
}

fun getFngColor(settings: AppSettings): Color {
    val isGreen = settings.fngValue >= 55
    val isRed = settings.fngValue <= 45
    val primaryColor = SentimentGreed
    val secondaryColor = SentimentFear
    val greenResult = if (settings.isColorInverted) secondaryColor else primaryColor
    val redResult = if (settings.isColorInverted) primaryColor else secondaryColor
    return when {
        isGreen -> greenResult
        isRed -> redResult
        else -> SentimentNeutral
    }
}

