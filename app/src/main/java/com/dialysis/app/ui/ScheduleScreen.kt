package com.dialysis.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.data.PrepItem as DataPrepItem
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
    onTabSelected: (TabItem) -> Unit,
    prefs: AppPreferences
) {
    var refreshKey by remember { mutableStateOf(0) }
    val refresh = { refreshKey++ }

    val nextDialysis = remember(refreshKey) { prefs.getNextDialysis() }
    val prepItemsData = remember(refreshKey) { prefs.getPrepItems() }

    val now = LocalDateTime.now()
    val totalHours = ChronoUnit.HOURS.between(now, nextDialysis.date).coerceAtLeast(0)
    val daysLeft = totalHours / 24
    val hoursLeft = totalHours % 24

    val dateFmt = DateTimeFormatter.ofPattern("M月d日 E")
    val chineseWeekday = when (nextDialysis.date.dayOfWeek.value) {
        1 -> "周一"
        2 -> "周二"
        3 -> "周三"
        4 -> "周四"
        5 -> "周五"
        6 -> "周六"
        7 -> "周日"
        else -> ""
    }
    val dateStr = "${nextDialysis.date.monthValue}月${nextDialysis.date.dayOfMonth}日 $chineseWeekday"
    val timeStr = String.format("%02d:%02d", nextDialysis.date.hour, nextDialysis.date.minute)
    val endTime = nextDialysis.date.plusHours(4)
    val endTimeStr = String.format("%02d:%02d", endTime.hour, endTime.minute)

    val upcoming = listOf(
        UpcomingSchedule(
            date = nextDialysis.date.plusDays(2).let { "${it.monthValue}/${it.dayOfMonth}" },
            title = when (nextDialysis.date.plusDays(2).dayOfWeek.value) {
                1,3,5 -> "透析"
                else -> "下次"
            },
            time = "08:00",
            location = "协和医院",
            accentColor = AccentBlueLight,
            accentBg = AccentBlue.copy(alpha = 0.25f)
        ),
        UpcomingSchedule(
            date = nextDialysis.date.plusDays(7).let { "${it.monthValue}/${it.dayOfMonth}" },
            title = "周五 复诊",
            time = "14:00",
            location = "肾内科门诊",
            accentColor = AccentPurple,
            accentBg = Color(0x405856D6)
        )
    )

    val history = remember { prefs.getDialysisHistory() }

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

            Text(
                text = "透析日程",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextNight,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Next dialysis card with countdown
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
                            text = dateStr,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNight
                        )
                        if (daysLeft == 0L && hoursLeft < 24L) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentOrange.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "今天",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentOrange
                                )
                            }
                        } else if (nextDialysis.confirmed) {
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
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Countdown
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${daysLeft}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNight,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "天",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextNightTertiary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${hoursLeft}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNight,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "时",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextNightTertiary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$timeStr - $endTimeStr",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextNightSecondary,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        LocationPinIcon(modifier = Modifier.padding(top = 2.dp))
                        Text(
                            text = "${nextDialysis.location}\n${nextDialysis.bedNumber}",
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "透析前准备",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextNightTertiary,
                            letterSpacing = 0.4.sp
                        )
                        val checkedCount = prepItemsData.count { it.checked }
                        Text(
                            text = "$checkedCount/${prepItemsData.size}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (checkedCount == prepItemsData.size) AccentGreen else TextNightTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    prepItemsData.forEach { item ->
                        PrepCheckItem(
                            text = item.text,
                            checked = item.checked,
                            onToggle = {
                                prefs.togglePrepItem(item.id)
                                refresh()
                            }
                        )
                    }
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
private fun PrepCheckItem(
    text: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .padding(vertical = 4.dp)
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val w = size.width
            val h = size.height
            if (checked) {
                drawCircle(color = AccentGreen, radius = w * 0.45f, center = Offset(w/2, h/2))
                val sw = 2.dp.toPx()
                val path = Path().apply {
                    moveTo(w * 0.28f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.68f)
                    lineTo(w * 0.74f, h * 0.34f)
                }
                drawPath(path, Color.White, style = Stroke(width = sw, cap = StrokeCap.Round))
            } else {
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = w * 0.4f,
                    center = Offset(w/2, h/2),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (checked) TextNightTertiary else TextNightSecondary,
            textDecoration = if (checked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
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
private fun HistoryItem(record: Triple<String, String, String>) {
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
                text = record.first,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextNightSecondary
            )
            Text(
                text = record.second,
                fontSize = 11.sp,
                color = TextNightTertiary
            )
            Text(
                text = record.third,
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
