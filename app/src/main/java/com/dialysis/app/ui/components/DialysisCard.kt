package com.dialysis.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class DialysisInfo(
    val nextTime: LocalDateTime,
    val location: String
)

private val weekdayNames = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

@Composable
fun DialysisCard(dialysis: DialysisInfo) {
    val now = LocalDateTime.now()
    val totalHours = ChronoUnit.HOURS.between(now, dialysis.nextTime)
    val daysLeft = totalHours / 24
    val hoursLeft = totalHours % 24

    val month = dialysis.nextTime.monthValue
    val day = dialysis.nextTime.dayOfMonth
    val weekday = weekdayNames[dialysis.nextTime.dayOfWeek.value % 7]
    val hour = dialysis.nextTime.hour
    val minute = dialysis.nextTime.minute

    val timeStr = "${month}月${day}日 ${weekday} ${String.format("%02d:%02d", hour, minute)}"

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "下次透析",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhiteSecondary
            )

            Text(
                text = timeStr,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${daysLeft}",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 44.sp
                )
                Text(
                    text = "天",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${hoursLeft}",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 44.sp
                )
                Text(
                    text = "时",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "地点",
                    tint = TextWhiteSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = dialysis.location,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
            }
        }
    }
}
