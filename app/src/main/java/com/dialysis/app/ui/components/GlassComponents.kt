package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dialysis.app.ui.theme.*

@Composable
fun SkyBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(SkyTop, SkyBottom, SkyHorizon),
                startY = 0f,
                endY = size.height
            ),
            size = size
        )

        drawCloud(this, Offset(size.width * 0.15f, size.height * 0.08f), 60f, 0.35f)
        drawCloud(this, Offset(size.width * 0.75f, size.height * 0.15f), 80f, 0.25f)
        drawCloud(this, Offset(size.width * 0.3f, size.height * 0.25f), 50f, 0.3f)
        drawCloud(this, Offset(size.width * 0.85f, size.height * 0.35f), 70f, 0.2f)
        drawCloud(this, Offset(size.width * 0.5f, size.height * 0.45f), 90f, 0.28f)
        drawCloud(this, Offset(size.width * 0.2f, size.height * 0.55f), 55f, 0.22f)
        drawCloud(this, Offset(size.width * 0.7f, size.height * 0.6f), 75f, 0.3f)
        drawCloud(this, Offset(size.width * 0.1f, size.height * 0.72f), 65f, 0.2f)
        drawCloud(this, Offset(size.width * 0.6f, size.height * 0.78f), 85f, 0.25f)
        drawCloud(this, Offset(size.width * 0.35f, size.height * 0.88f), 100f, 0.35f)
        drawCloud(this, Offset(size.width * 0.8f, size.height * 0.92f), 70f, 0.2f)
        drawCloud(this, Offset(size.width * 0.9f, size.height * 0.5f), 50f, 0.18f)
        drawCloud(this, Offset(size.width * 0.05f, size.height * 0.38f), 45f, 0.22f)
    }
}

private fun drawCloud(scope: DrawScope, center: Offset, radius: Float, alpha: Float) {
    val cloudColor = Color.White.copy(alpha = alpha)
    val path = Path()
    val r = radius
    path.addOval(Rect(center.x - r, center.y - r * 0.4f, center.x + r, center.y + r * 0.5f))
    path.addOval(Rect(center.x - r * 1.3f, center.y - r * 0.2f, center.x - r * 0.3f, center.y + r * 0.4f))
    path.addOval(Rect(center.x + r * 0.2f, center.y - r * 0.3f, center.x + r * 1.2f, center.y + r * 0.3f))
    path.addOval(Rect(center.x - r * 0.5f, center.y - r * 0.6f, center.x + r * 0.5f, center.y + r * 0.2f))
    scope.drawPath(path, cloudColor)
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    isWeather: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val density = LocalDensity.current
    val crPx = with(density) { cornerRadius.toPx() }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (isWeather) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0D47A1).copy(alpha = 0.35f),
                                Color(0xFF1565C0).copy(alpha = 0.28f),
                                Color(0xFF1976D2).copy(alpha = 0.22f)
                            )
                        )
                    )
                } else {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.10f),
                                Color.White.copy(alpha = 0.06f)
                            )
                        )
                    )
                }
            )
            .drawBehind {
                drawAppleGlassBorder(crPx, isWeather)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}

private fun DrawScope.drawAppleGlassBorder(cornerRadius: Float, isWeather: Boolean) {
    val w = size.width
    val h = size.height
    val borderW = 1.2.dp.toPx()

    // 主边框：上白亮、两侧稍暗、底部略带暖金色
    val borderBrush = Brush.verticalGradient(
        colors = if (isWeather) {
            listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.55f),
                Color.White.copy(alpha = 0.30f),
                Color(0xFFFFE0B2).copy(alpha = 0.35f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.75f),
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.20f),
                Color(0xFFFFE0B2).copy(alpha = 0.25f)
            )
        }
    )
    drawRoundRect(
        brush = borderBrush,
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = borderW)
    )

    // 顶部内高光：极细的亮白线
    val highlightH = 0.8.dp.toPx()
    val topHighlightPath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    offset = Offset(borderW * 1.5f, borderW * 1.5f),
                    size = Size(w - borderW * 3f, highlightH + 2.dp.toPx())
                ),
                cornerRadius = CornerRadius(cornerRadius * 0.8f)
            )
        )
    }
    drawPath(
        path = topHighlightPath,
        color = Color.White.copy(alpha = if (isWeather) 0.5f else 0.4f),
    )

    // 左上高光小弧：更亮
    drawLine(
        color = Color.White.copy(alpha = if (isWeather) 0.6f else 0.5f),
        start = Offset(cornerRadius * 0.8f, borderW),
        end = Offset(w - cornerRadius * 0.8f, borderW),
        strokeWidth = 0.6.dp.toPx(),
        cap = StrokeCap.Round,
        blendMode = BlendMode.Plus
    )

    // 外投影（底部柔和阴影）
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.06f),
                Color.Black.copy(alpha = 0.10f)
            ),
            startY = h - 6.dp.toPx(),
            endY = h + 4.dp.toPx()
        ),
        topLeft = Offset(-1.dp.toPx(), h - 4.dp.toPx()),
        size = Size(w + 2.dp.toPx(), 8.dp.toPx()),
        cornerRadius = CornerRadius(cornerRadius),
        blendMode = BlendMode.Multiply
    )
}

@Composable
fun GlassCardSmall(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val density = LocalDensity.current
    val crPx = with(density) { 20.dp.toPx() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.10f),
                        Color.White.copy(alpha = 0.06f)
                    )
                )
            )
            .drawBehind {
                val sw = size.width
                val sh = size.height
                val bw = 1.dp.toPx()

                val borderBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.2f),
                        Color(0xFFFFE0B2).copy(alpha = 0.22f)
                    )
                )
                drawRoundRect(
                    brush = borderBrush,
                    topLeft = Offset.Zero,
                    size = Size(sw, sh),
                    cornerRadius = CornerRadius(crPx),
                    style = Stroke(width = bw)
                )

                val highlightH = 0.6.dp.toPx()
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.35f),
                    topLeft = Offset(bw * 1.5f, bw * 1.5f),
                    size = Size(sw - bw * 3f, highlightH + 1.5.dp.toPx()),
                    cornerRadius = CornerRadius(crPx * 0.8f)
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.45f),
                    start = Offset(crPx * 0.7f, bw),
                    end = Offset(sw - crPx * 0.7f, bw),
                    strokeWidth = 0.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    blendMode = BlendMode.Plus
                )

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.08f)
                        )
                    ),
                    topLeft = Offset(-0.5.dp.toPx(), sh - 3.dp.toPx()),
                    size = Size(sw + 1.dp.toPx(), 5.dp.toPx()),
                    cornerRadius = CornerRadius(crPx)
                )
            }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        content = content
    )
}
