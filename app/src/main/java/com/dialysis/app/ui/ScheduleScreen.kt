package com.dialysis.app.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.data.DialysisCenter
import com.dialysis.app.data.PrepItem as DataPrepItem
import com.dialysis.app.data.ReminderManager
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

@Composable
fun ScheduleScreen(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    prefs: AppPreferences,
    onOpenMap: (String?) -> Unit = {}
) {
    val context = LocalContext.current
    var refreshKey by remember { mutableStateOf(0) }
    val refresh = { refreshKey++ }

    var showCenterPicker by remember { mutableStateOf(false) }

    val nextDialysis = remember(refreshKey) { prefs.getNextDialysis() }
    val prepItemsData = remember(refreshKey) { prefs.getPrepItems() }
    val centers = remember { ReminderManager.getDefaultDialysisCenters() }
    val selectedCenterId = remember(refreshKey) { prefs.getSelectedDialysisCenterId() }

    val now = LocalDateTime.now()
    val totalHours = ChronoUnit.HOURS.between(now, nextDialysis.date).coerceAtLeast(0)
    val daysLeft = totalHours / 24
    val hoursLeft = totalHours % 24

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
            location = prefs.getSelectedCenterName(),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "透析日程",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextNight,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                // Change location button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showCenterPicker = true }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SmallLocationIcon()
                        Text(
                            text = "选择地点",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextNightSecondary
                        )
                    }
                }
            }

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

                    // Navigate button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentBlue.copy(alpha = 0.25f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        val uri = Uri.parse("geo:0,0?q=${Uri.encode(nextDialysis.location)}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                        mapIntent.setPackage("com.autonavi.minimap")
                                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(mapIntent)
                                        } else {
                                            // Fallback to Google Maps or any map app
                                            val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(fallbackIntent)
                                        }
                                    }
                                )
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NavigateIcon()
                                Text(
                                    text = "导航到医院",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AccentBlueLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

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

            // Nearby dialysis centers section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "附近透析中心",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextNightSecondary
                    )
                    // Open map button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4FC3F7).copy(alpha = 0.15f))
                            .clickable { onOpenMap(selectedCenterId ?: centers.first().id) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🗺️ 查看地图",
                            fontSize = 11.sp,
                            color = Color(0xFF4FC3F7),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                centers.take(5).forEachIndexed { index, center ->
                    DialysisCenterItem(
                        center = center,
                        isSelected = center.id == selectedCenterId,
                        onSelect = {
                            prefs.setSelectedDialysisCenter(center.id, center.name, center.address)
                            prefs.updateNextDialysisCenter(center.name, prefs.getSelectedBedNumber())
                            refresh()
                        },
                        onCall = {
                            center.phone?.let { phone ->
                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phone")
                                }
                                context.startActivity(callIntent)
                            }
                        },
                        onNavigate = {
                            onOpenMap(center.id)
                        }
                    )
                    if (index < minOf(centers.size, 5) - 1) {
                        Spacer(modifier = Modifier.height(6.dp))
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

    // Center picker dialog
    if (showCenterPicker) {
        CenterPickerDialog(
            centers = centers,
            selectedId = selectedCenterId,
            onDismiss = { showCenterPicker = false },
            onSelect = { center ->
                prefs.setSelectedDialysisCenter(center.id, center.name, center.address)
                prefs.updateNextDialysisCenter(center.name, "待安排")
                refresh()
                showCenterPicker = false
            }
        )
    }
}

@Composable
private fun DialysisCenterItem(
    center: DialysisCenter,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCall: () -> Unit,
    onNavigate: () -> Unit
) {
    LiquidGlassCard(
        theme = GlassTheme.Night,
        cornerRadius = 16.dp,
        contentPadding = PaddingValues(12.dp, 12.dp),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Selected indicator / hospital icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) AccentGreen.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    SelectedCheckIcon()
                } else {
                    HospitalIcon()
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = center.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextNight
                    )
                    if (center.level != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AccentBlue.copy(alpha = 0.25f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = center.level,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                                color = AccentBlueLight
                            )
                        }
                    }
                }
                Text(
                    text = center.address,
                    fontSize = 10.sp,
                    color = TextNightTertiary,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (center.distance != null) {
                        Text(
                            text = center.distance,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentGreen
                        )
                    }
                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (center.phone != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onCall
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "电话",
                                    fontSize = 9.sp,
                                    color = TextNightSecondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentBlue.copy(alpha = 0.2f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onNavigate
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "导航",
                                fontSize = 9.sp,
                                color = AccentBlueLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterPickerDialog(
    centers: List<DialysisCenter>,
    selectedId: String?,
    onDismiss: () -> Unit,
    onSelect: (DialysisCenter) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            theme = GlassTheme.Night,
            contentPadding = PaddingValues(16.dp, 16.dp),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.heightIn(max = 420.dp)
            ) {
                Text(
                    text = "选择透析中心",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextNight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "共 ${centers.size} 家透析中心",
                    fontSize = 11.sp,
                    color = TextNightTertiary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    centers.forEach { center ->
                        val isSelected = center.id == selectedId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) AccentBlue.copy(alpha = 0.2f)
                                    else Color.White.copy(alpha = 0.06f)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onSelect(center) }
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) AccentGreen.copy(alpha = 0.25f)
                                            else Color.White.copy(alpha = 0.1f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        SelectedCheckIcon()
                                    } else {
                                        HospitalIcon(iconSize = 14f)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Text(
                                            text = center.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextNight
                                        )
                                        if (center.level != null) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(AccentBlue.copy(alpha = 0.25f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = center.level,
                                                    fontSize = 8.sp,
                                                    color = AccentBlueLight
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = center.address,
                                        fontSize = 10.sp,
                                        color = TextNightTertiary,
                                        maxLines = 1
                                    )
                                    if (center.distance != null) {
                                        Text(
                                            text = center.distance,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AccentGreen,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "取消",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextNightSecondary
                    )
                }
            }
        }
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
private fun SmallLocationIcon() {
    Canvas(modifier = Modifier.size(12.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            cubicTo(w * 0.2f, 0f, 0f, w * 0.3f, 0f, w * 0.5f)
            cubicTo(0f, w * 0.8f, w * 0.5f, h, w * 0.5f, h)
            cubicTo(w * 0.5f, h, w, w * 0.8f, w, w * 0.5f)
            cubicTo(w, w * 0.3f, w * 0.8f, 0f, w * 0.5f, 0f)
        }
        drawPath(path, Color.White.copy(alpha = 0.6f), style = Stroke(width = 1.5.dp.toPx()))
    }
}

@Composable
private fun NavigateIcon() {
    Canvas(modifier = Modifier.size(12.dp)) {
        val w = size.width; val h = size.height
        val sw = 1.8.dp.toPx()
        val color = AccentBlueLight
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.5f, h * 0.9f)
            moveTo(w * 0.2f, h * 0.45f)
            lineTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.8f, h * 0.45f)
        }
        drawPath(path, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun HospitalIcon(iconSize: Float = 16f) {
    Canvas(modifier = Modifier.size(iconSize.dp)) {
        val w = this.size.width
        val h = this.size.height
        val sw = (iconSize / 16f * 1.5f).dp.toPx()
        val color = Color.White.copy(alpha = 0.6f)
        val rect = androidx.compose.ui.geometry.Rect(w * 0.15f, h * 0.1f, w * 0.85f, h * 0.9f)
        drawRoundRect(color, rect.topLeft, rect.size, CornerRadius(w*0.08f), style = Stroke(width = sw))
        drawLine(color, Offset(w * 0.5f, h * 0.3f), Offset(w * 0.5f, h * 0.7f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.3f, h * 0.5f), Offset(w * 0.7f, h * 0.5f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
private fun SelectedCheckIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = AccentGreen,
            radius = w * 0.5f,
            center = Offset(w / 2f, h / 2f)
        )
        val sw = 2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.28f, h * 0.52f)
            lineTo(w * 0.44f, h * 0.68f)
            lineTo(w * 0.74f, h * 0.34f)
        }
        drawPath(path, Color.White, style = Stroke(width = sw, cap = StrokeCap.Round))
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
