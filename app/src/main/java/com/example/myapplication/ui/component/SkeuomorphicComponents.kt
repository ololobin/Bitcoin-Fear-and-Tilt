package com.example.myapplication.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.*

@Composable
fun Rivet(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(10.dp)) {
        // Draw screw body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(MetalShine, MetalRivet, Color.DarkGray),
                center = Offset(size.width * 0.3f, size.height * 0.3f)
            )
        )
        // Screw slot
        drawLine(
            color = Color(0xFF222222),
            start = Offset(size.width * 0.25f, size.height * 0.25f),
            end = Offset(size.width * 0.75f, size.height * 0.75f),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

@Composable
fun WoodCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(WoodLight, WoodMedium, WoodDark)
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .border(3.dp, CartoonBlack, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        // Corner Rivets
        Rivet(Modifier.align(Alignment.TopStart).padding(4.dp))
        Rivet(Modifier.align(Alignment.TopEnd).padding(4.dp))
        Rivet(Modifier.align(Alignment.BottomStart).padding(4.dp))
        Rivet(Modifier.align(Alignment.BottomEnd).padding(4.dp))
        
        // Card Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun LcdDisplay(
    modifier: Modifier = Modifier,
    label: String = "",
    value: String,
    valueColor: Color = LcdGlowText
) {
    Column(
        modifier = modifier
            .background(CartoonBlack, RoundedCornerShape(10.dp))
            .border(3.dp, WoodDark, RoundedCornerShape(10.dp))
            .padding(2.dp)
            .background(LcdScreen, RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = value,
            style = MaterialTheme.typography.displayLarge,
            color = valueColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun GlowLamp(
    modifier: Modifier = Modifier,
    isOn: Boolean,
    glowColor: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            // Metal bezel outer border
            drawCircle(Color.Black, radius = size.width / 2)
            drawCircle(MetalRivet, radius = size.width / 2 - 1.5.dp.toPx())
            
            // Glowing lens inside
            val color = if (isOn) glowColor else glowColor.copy(alpha = 0.25f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, color, Color.DarkGray),
                    center = Offset(size.width * 0.35f, size.height * 0.35f),
                    radius = size.width / 2 - 3.dp.toPx()
                ),
                radius = size.width / 2 - 3.dp.toPx()
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isOn) Color.White else Color.Gray
        )
    }
}

@Composable
fun RetroButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE2E4E9), Color(0xFFB0B3B8), Color(0xFF888B90))
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(2.dp, CartoonBlack, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = CartoonBlack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            activeTrackColor = MetalRivet,
            inactiveTrackColor = CartoonBlack,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        ),
        thumb = {
            Canvas(modifier = Modifier.size(24.dp).shadow(2.dp, RoundedCornerShape(12.dp))) {
                // Outer ring
                drawCircle(Color.Black, radius = size.width / 2)
                // Red knob body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF5252), Color(0xFFD32F2F), Color(0xFF7B0000)),
                        center = Offset(size.width * 0.35f, size.height * 0.35f),
                        radius = size.width / 2 - 1.5.dp.toPx()
                    ),
                    radius = size.width / 2 - 1.5.dp.toPx()
                )
                // Center rivet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(MetalShine, MetalRivet, Color.DarkGray),
                        radius = 3.dp.toPx()
                    ),
                    radius = 3.dp.toPx()
                )
            }
        },
        modifier = modifier
    )
}
