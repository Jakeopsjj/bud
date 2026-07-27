package com.dialysis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

data class VitalSign(
    val name: String,
    val value: String,
    val unit: String,
    val icon: ImageVector,
    val isNormal: Boolean = true
)

data class VitalsInfo(
    val heartRate: Int,
    val systolicBP: Int,
    val diastolicBP: Int,
    val weight: Float,
    val ultrafiltration: Float,
    val dialysisDuration: Float
)

@Composable
fun VitalsCard(vitals: VitalsInfo) {
    val vitalItems = listOf(
        VitalSign("心率", "${vitals.heartRate}", "次/分", Icons.Default.Favorite),
        VitalSign("血压", "${vitals.systolicBP}/${vitals.diastolicBP}", "mmHg", Icons.Default.MonitorHeart),
        VitalSign("体重", "${vitals.weight}", "kg", Icons.Default.Scale),
        VitalSign("超滤量", "${vitals.ultrafiltration}", "L", Icons.Default.Opacity, isNormal = vitals.ultrafiltration <= 3.0f),
        VitalSign("透析时长", "${vitals.dialysisDuration}", "小时", Icons.Default.AccessTime)
    )

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
                        colors = listOf(VitalsCardStart, VitalsCardEnd)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "今日体征",
                    style = MaterialTheme.typography.titleMedium,
                    color = Warning,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(vitalItems) { item ->
                        VitalItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalItemCard(item: VitalSign) {
    val valueColor = if (item.isNormal) OnBackground else Error

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = Warning,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = item.value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = item.unit,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}
