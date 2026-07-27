package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*
import java.time.LocalTime

data class Medication(
    val name: String,
    val dosage: String,
    val time: LocalTime,
    val taken: Boolean
)

data class MedicationInfo(
    val medications: List<Medication> = emptyList()
)

@Composable
fun MedicationCard(medicationInfo: MedicationInfo) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MedKitIcon()
                    Text(
                        text = "今日用药",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
                Text(
                    text = "共${medicationInfo.medications.size}种",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteSecondary
                )
            }

            if (medicationInfo.medications.isEmpty()) {
                Text(
                    text = "暂无用药记录，请添加用药信息",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhiteTertiary
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    medicationInfo.medications.forEach { med ->
                        MedicationItem(med)
                    }
                }
            }
        }
    }
}

@Composable
private fun MedKitIcon() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.85f)
            close()
        }
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.9f),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.5f, h * 0.25f),
            end = Offset(w * 0.5f, h * 0.15f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.35f, h * 0.15f),
            end = Offset(w * 0.65f, h * 0.15f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.5f, h * 0.4f),
            end = Offset(w * 0.5f, h * 0.7f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.35f, h * 0.55f),
            end = Offset(w * 0.65f, h * 0.55f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun MedicationItem(medication: Medication) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = medication.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (medication.taken) TextWhiteTertiary else TextWhite
            )
            Text(
                text = "${medication.dosage} · ${medication.time}",
                fontSize = 14.sp,
                color = TextWhiteMuted
            )
        }
        Text(
            text = if (medication.taken) "✓" else "○",
            fontSize = 18.sp,
            color = if (medication.taken) AccentGreen else TextWhiteTertiary
        )
    }
}
