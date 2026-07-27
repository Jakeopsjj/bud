package com.dialysis.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dialysis.app.ui.components.*
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val weather = WeatherInfo(
        temperature = 26,
        humidity = 65,
        windLevel = "3级",
        windDirection = "东南",
        description = "多云"
    )

    val dialysis = DialysisInfo(
        nextTime = LocalDateTime.now().plusDays(1).plusHours(5),
        location = "市中心医院血液净化中心"
    )

    val vitals = VitalsInfo(
        heartRate = 78,
        systolicBP = 135,
        diastolicBP = 85,
        weight = 62.5f,
        ultrafiltration = 2.8f,
        dialysisDuration = 4.0f
    )

    val medications = MedicationInfo(
        medications = listOf(
            Medication("硝苯地平缓释片", "20mg", LocalTime.of(7, 0), taken = true),
            Medication("叶酸片", "5mg", LocalTime.of(8, 0), taken = true),
            Medication("骨化三醇", "0.25μg", LocalTime.of(12, 0), taken = false),
            Medication("降压药", "1片", LocalTime.of(19, 0), taken = false),
            Medication("促红素", "3000U", LocalTime.of(21, 0), taken = false)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "透析助手",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherCard(weather = weather)
            DialysisCard(dialysis = dialysis)
            VitalsCard(vitals = vitals)
            MedicationCard(medicationInfo = medications)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
