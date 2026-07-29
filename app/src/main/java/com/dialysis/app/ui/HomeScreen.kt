package com.dialysis.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.TextWhite
import com.dialysis.app.ui.theme.TextWhiteSecondary
import java.time.LocalDateTime

private val monthNames = arrayOf("1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月")
private val weekdayNames = arrayOf("星期一","星期二","星期三","星期四","星期五","星期六","星期日")

@Composable
fun HomeScreen(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit
) {
    val now = LocalDateTime.now()
    val dateStr = "${monthNames[now.monthValue - 1]}${now.dayOfMonth}日 ${weekdayNames[now.dayOfWeek.value - 1]}"

    val weather = WeatherInfo(
        temperature = 26,
        humidity = 45,
        windLevel = "3级",
        windDirection = "东南",
        description = "晴"
    )

    val nextDialysis = LocalDateTime.now().plusDays(2).plusHours(14)
        .withHour(8).withMinute(0).withSecond(0).withNano(0)
    val dialysis = DialysisInfo(
        nextTime = nextDialysis,
        location = "协和医院 · 血液净化中心 · 3号机"
    )

    val vitals = VitalsInfo(
        systolicBP = 135,
        diastolicBP = 85,
        bpTrendUp = true,
        weight = 62.3f,
        weightChange = -0.2f,
        waterIntake = 850,
        waterTarget = 1500,
        heartRate = 72
    )

    val meds = listOf(
        MedItem("降压药 · 氨氯地平", "08:00", isTaken = true),
        MedItem("铁剂 · 多糖铁复合物", "12:00"),
        MedItem("磷结合剂 · 碳酸钙", "餐中"),
        MedItem("促红素 · EPO", "透析日", badge = "注射"),
        MedItem("活性维生素D", "睡前"),
        MedItem("叶酸片", "每日一次", isTaken = true)
    )

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

            // Greeting
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
                    text = "早上好，王阿姨",
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
            TodayMedication(meds = meds)
        }

        // Bottom tab bar
        BottomTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
