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
    val totalHours = ChronoUnit.HOURS.between(now, dialysis.nextTime).coerceAtLeast(0)
    val daysLeft = totalHours / 24
    val hoursLeft = totalHours % 24

    val month = dialysis.nextTime.monthValue
    val day = dialysis.nextTime.dayOfMonth
    val weekday = weekdayNames[(dialysis.nextTime.dayOfWeek.value + 6) % 7] // Mon=0
    val hour = dialysis.nextTime.hour
    val minute = dialysis.nextTime.minute
    val timeStr = "${month}月${day}日 ${weekday} ${String.format("%02d:%02d", hour, minute)}"

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        theme = GlassTheme.Sunny,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        hasSparkles = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "下次透析",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhiteSecondary,
                letterSpacing = 0.6.sp
            )

            Text(
                text = timeStr,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite,
                lineHeight = 18.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = "${daysLeft}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 36.sp
                )
                Text(
                    text = "天",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary.copy(alpha = 0.82f),
                    modifier = Modifier.padding(start = 1.dp, bottom = 5.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${hoursLeft}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 36.sp
                )
                Text(
                    text = "时",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary.copy(alpha = 0.82f),
                    modifier = Modifier.padding(start = 1.dp, bottom = 5.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                LocationPinIcon(modifier = Modifier.size(12.dp))
                Text(
                    text = dialysis.location,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextWhiteSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LocationPinIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()
        val pinColor = Color.White.copy(alpha = 0.75f)
        // Pin body: teardrop shape using path
        val cx = w * 0.5f
        val topY = h * 0.1f
        val bottomY = h * 0.95f
        val r = w * 0.35f
        val path = Path().apply {
            // Outer pin shape approximation using Bezier curves
            moveTo(cx, topY)
            quadraticBezierTo(cx + r * 1.3f, topY + h * 0.35f, cx, bottomY)
            quadraticBezierTo(cx - r * 1.3f, topY + h * 0.35f, cx, topY)
            close()
        }
        drawPath(
            path = path,
            color = pinColor,
            style = Stroke(width = strokeW)
        )
        // Inner circle
        drawCircle(
            color = pinColor,
            radius = r * 0.45f,
            center = Offset(cx, topY + h * 0.32f),
            style = Stroke(width = strokeW * 0.9f)
        )
    }
}
