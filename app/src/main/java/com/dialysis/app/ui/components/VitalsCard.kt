package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

data class VitalsInfo(
    val systolicBP: Int = 135,
    val diastolicBP: Int = 85,
    val bpTrendUp: Boolean = true,
    val weight: Float = 62.3f,
    val weightChange: Float = -0.2f,
    val waterIntake: Int = 850,
    val waterTarget: Int = 1500,
    val heartRate: Int = 72
)

@Composable
fun VitalsSection(vitals: VitalsInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "今日体征",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhiteSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 血压
            VitalGlassCard(
                modifier = Modifier.width(100.dp).height(92.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        BpIcon(modifier = Modifier.size(14.dp))
                        Text(
                            text = "血压",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextWhiteSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${vitals.systolicBP}/${vitals.diastolicBP}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            lineHeight = 20.sp,
                            maxLines = 1
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "mmHg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite.copy(alpha = 0.75f)
                        )
                        Text(
                            text = if (vitals.bpTrendUp) "↑" else "↓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vitals.bpTrendUp) AccentOrange else AccentGreen
                        )
                    }
                }
            }

            // 体重
            VitalGlassCard(
                modifier = Modifier.width(92.dp).height(92.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        WeightIconSvg(modifier = Modifier.size(14.dp))
                        Text(
                            text = "体重",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextWhiteSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${"%.1f".format(vitals.weight)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            lineHeight = 20.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "kg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite.copy(alpha = 0.75f),
                            modifier = Modifier.padding(start = 3.dp, bottom = 1.dp)
                        )
                    }
                    Text(
                        text = if (vitals.weightChange < 0)
                            "↓${"%.1f".format(kotlin.math.abs(vitals.weightChange))}kg"
                        else
                            "↑${"%.1f".format(vitals.weightChange)}kg",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (vitals.weightChange < 0) AccentGreen else AccentRed,
                        maxLines = 1
                    )
                }
            }

            // 饮水 (wider card with progress bar)
            VitalGlassCard(
                modifier = Modifier.width(128.dp).height(92.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        WaterDropIcon(modifier = Modifier.size(14.dp))
                        Text(
                            text = "饮水",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextWhiteSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${vitals.waterIntake}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            lineHeight = 20.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "ml",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite.copy(alpha = 0.75f),
                            modifier = Modifier.padding(start = 2.dp, bottom = 1.dp)
                        )
                        Text(
                            text = "/${vitals.waterTarget}ml",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextWhiteMuted,
                            modifier = Modifier.padding(start = 2.dp, bottom = 1.dp),
                            maxLines = 1
                        )
                    }
                    val ratio = vitals.waterIntake.toFloat() / vitals.waterTarget.toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(ProgressTrack, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(ProgressFillStart, ProgressFillEnd)
                                    ),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }

            // 心率
            VitalGlassCard(
                modifier = Modifier.width(92.dp).height(92.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        HeartRateIcon(modifier = Modifier.size(14.dp))
                        Text(
                            text = "心率",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextWhiteSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${vitals.heartRate}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            lineHeight = 20.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "bpm",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite.copy(alpha = 0.75f),
                            modifier = Modifier.padding(start = 3.dp, bottom = 1.dp)
                        )
                    }
                    // Empty space to balance layout
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BpIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.5.dp.toPx()
        val color = AccentRedSoft
        val cx = w * 0.5f
        // Heart shape (blood pressure)
        val path = Path().apply {
            moveTo(cx, h * 0.85f)
            cubicTo(cx - w * 0.5f, h * 0.5f, cx - w * 0.5f, h * 0.15f, cx, h * 0.3f)
            cubicTo(cx + w * 0.5f, h * 0.15f, cx + w * 0.5f, h * 0.5f, cx, h * 0.85f)
            close()
        }
        drawPath(path, color, style = Stroke(width = strokeW))
    }
}

@Composable
private fun WeightIconSvg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.5.dp.toPx()
        val color = Color.White.copy(alpha = 0.85f)
        // Head circle
        drawCircle(
            color = color,
            radius = w * 0.16f,
            center = Offset(w * 0.5f, h * 0.22f),
            style = Stroke(width = sw)
        )
        // Body (shoulders to feet)
        val bodyPath = Path().apply {
            moveTo(w * 0.5f, h * 0.38f)
            lineTo(w * 0.22f, h * 0.92f)
            lineTo(w * 0.78f, h * 0.92f)
            close()
        }
        drawPath(bodyPath, color, style = Stroke(width = sw))
    }
}

@Composable
private fun WaterDropIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.5.dp.toPx()
        val color = AccentBlueLight
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            cubicTo(w * 0.5f, h * 0.1f, w * 0.1f, h * 0.5f, w * 0.1f, h * 0.7f)
            cubicTo(w * 0.1f, h * 0.9f, w * 0.28f, h * 0.95f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.72f, h * 0.95f, w * 0.9f, h * 0.9f, w * 0.9f, h * 0.7f)
            cubicTo(w * 0.9f, h * 0.5f, w * 0.5f, h * 0.1f, w * 0.5f, h * 0.1f)
            close()
        }
        drawPath(path, color, style = Stroke(width = sw))
    }
}

@Composable
private fun HeartRateIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.5.dp.toPx()
        val midY = h * 0.55f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(w * 0.18f, midY)
            lineTo(w * 0.28f, midY - h * 0.3f)
            lineTo(w * 0.38f, midY + h * 0.4f)
            lineTo(w * 0.48f, midY - h * 0.35f)
            lineTo(w * 0.58f, midY + h * 0.15f)
            lineTo(w * 0.68f, midY)
            lineTo(w, midY)
        }
        drawPath(path, AccentRed, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}
