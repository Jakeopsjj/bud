package com.dialysis.app.ui

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.data.BpRecord
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class BpDayData(
    val day: String,
    val systolic: Float,
    val diastolic: Float,
    val isWarning: Boolean = false,
    val isToday: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordsScreen(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    prefs: AppPreferences
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var refreshKey by remember { mutableStateOf(0) }

    var selectedSegment by remember { mutableStateOf(0) }
    val segments = listOf("本周", "本月", "全部")

    val bpRecords = remember(refreshKey) {
        prefs.getBpRecords().takeLast(7)
    }
    val weightRecords = remember(refreshKey) {
        prefs.getWeightRecords().takeLast(8)
    }
    val waterIntake = remember(refreshKey) { prefs.getTodayWaterIntake() }
    val waterTarget = remember(refreshKey) { prefs.getWaterTarget() }
    val waterProgress = (waterIntake.toFloat() / waterTarget.toFloat()).coerceIn(0f, 1f)
    val medsData = remember(refreshKey) { prefs.getMedications() }
    val meds = medsData.map { MedItem(it.id, it.name, it.time, it.taken, it.badge) }

    val bpData = bpRecords.mapIndexed { index, r ->
        val dayNames = arrayOf("一", "二", "三", "四", "五", "六", "日")
        val date = LocalDate.parse(r.date, DateTimeFormatter.ISO_LOCAL_DATE)
        val isWarning = r.systolic >= 150 || r.diastolic >= 95
        val isToday = date == LocalDate.now()
        val dayLabel = if (isToday) "今" else dayNames[date.dayOfWeek.value - 1]
        BpDayData(dayLabel, r.systolic.toFloat(), r.diastolic.toFloat(), isWarning, isToday)
    }

    val weightData = weightRecords.map { it.weight }
    val weightDays = weightRecords.map { r ->
        val date = LocalDate.parse(r.date, DateTimeFormatter.ISO_LOCAL_DATE)
        val dayNames = arrayOf("一", "二", "三", "四", "五", "六", "日")
        if (date == LocalDate.now()) "今" else dayNames[date.dayOfWeek.value - 1]
    }

    val refresh: () -> Unit = { refreshKey++ }

    // Dialogs
    var showBpDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showWaterDialog by remember { mutableStateOf(false) }
    var showAddMedDialog by remember { mutableStateOf(false) }
    var deleteMedId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackground(theme = GlassTheme.Cloudy)

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

            // Navigation bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "健康记录",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }

            // Segmented control
            SegmentedControl(
                segments = segments,
                selectedIndex = selectedSegment,
                onSegmentSelected = { selectedSegment = it },
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // Blood pressure trend card
            LiquidGlassCard(
                theme = GlassTheme.Cloudy,
                hasSparkles = true,
                contentPadding = PaddingValues(16.dp, 16.dp),
                onClick = { showBpDialog = true }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BpIcon()
                            Text(
                                text = "血压趋势",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "点击录入",
                                fontSize = 11.sp,
                                color = AccentBlueLight
                            )
                            AddIcon()
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BpBarChart(data = bpData, modifier = Modifier.fillMaxWidth().height(80.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AccentRedSoft, RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = "收缩压",
                                fontSize = 10.sp,
                                color = TextWhiteTertiary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AccentRedSoft.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = "舒张压",
                                fontSize = 10.sp,
                                color = TextWhiteTertiary
                            )
                        }
                    }
                }
            }

            // Weight tracking card
            LiquidGlassCard(
                theme = GlassTheme.Cloudy,
                contentPadding = PaddingValues(16.dp, 16.dp),
                onClick = { showWeightDialog = true }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            WeightIcon()
                            Text(
                                text = "体重追踪",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val weightChange = if (weightRecords.size >= 2) {
                                weightRecords.last().weight - weightRecords[weightRecords.size - 2].weight
                            } else 0f
                            val changeColor = if (weightChange < 0) AccentGreen else if (weightChange > 0) AccentOrange else TextWhiteTertiary
                            val changeText = when {
                                weightChange < 0 -> "↓ %.1fkg 较昨日".format(kotlin.math.abs(weightChange))
                                weightChange > 0 -> "↑ %.1fkg 较昨日".format(weightChange)
                                else -> "-- 较昨日"
                            }
                            Text(
                                text = changeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = changeColor
                            )
                            AddIcon()
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    WeightLineChart(
                        data = weightData,
                        days = weightDays,
                        modifier = Modifier.fillMaxWidth().height(70.dp)
                    )
                }
            }

            // Water intake card
            LiquidGlassCard(
                theme = GlassTheme.Cloudy,
                contentPadding = PaddingValues(16.dp, 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            WaterIcon()
                            Text(
                                text = "今日饮水",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(84.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularWaterProgress(
                                progress = waterProgress,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$waterIntake",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "/ ${waterTarget}ml",
                                    fontSize = 10.sp,
                                    color = TextWhiteTertiary
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                GlassButton(
                                    text = "+100ml 水",
                                    onClick = {
                                        prefs.addWaterIntake(100)
                                        refresh()
                                        Toast.makeText(context, "已记录100ml饮水", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                GlassButton(
                                    text = "+200ml 汤",
                                    onClick = {
                                        prefs.addWaterIntake(200)
                                        refresh()
                                        Toast.makeText(context, "已记录200ml汤/粥", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "还可摄入 ${(waterTarget - waterIntake).coerceAtLeast(0)}ml",
                                fontSize = 10.sp,
                                color = if (waterIntake > waterTarget) AccentRedSoft else TextWhiteTertiary
                            )
                        }
                    }
                }
            }

            // Medication records card
            LiquidGlassCard(
                theme = GlassTheme.Cloudy,
                contentPadding = PaddingValues(16.dp, 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PillIconRecords()
                            Text(
                                text = "今日用药",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            if (meds.isNotEmpty()) {
                                val takenCount = meds.count { it.isTaken }
                                Text(
                                    text = "$takenCount/${meds.size}已服",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AccentGreen
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showAddMedDialog = true }
                            )
                        ) {
                            Text(
                                text = if (meds.isEmpty()) "添加" else "管理",
                                fontSize = 11.sp,
                                color = AccentBlueLight
                            )
                            AddIcon()
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (meds.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无用药记录，点击右上角添加",
                                fontSize = 11.sp,
                                color = TextWhiteTertiary
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            meds.forEachIndexed { index, med ->
                                Column {
                                    if (index > 0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(0.5.dp)
                                                .background(Color.White.copy(alpha = 0.08f))
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(
                                                if (meds.isNotEmpty()) {
                                                    Modifier.combinedClickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null,
                                                        onClick = {
                                                            prefs.toggleMedication(med.id)
                                                            refresh()
                                                        },
                                                        onLongClick = {
                                                            deleteMedId = med.id
                                                        }
                                                    )
                                                } else Modifier
                                            )
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Canvas(modifier = Modifier.size(18.dp)) {
                                                val w = size.width
                                                val h = size.height
                                                if (med.isTaken) {
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
                                            Column {
                                                Text(
                                                    text = med.name,
                                                    fontSize = 12.sp,
                                                    color = if (med.isTaken) TextWhiteTertiary else TextWhite,
                                                    textDecoration = if (med.isTaken) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                                )
                                                Text(
                                                    text = med.time,
                                                    fontSize = 10.sp,
                                                    color = TextWhiteTertiary
                                                )
                                            }
                                        }
                                        if (med.badge != null) {
                                            Box(
                                                modifier = Modifier
                                                    .background(AccentPurpleBg, RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = med.badge,
                                                    fontSize = 9.sp,
                                                    color = AccentPurple
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        BottomTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            theme = GlassTheme.Cloudy,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // BP Input Dialog
    if (showBpDialog) {
        BpInputDialog(
            onDismiss = { showBpDialog = false },
            onConfirm = { sys, dia ->
                prefs.addBpRecord(sys, dia)
                refresh()
                showBpDialog = false
                Toast.makeText(context, "血压已记录：${sys}/${dia}mmHg", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showWeightDialog) {
        WeightInputDialog(
            onDismiss = { showWeightDialog = false },
            onConfirm = { weight ->
                prefs.addWeightRecord(weight)
                refresh()
                showWeightDialog = false
                Toast.makeText(context, "体重已记录：${weight}kg", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Add Medication Dialog
    if (showAddMedDialog) {
        MedInputDialog(
            onDismiss = { showAddMedDialog = false },
            onConfirm = { name, time, badge ->
                prefs.addMedication(name, time, badge)
                refresh()
                showAddMedDialog = false
                Toast.makeText(context, "已添加用药：$name", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete med confirmation
    if (deleteMedId != null) {
        Dialog(onDismissRequest = { deleteMedId = null }) {
            LiquidGlassCard(
                theme = GlassTheme.Cloudy,
                contentPadding = PaddingValues(20.dp, 20.dp),
                cornerRadius = 20.dp
            ) {
                Column {
                    Text(
                        text = "删除用药",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "确定要删除这个用药记录吗？",
                        fontSize = 13.sp,
                        color = TextWhiteSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassButton(
                            text = "取消",
                            onClick = { deleteMedId = null },
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(Color(0xFFE53E3E).copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFE53E3E).copy(alpha = 0.9f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(
                                    onClick = {
                                        prefs.deleteMedication(deleteMedId!!)
                                        deleteMedId = null
                                        refresh()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        "删除",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BpInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var sysText by remember { mutableStateOf("") }
    var diaText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            theme = GlassTheme.Cloudy,
            contentPadding = PaddingValues(20.dp, 20.dp),
            cornerRadius = 24.dp
        ) {
            Column {
                Text(
                    text = "记录血压",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = sysText,
                        onValueChange = { sysText = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("收缩压", color = TextWhiteSecondary, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = AccentBlueLight,
                            focusedIndicatorColor = AccentBlueLight,
                            unfocusedIndicatorColor = TextWhiteTertiary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    OutlinedTextField(
                        value = diaText,
                        onValueChange = { diaText = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("舒张压", color = TextWhiteSecondary, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = AccentBlueLight,
                            focusedIndicatorColor = AccentBlueLight,
                            unfocusedIndicatorColor = TextWhiteTertiary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        text = "取消",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "保存",
                        onClick = {
                            val sys = sysText.toIntOrNull()
                            val dia = diaText.toIntOrNull()
                            if (sys != null && dia != null && sys in 70..220 && dia in 40..140) {
                                onConfirm(sys, dia)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var weightText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            theme = GlassTheme.Cloudy,
            contentPadding = PaddingValues(20.dp, 20.dp),
            cornerRadius = 24.dp
        ) {
            Column {
                Text(
                    text = "记录体重",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "单位：kg（如 62.5）",
                    fontSize = 11.sp,
                    color = TextWhiteTertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
                    label = { Text("体重", color = TextWhiteSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedIndicatorColor = AccentBlueLight,
                        unfocusedIndicatorColor = TextWhiteTertiary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        text = "取消",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "保存",
                        onClick = {
                            val w = weightText.toFloatOrNull()
                            if (w != null && w in 30f..200f) {
                                onConfirm(w)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MedInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, time: String, badge: String?) -> Unit
) {
    var medName by remember { mutableStateOf("") }
    var medTime by remember { mutableStateOf("") }
    var medBadge by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            theme = GlassTheme.Cloudy,
            contentPadding = PaddingValues(20.dp, 20.dp),
            cornerRadius = 24.dp
        ) {
            Column {
                Text(
                    text = "添加用药",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = medName,
                    onValueChange = { medName = it },
                    label = { Text("药品名称", color = TextWhiteSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedIndicatorColor = AccentBlueLight,
                        unfocusedIndicatorColor = TextWhiteTertiary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = medTime,
                    onValueChange = { medTime = it },
                    label = { Text("服用时间（如：08:00/餐前/睡前）", color = TextWhiteSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedIndicatorColor = AccentBlueLight,
                        unfocusedIndicatorColor = TextWhiteTertiary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = medBadge,
                    onValueChange = { medBadge = it },
                    label = { Text("备注（可选，如：注射）", color = TextWhiteSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedIndicatorColor = AccentBlueLight,
                        unfocusedIndicatorColor = TextWhiteTertiary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        text = "取消",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "保存",
                        onClick = {
                            val name = medName.trim()
                            val time = medTime.trim().ifEmpty { "每日一次" }
                            if (name.isNotEmpty()) {
                                onConfirm(name, time, medBadge.trim().ifEmpty { null })
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PillIconRecords() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val sw = 1.8.dp.toPx()
        val color = AccentPurple
        val path = Path().apply {
            val cxl = w * 0.28f
            val cyl = h * 0.65f
            val cxr = w * 0.72f
            val cyr = h * 0.35f
            val r = w * 0.32f
            moveTo(cxl + r * 0.7f, cyl - r * 0.7f)
            lineTo(cxr + r * 0.7f, cyr - r * 0.7f)
            arcTo(
                androidx.compose.ui.geometry.Rect(cxr - r, cyr - r, cxr + r, cyr + r),
                -45f, 180f, false
            )
            lineTo(cxl - r * 0.7f, cyl + r * 0.7f)
            arcTo(
                androidx.compose.ui.geometry.Rect(cxl - r, cyl - r, cxl + r, cyl + r),
                135f, 180f, false
            )
            close()
        }
        drawPath(path, color, style = Stroke(width = sw))
        drawLine(
            Color.White.copy(alpha = 0.6f),
            Offset(w * 0.3f, h * 0.3f),
            Offset(w * 0.7f, h * 0.7f),
            strokeWidth = sw * 0.75f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun SegmentedControl(
    segments: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            segments.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) Brush.verticalGradient(
                                listOf(AccentBlue, AccentBlue.copy(alpha = 0.8f))
                            ) else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSegmentSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) TextWhite else TextWhiteSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun BpIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            cubicTo(w * 0.2f, h * 0.25f, w * 0.05f, h * 0.5f, w * 0.25f, h * 0.75f)
            lineTo(w * 0.5f, h * 0.95f)
            lineTo(w * 0.75f, h * 0.75f)
            cubicTo(w * 0.95f, h * 0.5f, w * 0.8f, h * 0.25f, w * 0.5f, h * 0.05f)
        }
        drawPath(path, AccentRedSoft, style = Stroke(width = 1.8.dp.toPx()))
        val hbPath = Path().apply {
            moveTo(w * 0.25f, h * 0.55f)
            lineTo(w * 0.4f, h * 0.55f)
            lineTo(w * 0.48f, h * 0.38f)
            lineTo(w * 0.58f, h * 0.7f)
            lineTo(w * 0.68f, h * 0.5f)
            lineTo(w * 0.8f, h * 0.5f)
        }
        drawPath(hbPath, AccentRedSoft, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun WeightIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width; val h = size.height; val sw = 1.8.dp.toPx()
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.35f),
            style = Stroke(width = sw)
        )
        val bodyPath = Path().apply {
            moveTo(w * 0.18f, h * 0.9f)
            quadraticBezierTo(w * 0.18f, h * 0.55f, w * 0.5f, h * 0.55f)
            quadraticBezierTo(w * 0.82f, h * 0.55f, w * 0.82f, h * 0.9f)
        }
        drawPath(bodyPath, Color.White.copy(alpha = 0.9f), style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}

@Composable
private fun WaterIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width; val h = size.height; val sw = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            cubicTo(w * 0.5f, h * 0.05f, w * 0.15f, h * 0.5f, w * 0.15f, h * 0.65f)
            cubicTo(w * 0.15f, h * 0.85f, w * 0.3f, h * 0.95f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.7f, h * 0.95f, w * 0.85f, h * 0.85f, w * 0.85f, h * 0.65f)
            cubicTo(w * 0.85f, h * 0.5f, w * 0.5f, h * 0.05f, w * 0.5f, h * 0.05f)
        }
        drawPath(path, AccentBlueLight, style = Stroke(width = sw))
    }
}

@Composable
private fun AddIcon() {
    Canvas(modifier = Modifier.size(14.dp)) {
        val sw = 1.8.dp.toPx()
        val c = AccentBlueLight
        drawLine(c, Offset(size.width * 0.5f, size.height * 0.2f), Offset(size.width * 0.5f, size.height * 0.8f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(c, Offset(size.width * 0.2f, size.height * 0.5f), Offset(size.width * 0.8f, size.height * 0.5f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
private fun BpBarChart(data: List<BpDayData>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barWidth = 8.dp.toPx()
        val gap = 2.dp.toPx()
        val maxBp = 170f
        val minBp = 60f
        val chartH = size.height - 18.dp.toPx()

        data.forEachIndexed { index, day ->
            val x = (size.width / data.size) * index + (size.width / data.size - barWidth * 2 - gap) / 2
            val systolicH = ((day.systolic - minBp) / (maxBp - minBp)) * chartH
            val diastolicH = ((day.diastolic - minBp) / (maxBp - minBp)) * chartH

            val sysColor = when {
                day.isWarning -> AccentOrange
                day.isToday -> AccentBlue
                else -> AccentRedSoft
            }
            val diaColor = sysColor.copy(alpha = 0.5f)

            drawRoundRect(
                color = diaColor,
                topLeft = Offset(x + barWidth + gap, chartH - diastolicH + 18.dp.toPx()),
                size = Size(barWidth, diastolicH),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
            drawRoundRect(
                color = sysColor,
                topLeft = Offset(x, chartH - systolicH + 18.dp.toPx()),
                size = Size(barWidth, systolicH),
                cornerRadius = CornerRadius(3.dp.toPx())
            )

            if (day.isWarning) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentOrange.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    radius = 8.dp.toPx(),
                    center = Offset(x + barWidth / 2, chartH - systolicH + 18.dp.toPx())
                )
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        data.forEach { day ->
            val color = when {
                day.isWarning -> AccentOrangeSoft
                day.isToday -> AccentBlueLight
                else -> TextWhiteTertiary
            }
            val weight = if (day.isWarning || day.isToday) FontWeight.SemiBold else FontWeight.Normal
            Text(
                text = day.day,
                fontSize = 9.sp,
                fontWeight = weight,
                color = color
            )
        }
    }
}

@Composable
private fun WeightLineChart(data: List<Float>, days: List<String>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return
    val minW = data.minOrNull() ?: 62f
    val maxW = data.maxOrNull() ?: 64f
    val range = (maxW - minW).coerceAtLeast(1f)

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val w = size.width
            val h = size.height
            val stepX = if (data.size > 1) w / (data.size - 1) else w

            for (i in 0..2) {
                val y = h * (0.2f + i * 0.3f)
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val linePath = Path()
            val fillPath = Path()
            val points = data.mapIndexed { index, value ->
                val x = if (data.size > 1) stepX * index else w / 2
                val y = h - ((value - minW) / range) * (h * 0.8f) - h * 0.1f
                Offset(x, y)
            }

            points.forEachIndexed { index, point ->
                if (index == 0) {
                    linePath.moveTo(point.x, point.y)
                    fillPath.moveTo(point.x, point.y)
                } else {
                    val prev = points[index - 1]
                    linePath.cubicTo(
                        prev.x + (point.x - prev.x) * 0.5f, prev.y,
                        prev.x + (point.x - prev.x) * 0.5f, point.y,
                        point.x, point.y
                    )
                    fillPath.cubicTo(
                        prev.x + (point.x - prev.x) * 0.5f, prev.y,
                        prev.x + (point.x - prev.x) * 0.5f, point.y,
                        point.x, point.y
                    )
                }
            }
            if (points.isNotEmpty()) {
                fillPath.lineTo(points.last().x, h)
                fillPath.lineTo(points.first().x, h)
                fillPath.close()
            }

            drawPath(
                fillPath,
                Brush.verticalGradient(
                    listOf(AccentBlueLight.copy(alpha = 0.3f), Color.Transparent)
                )
            )
            drawPath(linePath, AccentBlueLight, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

            points.forEachIndexed { index, point ->
                val color = when {
                    index == points.size - 1 -> AccentBlue
                    else -> AccentBlueLight
                }
                val radius = if (index == points.size - 1) 4.dp.toPx() else 3.dp.toPx()
                drawCircle(color = color, radius = radius, center = point)
                if (index == points.size - 1) {
                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = point, style = Stroke(width = 1.5.dp.toPx()))
                }
            }
        }

        if (days.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 9.sp,
                        color = TextWhiteTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularWaterProgress(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 6.dp.toPx()
        val r = (size.minDimension - stroke) / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            color = Color.White.copy(alpha = 0.12f),
            radius = r,
            center = center,
            style = Stroke(width = stroke)
        )

        val sweep = 360f * progress.coerceIn(0f, 1f)
        drawArc(
            brush = Brush.linearGradient(
                listOf(AccentBlueLight, AccentBlue),
                start = Offset(center.x - r, center.y),
                end = Offset(center.x + r, center.y)
            ),
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(center.x - r, center.y - r),
            size = Size(r * 2, r * 2),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextWhite
        )
    }
}
