package com.example.myapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R
import com.example.myapplication.data.pref.AppSettings
import com.example.myapplication.data.pref.Timeframe
import com.example.myapplication.data.pref.getLocalizedString
import com.example.myapplication.ui.CryptoViewModel
import com.example.myapplication.ui.component.*
import androidx.activity.compose.BackHandler
import com.example.myapplication.ui.theme.CartoonBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CryptoViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }
    
    val settings by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Local states
    var extremeFearText by remember(settings.thresholdExtremeFear) { mutableStateOf(settings.thresholdExtremeFear.toString()) }
    var fearText by remember(settings.thresholdFear) { mutableStateOf(settings.thresholdFear.toString()) }
    var neutralText by remember(settings.thresholdNeutral) { mutableStateOf(settings.thresholdNeutral.toString()) }
    var greedText by remember(settings.thresholdGreed) { mutableStateOf(settings.thresholdGreed.toString()) }
    var extremeGreedText by remember(settings.thresholdExtremeGreed) { mutableStateOf(settings.thresholdExtremeGreed.toString()) }
    
    var sensitivityText by remember(settings.sensitivityK) { mutableStateOf(settings.sensitivityK.toString()) }
    var speedThresholdText by remember(settings.speedThresholdX) { mutableStateOf(settings.speedThresholdX.toString()) }

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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar (Pinned)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .background(Color(0xFF422C1D), RoundedCornerShape(8.dp))
                        .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = getLocalizedString(context, R.string.settings_desc, settings.language),
                        tint = Color(0xFFE5A93B)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = getLocalizedString(context, R.string.settings_console, settings.language),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFE5A93B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button at the very top (Pinned)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                RetroButton(
                    text = getLocalizedString(context, R.string.apply_save, settings.language),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val ef = extremeFearText.toIntOrNull() ?: settings.thresholdExtremeFear
                    val f = fearText.toIntOrNull() ?: settings.thresholdFear
                    val n = neutralText.toIntOrNull() ?: settings.thresholdNeutral
                    val g = greedText.toIntOrNull() ?: settings.thresholdGreed
                    val eg = extremeGreedText.toIntOrNull() ?: settings.thresholdExtremeGreed
                    viewModel.updateThresholds(ef, f, n, g, eg)
                    
                    sensitivityText.toFloatOrNull()?.let { viewModel.updateSensitivityK(it) }
                    speedThresholdText.toFloatOrNull()?.let { viewModel.updateSpeedThresholdX(it) }
                    
                    onNavigateBack()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable settings content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 0. Language Selector
                WoodCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = getLocalizedString(context, R.string.language_label, settings.language),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // English
                            val isEnSelected = settings.language == "en"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isEnSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateLanguage("en") }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getLocalizedString(context, R.string.lang_en, settings.language),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isEnSelected) Color.White else Color.Gray
                                )
                            }

                            // Russian
                            val isRuSelected = settings.language == "ru"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isRuSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateLanguage("ru") }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getLocalizedString(context, R.string.lang_ru, settings.language),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isRuSelected) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Color Direction Preference (Inverted vs Normal)
                WoodCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = getLocalizedString(context, R.string.color_mapping_direction, settings.language),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Normal (Up = Green, Down = Red)
                            val isNormalSelected = !settings.isColorInverted
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isNormalSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateIsColorInverted(false) }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getLocalizedString(context, R.string.up_green, settings.language),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isNormalSelected) Color.White else Color.Gray
                                )
                            }

                            // Inverted (Up = Red, Down = Green - Chinese style)
                            val isInvertedSelected = settings.isColorInverted
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isInvertedSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateIsColorInverted(true) }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getLocalizedString(context, R.string.up_red, settings.language),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isInvertedSelected) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }

            Spacer(modifier = Modifier.height(16.dp))

            // 1b. Widget Background Transparency Preference
            WoodCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = getLocalizedString(context, R.string.widget_background, settings.language),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Solid Color (Default)
                        val isSolidSelected = !settings.isWidgetBackgroundTransparent
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSolidSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateIsWidgetBackgroundTransparent(false) }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getLocalizedString(context, R.string.solid_color, settings.language),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSolidSelected) Color.White else Color.Gray
                            )
                        }

                        // Transparent Background
                        val isTransparentSelected = settings.isWidgetBackgroundTransparent
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isTransparentSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateIsWidgetBackgroundTransparent(true) }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getLocalizedString(context, R.string.transparent, settings.language),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isTransparentSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Base Timeframe Configuration
            WoodCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = getLocalizedString(context, R.string.tilt_base_timeframe, settings.language),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Timeframe.values().forEach { tf ->
                            val isSelected = settings.timeframe == tf
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) Color(0xFF6B452B) else Color(0xFF352013),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateTimeframe(tf) }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val label = when (tf) {
                                    Timeframe.T_30M -> getLocalizedString(context, R.string.timeframe_30m, settings.language)
                                    Timeframe.T_24H -> getLocalizedString(context, R.string.timeframe_24h, settings.language)
                                    Timeframe.START_OF_DAY -> getLocalizedString(context, R.string.timeframe_start_of_day, settings.language)
                                }
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Sensitivity Coefficient (K) Input Box
            WoodCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = getLocalizedString(context, R.string.sensitivity_k, settings.language),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        
                        TextField(
                            value = sensitivityText,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() || it == '.' }) {
                                    sensitivityText = newVal
                                    newVal.toFloatOrNull()?.let { viewModel.updateSensitivityK(it) }
                                }
                            },
                            textStyle = TextStyle(fontSize = 14.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .width(100.dp)
                                .height(56.dp) // Fixed vertical cutoff
                                .border(2.dp, CartoonBlack, RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E130B),
                                unfocusedContainerColor = Color(0xFF1E130B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                    Text(
                        text = getLocalizedString(context, R.string.sensitivity_description, settings.language),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Speed Mode Threshold (X%) Input Box
            WoodCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = getLocalizedString(context, R.string.speed_threshold_x, settings.language),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        
                        TextField(
                            value = speedThresholdText,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() || it == '.' }) {
                                    speedThresholdText = newVal
                                    newVal.toFloatOrNull()?.let { viewModel.updateSpeedThresholdX(it) }
                                }
                            },
                            textStyle = TextStyle(fontSize = 14.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .width(100.dp)
                                .height(56.dp) // Fixed vertical cutoff
                                .border(2.dp, CartoonBlack, RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E130B),
                                unfocusedContainerColor = Color(0xFF1E130B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                    Text(
                        text = getLocalizedString(context, R.string.speed_description, settings.language),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Sentiment Thresholds Inputs
            WoodCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = getLocalizedString(context, R.string.fear_greed_thresholds, settings.language),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ThresholdInputField(
                        label = getLocalizedString(context, R.string.extreme_fear_max, settings.language),
                        value = extremeFearText,
                        onValueChange = { extremeFearText = it }
                    )
                    ThresholdInputField(
                        label = getLocalizedString(context, R.string.fear_max, settings.language),
                        value = fearText,
                        onValueChange = { fearText = it }
                    )
                    ThresholdInputField(
                        label = getLocalizedString(context, R.string.neutral_max, settings.language),
                        value = neutralText,
                        onValueChange = { neutralText = it }
                    )
                    ThresholdInputField(
                        label = getLocalizedString(context, R.string.greed_max, settings.language),
                        value = greedText,
                        onValueChange = { greedText = it }
                    )
                    ThresholdInputField(
                        label = getLocalizedString(context, R.string.extreme_greed_max, settings.language),
                        value = extremeGreedText,
                        onValueChange = { extremeGreedText = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Defaults Button at the bottom of settings list
            RetroButton(
                text = getLocalizedString(context, R.string.reset_defaults, settings.language),
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.resetToDefaults()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

@Composable
fun ThresholdInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), // Smaller label text
            color = Color.White
        )
        
        TextField(
            value = value,
            onValueChange = { newVal ->
                if (newVal.all { it.isDigit() } && newVal.length <= 3) {
                    onValueChange(newVal)
                }
            },
            textStyle = TextStyle(fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(80.dp)
                .height(56.dp) // Fixed vertical cutoff
                .border(2.dp, CartoonBlack, RoundedCornerShape(6.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E130B),
                unfocusedContainerColor = Color(0xFF1E130B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}
