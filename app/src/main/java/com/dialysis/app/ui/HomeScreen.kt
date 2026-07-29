package com.dialysis.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.TextWhite
import com.dialysis.app.ui.theme.TextWhiteSecondary
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
    val dryWeight = remember(refreshKey) { prefs.getDryWeight() }

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
                androidx.compose.material3.Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
                androidx.compose.material3.Text(
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
                }
            )
        }

        BottomTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
