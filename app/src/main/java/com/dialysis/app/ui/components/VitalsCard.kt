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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 血压
            VitalGlassCard(
                modifier = Modifier.width(120.dp).height(105.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BpIcon(modifier = Modifier.size(13.dp))
                        Text(
                            text = "血压",
                            fontSize = 10.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "${vitals.systolicBP}/${vitals.diastolicBP}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            softWrap = false
                        )
                        Text(
                            text = "mmHg",
                            fontSize = 9.sp,
                            color = TextWhite.copy(alpha = 0.55f),
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
            }

            // 体重
            VitalGlassCard(
                modifier = Modifier.width(120.dp).height(105.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WeightIconSvg(modifier = Modifier.size(13.dp))
                        Text(
                            text = "体重",
                            fontSize = 10.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "${"%.1f".format(vitals.weight)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            softWrap = false
                        )
                        Text(
                            text = "kg",
                            fontSize = 9.sp,
                            color = TextWhite.copy(alpha = 0.55f),
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
            }

            // 饮水
            VitalGlassCard(
                modifier = Modifier.width(150.dp).height(105.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WaterDropIcon(modifier = Modifier.size(13.dp))
                        Text(
                            text = "饮水",
                            fontSize = 10.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "${vitals.waterIntake}/${vitals.waterTarget}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            softWrap = false
                        )
                        Text(
                            text = "ml",
                            fontSize = 9.sp,
                            color = TextWhite.copy(alpha = 0.55f),
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val ratio = vitals.waterIntake.toFloat() / vitals.waterTarget.toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(ProgressTrack, RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(ProgressFillStart, ProgressFillEnd)
                                        ),
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }

            // 心率
            VitalGlassCard(
                modifier = Modifier.width(120.dp).height(105.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HeartRateIcon(modifier = Modifier.size(13.dp))
                        Text(
                            text = "心率",
                            fontSize = 10.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "${vitals.heartRate}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite,
                            softWrap = false
                        )
                        Text(
                            text = "bpm",
                            fontSize = 9.sp,
                            color = TextWhite.copy(alpha = 0.55f),
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
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
        val path = Path().apply {
            moveTo(cx, h * 0.88f)
            cubicTo(cx - w * 0.5f, h * 0.5f, cx - w * 0.5f, h * 0.12f, cx, h * 0.3f)
            cubicTo(cx + w * 0.5f, h * 0.12f, cx + w * 0.5f, h * 0.5f, cx, h * 0.88f)
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
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.25f),
            style = Stroke(width = sw)
        )
        val bodyPath = Path().apply {
            moveTo(w * 0.5f, h * 0.43f)
            lineTo(w * 0.12f, h * 0.95f)
            lineTo(w * 0.88f, h * 0.95f)
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
            moveTo(w * 0.5f, h * 0.08f)
            cubicTo(w * 0.5f, h * 0.08f, w * 0.08f, h * 0.52f, w * 0.08f, h * 0.72f)
            cubicTo(w * 0.08f, h * 0.92f, w * 0.27f, h * 0.95f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.73f, h * 0.95f, w * 0.92f, h * 0.92f, w * 0.92f, h * 0.72f)
            cubicTo(w * 0.92f, h * 0.52f, w * 0.5f, h * 0.08f, w * 0.5f, h * 0.08f)
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
        val midY = h * 0.62f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(w * 0.15f, midY)
            lineTo(w * 0.25f, midY - h * 0.4f)
            lineTo(w * 0.38f, midY + h * 0.4f)
            lineTo(w * 0.5f, midY - h * 0.3f)
            lineTo(w * 0.6f, midY + h * 0.12f)
            lineTo(w * 0.68f, midY)
            lineTo(w, midY)
        }
        drawPath(path, AccentRed, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}
