package com.dialysis.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dialysis.app.ui.components.*
import java.time.LocalDateTime

@Composable
fun HomeScreen() {
    val weather = WeatherInfo(
        temperature = 26,
        humidity = 45,
        windLevel = "3级",
        windDirection = "东南",
        description = "晴"
    )

    val dialysis = DialysisInfo(
        nextTime = LocalDateTime.now().plusDays(2).plusHours(14)
            .withHour(8).withMinute(0).withSecond(0).withNano(0),
        location = "协和医院 · 血液净化中心 · 3号机"
    )

    val vitals = VitalsInfo(
        bpTrendUp = true,
        weightChange = -0.2f,
        waterIntake = 850,
        waterTarget = 1500
    )

    val medications = MedicationInfo(
        medications = emptyList()
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WeatherCard(weather = weather)
            DialysisCard(dialysis = dialysis)
            VitalsSection(vitals = vitals)
            MedicationCard(medicationInfo = medications)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
