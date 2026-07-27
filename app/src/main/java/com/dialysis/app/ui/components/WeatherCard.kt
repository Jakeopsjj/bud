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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "☀️",
                fontSize = 56.sp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = weather.description,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = "湿度 ${weather.humidity}% · ${weather.windDirection}风 ${weather.windLevel}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
            }
        }
    }
}
