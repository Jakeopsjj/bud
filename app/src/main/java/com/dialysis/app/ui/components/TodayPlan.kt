package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

data class MedItem(
    val id: String,
    val name: String,
    val time: String,
    val isTaken: Boolean = false,
    val badge: String? = null
)

@Composable
fun TodayMedication(
    meds: List<MedItem>,
    onMedToggle: ((String) -> Unit)? = null,
    onAddMed: (() -> Unit)? = null,
    onDeleteMed: ((String) -> Unit)? = null
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        theme = GlassTheme.Sunny,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CapsuleIcon(modifier = Modifier.size(14.dp))
                    Text(
                        text = "今日用药",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                    if (meds.isNotEmpty()) {
                        val takenCount = meds.count { it.isTaken }
                        Text(
                            text = "$takenCount/${meds.size}已服用",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = AccentGreen,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                if (onAddMed != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onAddMed
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AddMedIcon()
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (meds.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "暂无用药记录",
                            fontSize = 12.sp,
                            color = TextWhiteSecondary
                        )
                        if (onAddMed != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onAddMed
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "+ 添加用药",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AccentBlueLight
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    meds.forEachIndexed { index, med ->
                        MedRow(
                            med = med,
                            showDivider = index > 0,
                            onClick = if (onMedToggle != null) { { onMedToggle(med.id) } } else null,
                            onLongClick = if (onDeleteMed != null) { { onDeleteMed(med.id) } } else null
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MedRow(
    med: MedItem,
    showDivider: Boolean,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    Column {
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color.White.copy(alpha = 0.10f))
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null || onLongClick != null) {
                        Modifier.combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onClick?.invoke() },
                            onLongClick = { onLongClick?.invoke() }
                        )
                    } else Modifier
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = med.name,
                fontSize = 12.sp,
                fontWeight = if (med.isTaken) FontWeight.Normal else FontWeight.Normal,
                color = if (med.isTaken) TextWhiteSecondary else TextWhite,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = med.time,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextWhiteSecondary
                )
                if (med.badge != null) {
                    Box(
                        modifier = Modifier
                            .background(AccentPurpleBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = med.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentPurple
                        )
                    }
                }
                if (med.isTaken) {
                    CheckCircle(modifier = Modifier.size(18.dp))
                } else {
                    EmptyCircle(modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AddMedIcon() {
    Canvas(modifier = Modifier.size(18.dp)) {
        val sw = 2.dp.toPx()
        val c = AccentBlueLight
        drawLine(c, Offset(size.width * 0.5f, size.height * 0.2f), Offset(size.width * 0.5f, size.height * 0.8f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(c, Offset(size.width * 0.2f, size.height * 0.5f), Offset(size.width * 0.8f, size.height * 0.5f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
private fun CapsuleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 2.dp.toPx()
        val color = Color.White.copy(alpha = 0.9f)
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
private fun CheckCircle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = AccentGreen,
            radius = w * 0.5f,
            center = Offset(w / 2f, h / 2f)
        )
        val sw = 2.2.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.28f, h * 0.52f)
            lineTo(w * 0.44f, h * 0.68f)
            lineTo(w * 0.74f, h * 0.34f)
        }
        drawPath(path, Color.White, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}

@Composable
private fun EmptyCircle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = w * 0.42f,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
