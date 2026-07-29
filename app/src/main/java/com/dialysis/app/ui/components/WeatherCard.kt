package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

data class WeatherInfo(
    val temperature: Int,
    val humidity: Int,
    val windLevel: String,
    val windDirection: String,
    val description: String = "晴"
)

@Composable
fun WeatherCard(weather: WeatherInfo) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        isSunny = true,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SunIcon(modifier = Modifier.size(34.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextWhite
                    )
                    Text(
                        text = weather.description,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextWhite
                    )
                }
                Text(
                    text = "湿度 ${weather.humidity}% · ${weather.windDirection}风 ${weather.windLevel}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextWhiteSecondary
                )
            }
        }
    }
}

@Composable
private fun SunIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val coreR = w * 0.19f
        // Core circle
        drawCircle(
            color = Color(0xFFFFD60A),
            radius = coreR,
            center = Offset(cx, cy)
        )
        // Rays
        val rayStroke = 2.dp.toPx()
        val innerRay = coreR + w * 0.08f
        val outerRay = coreR + w * 0.25f
        for (i in 0 until 8) {
            val angle = i * (Math.PI / 4.0).toFloat()
            val x1 = cx + innerRay * kotlin.math.cos(angle)
            val y1 = cy + innerRay * kotlin.math.sin(angle)
            val x2 = cx + outerRay * kotlin.math.cos(angle)
            val y2 = cy + outerRay * kotlin.math.sin(angle)
            drawLine(
                color = Color(0xFFFFD60A),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = rayStroke,
                cap = StrokeCap.Round
            )
        }
    }
}
