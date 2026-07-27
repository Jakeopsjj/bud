package com.dialysis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(CardStart, CardEnd)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "天气",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryDark,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${weather.temperature}°C",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground
                        )
                        Text(
                            text = weather.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        WeatherDetailItem(
                            icon = Icons.Default.WaterDrop,
                            label = "湿度",
                            value = "${weather.humidity}%"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        WeatherDetailItem(
                            icon = Icons.Default.Air,
                            label = "${weather.windDirection}风",
                            value = weather.windLevel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.bodyMedium,
            color = OnBackground
        )
    }
}
