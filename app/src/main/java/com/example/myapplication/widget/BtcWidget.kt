package com.example.myapplication.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpSize
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.ContentScale
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.R
import com.example.myapplication.MainActivity
import androidx.glance.action.actionStartActivity
import com.example.myapplication.data.pref.AppSettings
import com.example.myapplication.data.pref.AppSettingsManager
import com.example.myapplication.data.pref.ColorMode
import com.example.myapplication.data.pref.Timeframe
import com.example.myapplication.data.pref.getLocalizedString
import com.example.myapplication.data.repository.CryptoRepository
import com.example.myapplication.worker.UpdateWorker
import kotlinx.coroutines.flow.first
import kotlin.math.abs

class BtcWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settingsManager = AppSettingsManager(context)
        
        provideContent {
            val settingsState = settingsManager.settingsFlow.collectAsState(initial = AppSettings())
            val settings = settingsState.value
            
            val percent = calculatePriceChangePercent(settings)
            val angle = -(percent.toFloat() * settings.sensitivityK).coerceIn(-89f, 89f)
            val color = getThemeColor(settings, percent)
            val fngColor = getThemeFngColor(settings)
            
            val imageResName = getCartImageResName(context, settings, percent)
            val imageRes = context.resources.getIdentifier(imageResName, "drawable", context.packageName).let {
                if (it != 0) it else R.drawable.slow_neutral
            }
            val widgetBgColor = getWidgetBackgroundColor(imageResName)
            
            val size = LocalSize.current
            val width = size.width
            val height = size.height
            val density = context.resources.displayMetrics.density
            val targetSizePx = remember(width, height, density) {
                ((minOf(width.value, height.value) * density).toInt()).coerceIn(200, 400)
            }
            
            val transparentColorToStrip = if (settings.isWidgetBackgroundTransparent) widgetBgColor else null
            val rotatedBitmap = remember(imageRes, angle, settings.isWidgetBackgroundTransparent, targetSizePx) {
                rotateBitmap(context, imageRes, angle, targetSizePx, transparentColorToStrip)
            }
            
            // Determine adaptive layout direction based on aspect ratio
            val isHorizontal = width >= height * 1.3f
            
            // Scale text sizes dynamically with the widget height (compact multipliers)
            val dynamicPriceSize = if (isHorizontal) {
                (height.value * 0.14f).coerceIn(11f, 22f).sp
            } else {
                (height.value * 0.10f).coerceIn(10f, 18f).sp
            }
            val dynamicFngSize = if (isHorizontal) {
                (height.value * 0.10f).coerceIn(9f, 16f).sp
            } else {
                (height.value * 0.07f).coerceIn(8f, 13f).sp
            }
            
            val resolvedWidgetBgColor = if (settings.isWidgetBackgroundTransparent) Color.Transparent else widgetBgColor
            
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(resolvedWidgetBgColor))
                    .padding(4.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                if (isHorizontal) {
                    // Horizontal layout (Row) - Fill 100% of widget window
                    Row(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            provider = ImageProvider(rotatedBitmap),
                            contentDescription = getLocalizedString(context, R.string.tilted_cart, settings.language),
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier
                                .defaultWeight()
                                .fillMaxHeight()
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Column(
                            modifier = GlanceModifier
                                .padding(end = 8.dp)
                                .then(
                                    if (settings.isWidgetBackgroundTransparent) {
                                        GlanceModifier.background(ColorProvider(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.6f)))
                                    } else {
                                        GlanceModifier
                                    }
                                )
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = formatPrice(settings.currentPrice),
                                style = TextStyle(
                                    color = ColorProvider(color),
                                    fontSize = dynamicPriceSize,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(1.dp))
                            Text(
                                text = getLocalizedString(context, R.string.widget_fng, settings.language, settings.fngValue),
                                style = TextStyle(
                                    color = ColorProvider(fngColor),
                                    fontSize = dynamicFngSize,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                } else {
                    // Vertical layout (Column) - Stacks image on top, text at bottom to prevent overlap
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            provider = ImageProvider(rotatedBitmap),
                            contentDescription = getLocalizedString(context, R.string.tilted_cart, settings.language),
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier
                                .defaultWeight()
                                .fillMaxWidth()
                        )
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Column(
                            modifier = GlanceModifier
                                .then(
                                    if (settings.isWidgetBackgroundTransparent) {
                                        GlanceModifier.background(ColorProvider(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.6f)))
                                    } else {
                                        GlanceModifier
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatPrice(settings.currentPrice),
                                style = TextStyle(
                                    color = ColorProvider(color),
                                    fontSize = dynamicPriceSize,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(1.dp))
                            Text(
                                text = getLocalizedString(context, R.string.widget_fng, settings.language, settings.fngValue),
                                style = TextStyle(
                                    color = ColorProvider(fngColor),
                                    fontSize = dynamicFngSize,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                
                // Overlay refresh button in the top right corner
                Box(
                    modifier = GlanceModifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        provider = ImageProvider(android.R.drawable.stat_notify_sync),
                        contentDescription = getLocalizedString(context, R.string.refresh_button, settings.language),
                        modifier = GlanceModifier
                            .size(maxOf(18.dp, (height.value * 0.1f).coerceIn(18f, 28f).dp))
                            .clickable(actionRunCallback<RefreshActionCallback>())
                    )
                }
            }
        }
    }
}

// ActionCallback for widget manual refresh
class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("BtcWidget", "Widget refresh clicked, scheduling OneTimeWork...")
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<UpdateWorker>()
            .addTag("FORCED_REFRESH")
            .build()
        workManager.enqueueUniqueWork(
            "WidgetManualUpdate",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}

// Helpers
fun calculatePriceChangePercent(settings: AppSettings): Double {
    val basePrice = when (settings.timeframe) {
        Timeframe.T_30M -> settings.price30m
        Timeframe.T_24H -> settings.price24h
        Timeframe.START_OF_DAY -> settings.priceStartOfDay
    }
    if (basePrice <= 0.0) return 0.0
    return ((settings.currentPrice - basePrice) / basePrice) * 100.0
}

fun getThemeColor(settings: AppSettings, percent: Double): Color {
    val isGreen = percent > 0.0
    val isRed = percent < 0.0
    
    val primaryColor = Color(0xFF2ECC71) // Green
    val secondaryColor = Color(0xFFE74C3C) // Red
    
    val greenResult = if (settings.isColorInverted) secondaryColor else primaryColor
    val redResult = if (settings.isColorInverted) primaryColor else secondaryColor

    return when {
        isGreen -> greenResult
        isRed -> redResult
        else -> Color(0xFF95A5A6)    // Grey
    }
}

fun getThemeFngColor(settings: AppSettings): Color {
    val isGreen = settings.fngValue >= 55
    val isRed = settings.fngValue <= 45
    
    val primaryColor = Color(0xFF2ECC71) // Green
    val secondaryColor = Color(0xFFE74C3C) // Red
    
    val greenResult = if (settings.isColorInverted) secondaryColor else primaryColor
    val redResult = if (settings.isColorInverted) primaryColor else secondaryColor

    return when {
        isGreen -> greenResult
        isRed -> redResult
        else -> Color(0xFF95A5A6) // Grey
    }
}

fun getSentimentClassification(fngValue: Int, settings: AppSettings): String {
    return when {
        fngValue <= settings.thresholdExtremeFear -> "Extreme Fear"
        fngValue <= settings.thresholdFear -> "Fear"
        fngValue <= settings.thresholdNeutral -> "Neutral"
        fngValue <= settings.thresholdGreed -> "Greed"
        else -> "Extreme Greed"
    }
}

fun getCartImageResName(context: Context, settings: AppSettings, percent: Double): String {
    if (shouldForceBigPriceImage(settings.currentPrice, settings.previousPrice, settings.fngValue)) {
        return "fast_extreme_big_price"
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
    
    return "$prefix$sentimentStr"
}

private fun shouldForceBigPriceImage(currentPrice: Double, previousPrice: Double, fngValue: Int): Boolean {
    if (fngValue < 76) return false
    val levels = listOf(100000.0, 150000.0, 200000.0, 250000.0, 300000.0, 350000.0, 400000.0, 450000.0, 500000.0)
    for (level in levels) {
        if (previousPrice < level && currentPrice >= level) {
            return true
        }
    }
    return false
}

fun getWidgetBackgroundColor(imageResName: String): Color {
    return when {
        imageResName == "fast_extreme_big_price" -> Color(0xFF443232)
        imageResName.startsWith("fast_") -> Color(0xFFC2A198)
        imageResName.startsWith("slow_") -> Color(0xFF9CBAC4)
        else -> Color(0xFF9CBAC4)
    }
}

fun rotateBitmap(context: Context, resId: Int, degrees: Float, targetSize: Int, transparentBgColor: Color? = null): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, resId, this)
        var scale = 1
        while (outWidth / scale / 2 >= targetSize && outHeight / scale / 2 >= targetSize) {
            scale *= 2
        }
        inJustDecodeBounds = false
        inSampleSize = scale
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    
    val decoded = BitmapFactory.decodeResource(context.resources, resId, options) ?: 
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        
    // If transparentBgColor is specified, replace pixels matching that color with transparent pixels
    val mutableBitmap = if (transparentBgColor != null) {
        val width = decoded.width
        val height = decoded.height
        val pixels = IntArray(width * height)
        decoded.getPixels(pixels, 0, width, 0, 0, width, height)
        
        val targetR = (transparentBgColor.red * 255f).toInt()
        val targetG = (transparentBgColor.green * 255f).toInt()
        val targetB = (transparentBgColor.blue * 255f).toInt()
        val tolerance = 25 // Tolerating JPEG compression artifacts
        
        for (i in pixels.indices) {
            val colorVal = pixels[i]
            val r = (colorVal shr 16) and 0xFF
            val g = (colorVal shr 8) and 0xFF
            val b = colorVal and 0xFF
            
            if (abs(r - targetR) <= tolerance && abs(g - targetG) <= tolerance && abs(b - targetB) <= tolerance) {
                pixels[i] = android.graphics.Color.TRANSPARENT
            }
        }
        
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        decoded.recycle()
        newBitmap
    } else {
        decoded
    }
    
    // Scale to exact targetSize to prevent huge bitmaps in RemoteViews
    val scaledBitmap = if (mutableBitmap.width > targetSize || mutableBitmap.height > targetSize) {
        val aspect = mutableBitmap.width.toFloat() / mutableBitmap.height.toFloat()
        val (w, h) = if (aspect > 1f) {
            targetSize to (targetSize / aspect).toInt()
        } else {
            (targetSize * aspect).toInt() to targetSize
        }
        val scaled = Bitmap.createScaledBitmap(mutableBitmap, w, h, true)
        if (scaled != mutableBitmap) {
            mutableBitmap.recycle()
        }
        scaled
    } else {
        mutableBitmap
    }
        
    if (degrees == 0f) return scaledBitmap
    
    val matrix = Matrix().apply { postRotate(degrees) }
    val rotated = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
    if (rotated != scaledBitmap) {
        scaledBitmap.recycle()
    }
    return rotated
}

fun formatPrice(price: Double): String {
    return String.format("$%,.0f", price)
}

fun formatPercent(percent: Double): String {
    val sign = if (percent > 0.0) "+" else ""
    return String.format("%s%.2f%%", sign, percent)
}
