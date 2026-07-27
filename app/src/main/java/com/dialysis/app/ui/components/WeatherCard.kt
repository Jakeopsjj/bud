package com.dialysis.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        isWeather = true
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "☀️",
                fontSize = 36.sp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = weather.description,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
                Text(
                    text = "湿度 ${weather.humidity}% · ${weather.windDirection}风 ${weather.windLevel}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
            }
        }
    }
}
