package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

data class VitalsInfo(
    val systolicBP: Int? = null,
    val diastolicBP: Int? = null,
    val bpTrendUp: Boolean = true,
    val weight: Float? = null,
    val weightChange: Float = -0.2f,
    val waterIntake: Int = 850,
    val waterTarget: Int = 1500,
    val heartRate: Int? = null
)

@Composable
fun VitalsSection(vitals: VitalsInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "今日体征",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextWhiteSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VitalSmallCard(
                icon = { EcgIcon(AccentRed) },
                title = "血压",
                bottomContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "mmHg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhiteSecondary
                        )
                        Text(
                            text = if (vitals.bpTrendUp) "↑" else "↓",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vitals.bpTrendUp) AccentOrange else AccentGreen
                        )
                    }
                }
            )

            VitalSmallCard(
                icon = { WeightIcon() },
                title = "体重",
                bottomContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhiteSecondary
                        )
                        Text(
                            text = if (vitals.weightChange < 0)
                                "↓${"%.1f".format(kotlin.math.abs(vitals.weightChange))}kg"
                            else
                                "↑${"%.1f".format(vitals.weightChange)}kg",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vitals.weightChange < 0) AccentGreen else AccentRed
                        )
                    }
                }
            )

            VitalSmallCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "饮水",
                        tint = AccentBlue,
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = "饮水",
                centerContent = {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "${vitals.waterIntake}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "/${vitals.waterTarget}ml",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhiteSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                },
                modifier = Modifier.width(160.dp)
            )

            VitalSmallCard(
                icon = { EcgIcon(AccentRed) },
                title = "心率",
                bottomContent = {
                    Text(
                        text = "bpm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhiteSecondary
                    )
                }
            )
        }
    }
}

@Composable
private fun VitalSmallCard(
    icon: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    centerContent: (@Composable () -> Unit)? = null,
    bottomContent: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .width(120.dp)
            .height(140.dp)
            .background(
                color = GlassWhite,
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.5.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon()
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
            }

            if (centerContent != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    centerContent()
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (bottomContent != null) {
                bottomContent()
            }
        }
    }
}

@Composable
private fun EcgIcon(tint: Color) {
    Canvas(modifier = Modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val midY = h * 0.55f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(w * 0.15f, midY)
            lineTo(w * 0.25f, midY - h * 0.25f)
            lineTo(w * 0.35f, midY + h * 0.35f)
            lineTo(w * 0.45f, midY - h * 0.4f)
            lineTo(w * 0.55f, midY + h * 0.15f)
            lineTo(w * 0.65f, midY)
            lineTo(w * 0.8f, midY - h * 0.2f)
            lineTo(w * 0.9f, midY + h * 0.2f)
            lineTo(w, midY)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun WeightIcon() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.25f)
        )
        val bodyPath = Path().apply {
            moveTo(w * 0.5f, h * 0.4f)
            lineTo(w * 0.2f, h * 0.95f)
            lineTo(w * 0.8f, h * 0.95f)
            close()
        }
        drawPath(
            path = bodyPath,
            color = Color.White.copy(alpha = 0.9f),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.3f, h * 0.55f),
            end = Offset(w * 0.7f, h * 0.55f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.05f, h * 0.95f),
            end = Offset(w * 0.95f, h * 0.95f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
