package com.dialysis.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.*
import java.time.LocalDateTime

private val monthNames = arrayOf("1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月")
private val weekdayNames = arrayOf("星期一","星期二","星期三","星期四","星期五","星期六","星期日")

@Composable
fun HomeScreen(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    prefs: AppPreferences
) {
    var refreshKey by remember { mutableStateOf(0) }
    val refresh = { refreshKey++ }

    var showAddMedDialog by remember { mutableStateOf(false) }
    var deleteMedId by remember { mutableStateOf<String?>(null) }

    val now = LocalDateTime.now()
    val dateStr = "${monthNames[now.monthValue - 1]}${now.dayOfMonth}日 ${weekdayNames[now.dayOfWeek.value - 1]}"

    val hour = now.hour
    val greeting = when {
        hour < 6 -> "凌晨好"
        hour < 9 -> "早上好"
        hour < 12 -> "上午好"
        hour < 14 -> "中午好"
        hour < 18 -> "下午好"
        hour < 22 -> "晚上好"
        else -> "夜深了"
    }

    val weather = WeatherInfo(
        temperature = 26,
        humidity = 45,
        windLevel = "3级",
        windDirection = "东南",
        description = "晴"
    )

    val nextDialysis = remember(refreshKey) { prefs.getNextDialysis() }
    val dialysis = DialysisInfo(
        nextTime = nextDialysis.date,
        location = "${nextDialysis.location} · ${nextDialysis.bedNumber}"
    )

    val bp = remember(refreshKey) { prefs.getLatestBp() }
    val weight = remember(refreshKey) { prefs.getLatestWeight() }
    val waterIntake = remember(refreshKey) { prefs.getTodayWaterIntake() }
    val waterTarget = remember(refreshKey) { prefs.getWaterTarget() }
    val heartRate = remember(refreshKey) { prefs.getHeartRate() }

    val weightRecords = remember(refreshKey) { prefs.getWeightRecords() }
    val weightChange = if (weightRecords.size >= 2) {
        weightRecords.last().weight - weightRecords[weightRecords.size - 2].weight
    } else 0f

    val bpRecords = remember(refreshKey) { prefs.getBpRecords() }
    val bpTrendUp = if (bpRecords.size >= 2) {
        bpRecords.last().systolic >= bpRecords[bpRecords.size - 2].systolic
    } else true

    val vitals = VitalsInfo(
        systolicBP = bp.first,
        diastolicBP = bp.second,
        bpTrendUp = bpTrendUp,
        weight = weight,
        weightChange = weightChange,
        waterIntake = waterIntake,
        waterTarget = waterTarget,
        heartRate = heartRate
    )

    val medsData = remember(refreshKey) { prefs.getMedications() }
    val meds = medsData.map { MedItem(it.id, it.name, it.time, it.taken, it.badge) }

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackground(theme = GlassTheme.Sunny)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 0.dp)
                .padding(bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
                Text(
                    text = "$greeting，王阿姨",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            WeatherCard(weather = weather)
            DialysisCard(dialysis = dialysis)
            VitalsSection(vitals = vitals)
            TodayMedication(
                meds = meds,
                onMedToggle = { medId ->
                    prefs.toggleMedication(medId)
                    refresh()
                },
                onAddMed = { showAddMedDialog = true },
                onDeleteMed = { medId ->
                    deleteMedId = medId
                }
            )
        }

        BottomTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Add Medication Dialog
    if (showAddMedDialog) {
        AddMedicationDialog(
            onDismiss = { showAddMedDialog = false },
            onConfirm = { name, time, badge ->
                prefs.addMedication(name, time, badge)
                refresh()
                showAddMedDialog = false
            }
        )
    }

    // Delete confirmation
    if (deleteMedId != null) {
        Dialog(onDismissRequest = { deleteMedId = null }) {
            LiquidGlassCard(
                theme = GlassTheme.Sunny,
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
private fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, time: String, badge: String?) -> Unit
) {
    var medName by remember { mutableStateOf("") }
    var medTime by remember { mutableStateOf("") }
    var medBadge by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            theme = GlassTheme.Sunny,
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedBorderColor = AccentBlueLight.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = medTime,
                    onValueChange = { medTime = it },
                    label = { Text("服用时间（如：08:00/餐前/睡前）", color = TextWhiteSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedBorderColor = AccentBlueLight.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = medBadge,
                    onValueChange = { medBadge = it },
                    label = { Text("备注（可选，如：注射）", color = TextWhiteSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentBlueLight,
                        focusedBorderColor = AccentBlueLight.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
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
