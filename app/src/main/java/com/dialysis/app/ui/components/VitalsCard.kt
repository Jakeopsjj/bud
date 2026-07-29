package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 血压
            VitalGlassCard(
                modifier = Modifier.weight(1f).height(76.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 7.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        BpIcon(modifier = Modifier.size(11.dp))
                        Text(
                            text = "血压",
                            fontSize = 9.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextWhite)) {
                                append("${vitals.systolicBP}/${vitals.diastolicBP}")
                            }
                            withStyle(SpanStyle(fontSize = 8.sp, color = TextWhite.copy(alpha = 0.65f))) {
                                append("mmHg")
                            }
                        },
                        maxLines = 1
                    )
                }
            }

            // 体重
            VitalGlassCard(
                modifier = Modifier.weight(1f).height(76.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 7.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        WeightIconSvg(modifier = Modifier.size(11.dp))
                        Text(
                            text = "体重",
                            fontSize = 9.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextWhite)) {
                                append("${"%.1f".format(vitals.weight)}")
                            }
                            withStyle(SpanStyle(fontSize = 8.sp, color = TextWhite.copy(alpha = 0.65f))) {
                                append("kg")
                            }
                        },
                        maxLines = 1
                    )
                }
            }

            // 饮水
            VitalGlassCard(
                modifier = Modifier.weight(1f).height(76.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 7.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        WaterDropIcon(modifier = Modifier.size(11.dp))
                        Text(
                            text = "饮水",
                            fontSize = 9.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Column {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextWhite)) {
                                    append("${vitals.waterIntake}")
                                }
                                withStyle(SpanStyle(fontSize = 8.sp, color = TextWhite.copy(alpha = 0.65f))) {
                                    append("/${vitals.waterTarget}ml")
                                }
                            },
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val ratio = vitals.waterIntake.toFloat() / vitals.waterTarget.toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
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
                modifier = Modifier.weight(1f).height(76.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 7.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        HeartRateIcon(modifier = Modifier.size(11.dp))
                        Text(
                            text = "心率",
                            fontSize = 9.sp,
                            color = TextWhiteSecondary
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextWhite)) {
                                append("${vitals.heartRate}")
                            }
                            withStyle(SpanStyle(fontSize = 8.sp, color = TextWhite.copy(alpha = 0.65f))) {
                                append("bpm")
                            }
                        },
                        maxLines = 1
                    )
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
        val strokeW = 1.2.dp.toPx()
        val color = AccentRedSoft
        val cx = w * 0.5f
        val path = Path().apply {
            moveTo(cx, h * 0.9f)
            cubicTo(cx - w * 0.5f, h * 0.5f, cx - w * 0.5f, h * 0.1f, cx, h * 0.3f)
            cubicTo(cx + w * 0.5f, h * 0.1f, cx + w * 0.5f, h * 0.5f, cx, h * 0.9f)
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
        val sw = 1.2.dp.toPx()
        val color = Color.White.copy(alpha = 0.85f)
        drawCircle(
            color = color,
            radius = w * 0.2f,
            center = Offset(w * 0.5f, h * 0.28f),
            style = Stroke(width = sw)
        )
        val bodyPath = Path().apply {
            moveTo(w * 0.5f, h * 0.48f)
            lineTo(w * 0.1f, h * 0.98f)
            lineTo(w * 0.9f, h * 0.98f)
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
        val sw = 1.2.dp.toPx()
        val color = AccentBlueLight
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            cubicTo(w * 0.5f, h * 0.05f, w * 0.05f, h * 0.55f, w * 0.05f, h * 0.75f)
            cubicTo(w * 0.05f, h * 0.95f, w * 0.25f, h * 0.95f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.75f, h * 0.95f, w * 0.95f, h * 0.95f, w * 0.95f, h * 0.75f)
            cubicTo(w * 0.95f, h * 0.55f, w * 0.5f, h * 0.05f, w * 0.5f, h * 0.05f)
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
        val sw = 1.2.dp.toPx()
        val midY = h * 0.65f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(w * 0.12f, midY)
            lineTo(w * 0.22f, midY - h * 0.4f)
            lineTo(w * 0.36f, midY + h * 0.4f)
            lineTo(w * 0.48f, midY - h * 0.25f)
            lineTo(w * 0.58f, midY + h * 0.1f)
            lineTo(w * 0.65f, midY)
            lineTo(w, midY)
        }
        drawPath(path, AccentRed, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}
