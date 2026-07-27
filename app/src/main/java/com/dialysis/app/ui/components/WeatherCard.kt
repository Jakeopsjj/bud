package com.dialysis.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
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
        isWeather = true,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "☀️",
                fontSize = 28.sp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = weather.description,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
                Text(
                    text = "湿度 ${weather.humidity}% · ${weather.windDirection}风 ${weather.windLevel}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
            }
        }
    }
}
