package com.dialysis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dialysis.app.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Medication(
    val name: String,
    val dosage: String,
    val time: LocalTime,
    val taken: Boolean
)

data class MedicationInfo(
    val medications: List<Medication>
)

@Composable
fun MedicationCard(medicationInfo: MedicationInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(MedicationCardStart, MedicationCardEnd)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "今日用药",
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color(0xFF7B1FA2),
                        fontWeight = FontWeight.SemiBold
                    )
                    val takenCount = medicationInfo.medications.count { it.taken }
                    Text(
                        text = "$takenCount/${medicationInfo.medications.size} 已服用",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 280.dp)
                ) {
                    items(medicationInfo.medications) { medication ->
                        MedicationItem(medication)
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationItem(medication: Medication) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val cardColor = if (medication.taken) {
        androidx.compose.ui.graphics.Color(0xFFE8F5E9)
    } else {
        Surface
    }
    val iconColor = if (medication.taken) Secondary else Warning

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = medication.name,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (medication.taken) TextDecoration.LineThrough else null,
                        color = if (medication.taken) TextSecondary else OnBackground
                    )
                    Text(
                        text = "${medication.dosage} · ${medication.time.format(timeFormatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Icon(
                imageVector = if (medication.taken) Icons.Default.Check else Icons.Default.Pending,
                contentDescription = if (medication.taken) "已服用" else "待服用",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
