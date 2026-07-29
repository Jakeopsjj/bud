package com.dialysis.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.*

data class UpcomingSchedule(
    val date: String,
    val title: String,
    val time: String,
    val location: String,
    val accentColor: Color,
    val accentBg: Color
)

data class HistoryRecord(
    val date: String,
    val duration: String,
    val dehydration: String,
    val dryWeight: String
)

@Composable
fun ScheduleScreen(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit
) {
    val upcoming = listOf(
        UpcomingSchedule(
            date = "7/30",
            title = "周三 透析",
            time = "08:00",
            location = "协和医院",
            accentColor = AccentBlueLight,
            accentBg = AccentBlue.copy(alpha = 0.25f)
        ),
        UpcomingSchedule(
            date = "8/1",
            title = "周五 复诊",
            time = "14:00",
            location = "肾内科门诊",
            accentColor = AccentPurple,
            accentBg = Color(0x405856D6)
        )
    )

    val history = listOf(
        HistoryRecord("7月25日", "4h", "脱水2.1kg", "干体重62.5"),
        HistoryRecord("7月23日", "4h", "脱水2.3kg", "干体重62.5"),
        HistoryRecord("7月21日", "4h", "脱水2.0kg", "干体重62.5")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackground(theme = GlassTheme.Night)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "透析日程",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextNight,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Next dialysis card
            LiquidGlassCard(
                theme = GlassTheme.Night,
                hasSparkles = true,
                contentPadding = PaddingValues(16.dp, 16.dp)
            ) {
                Column {
                    Text(
                        text = "下次透析",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextNightTertiary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "7月28日 周一",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNight
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentGreen.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "已确认",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "08:00",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNight,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = " - 12:00",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextNightTertiary,
                            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        LocationPinIcon(modifier = Modifier.padding(top = 2.dp))
                        Text(
                            text = "协和医院血液净化中心\n3层12号机位",
                            fontSize = 12.sp,
                            color = TextNightSecondary,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color.White.copy(alpha = 0.12f))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "透析前准备",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextNightTertiary,
                        letterSpacing = 0.4.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    PrepItem(icon = { IdCardIcon() }, text = "身份证件")
                    PrepItem(icon = { ClockIcon() }, text = "降压药（按医嘱）")
                    PrepItem(icon = { ListIcon() }, text = "止血带")
                }
            }

            // Upcoming section
            Column {
                Text(
                    text = "即将到来",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextNightSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )

                upcoming.forEachIndexed { index, schedule ->
                    UpcomingItem(schedule = schedule)
                    if (index < upcoming.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // History section
            Column {
                Text(
                    text = "历史记录",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextNightSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )

                history.forEachIndexed { index, record ->
                    HistoryItem(record = record)
                    if (index < history.size - 1) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        BottomTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            theme = GlassTheme.Night,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PrepItem(icon: @Composable () -> Unit, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        icon()
        Text(
            text = text,
            fontSize = 11.sp,
            color = TextNightSecondary
        )
    }
}

@Composable
private fun LocationPinIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(12.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            cubicTo(w * 0.2f, 0f, 0f, w * 0.3f, 0f, w * 0.5f)
            cubicTo(0f, w * 0.8f, w * 0.5f, h, w * 0.5f, h)
            cubicTo(w * 0.5f, h, w, w * 0.8f, w, w * 0.5f)
            cubicTo(w, w * 0.3f, w * 0.8f, 0f, w * 0.5f, 0f)
        }
        drawPath(path, Color.White.copy(alpha = 0.7f), style = Stroke(width = 1.8.dp.toPx()))
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = w * 0.15f,
            center = Offset(w * 0.5f, w * 0.45f),
            style = Stroke(width = 1.8.dp.toPx())
        )
    }
}

@Composable
private fun IdCardIcon() {
    Canvas(modifier = Modifier.size(13.dp)) {
        val w = size.width; val h = size.height; val sw = 1.8.dp.toPx()
        drawRoundRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = Offset(w * 0.05f, h * 0.15f),
            size = Size(w * 0.9f, h * 0.7f),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = sw)
        )
        val bagPath = Path().apply {
            moveTo(w * 0.28f, h * 0.15f)
            lineTo(w * 0.28f, h * 0.05f)
            cubicTo(w * 0.28f, 0f, w * 0.72f, 0f, w * 0.72f, h * 0.05f)
            lineTo(w * 0.72f, h * 0.15f)
        }
        drawPath(bagPath, Color.White.copy(alpha = 0.8f), style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}

@Composable
private fun ClockIcon() {
    Canvas(modifier = Modifier.size(13.dp)) {
        val w = size.width; val h = size.height; val sw = 1.8.dp.toPx()
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = sw)
        )
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(w * 0.5f, h * 0.5f),
            end = Offset(w * 0.5f, h * 0.25f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(w * 0.5f, h * 0.5f),
            end = Offset(w * 0.72f, h * 0.62f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ListIcon() {
    Canvas(modifier = Modifier.size(13.dp)) {
        val w = size.width; val h = size.height; val sw = 1.8.dp.toPx()
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(w * 0.08f, h * 0.25f),
            end = Offset(w * 0.92f, h * 0.25f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(w * 0.2f, h * 0.5f),
            end = Offset(w * 0.92f, h * 0.5f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(w * 0.3f, h * 0.75f),
            end = Offset(w * 0.92f, h * 0.75f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun UpcomingItem(schedule: UpcomingSchedule) {
    LiquidGlassCard(
        theme = GlassTheme.Night,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(12.dp, 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(schedule.accentBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = schedule.date,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = schedule.accentColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextNight
                )
                Text(
                    text = "${schedule.time} · ${schedule.location}",
                    fontSize = 11.sp,
                    color = TextNightTertiary,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            ChevronRightIcon()
        }
    }
}

@Composable
private fun HistoryItem(record: HistoryRecord) {
    LiquidGlassCard(
        theme = GlassTheme.Night,
        cornerRadius = 16.dp,
        contentPadding = PaddingValues(10.dp, 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = record.date,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextNightSecondary
            )
            Text(
                text = "${record.duration} · ${record.dehydration}",
                fontSize = 11.sp,
                color = TextNightTertiary
            )
            Text(
                text = record.dryWeight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AccentGreen
            )
        }
    }
}

@Composable
private fun ChevronRightIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width; val h = size.height; val sw = 2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.3f, h * 0.2f)
            lineTo(w * 0.75f, h * 0.5f)
            lineTo(w * 0.3f, h * 0.8f)
        }
        drawPath(path, Color.White.copy(alpha = 0.4f), style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
