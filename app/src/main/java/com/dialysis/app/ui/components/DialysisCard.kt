package com.dialysis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class DialysisInfo(
    val nextTime: LocalDateTime,
    val location: String
)

@Composable
fun DialysisCard(dialysis: DialysisInfo) {
    val now = LocalDateTime.now()
    val daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), dialysis.nextTime.toLocalDate())
    val hoursLeft = ChronoUnit.HOURS.between(now, dialysis.nextTime) % 24
    val timeFormatter = DateTimeFormatter.ofPattern("MM月dd日 HH:mm")

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
                        colors = listOf(DialysisCardStart, DialysisCardEnd)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "下次透析",
                    style = MaterialTheme.typography.titleMedium,
                    color = SecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "时间",
                                tint = Secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = dialysis.nextTime.format(timeFormatter),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = OnBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "地点",
                                tint = Secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = dialysis.location,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${daysLeft}天${hoursLeft}小时",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryDark
                        )
                        Text(
                            text = "倒计时",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
