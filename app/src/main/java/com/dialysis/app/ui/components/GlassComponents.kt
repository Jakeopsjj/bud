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
                                Color(0xFF0D47A1).copy(alpha = 0.40f),
                                Color(0xFF1565C0).copy(alpha = 0.32f),
                                Color(0xFF1976D2).copy(alpha = 0.25f)
                            )
                        )
                    )
                } else {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFD0D2DA).copy(alpha = 0.14f),
                                Color(0xFFC2C4CD).copy(alpha = 0.10f),
                                Color(0xFFB4B6C1).copy(alpha = 0.07f)
                            )
                        )
                    )
                }
            )
            .drawBehind {
                drawRefGlassBorder(crPx, isWeather)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}

private fun DrawScope.drawRefGlassBorder(cornerRadius: Float, isWeather: Boolean) {
    val w = size.width
    val h = size.height
    val dp = density
    val bw = 1.5f * dp

    // 边框垂直渐变：顶部亮白 → 中部淡 → 底部暖金(参考图边框颜色)
    val borderBrush = Brush.verticalGradient(
        colors = if (isWeather) {
            listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.50f),
                Color(0xFFEED9B8).copy(alpha = 0.55f),
                Color(0xFFE8C9A0).copy(alpha = 0.60f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.75f),
                Color.White.copy(alpha = 0.40f),
                Color(0xFFEED9B8).copy(alpha = 0.45f),
                Color(0xFFE8C9A0).copy(alpha = 0.50f)
            )
        }
    )
    drawRoundRect(
        brush = borderBrush,
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = bw)
    )

    // 顶部内高光：极细亮白，沿顶部圆角
    val hlH = 0.6f * dp
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0f),
                Color.White.copy(alpha = if (isWeather) 0.55f else 0.40f),
                Color.White.copy(alpha = if (isWeather) 0.55f else 0.40f),
                Color.White.copy(alpha = 0f)
            ),
            startX = cornerRadius,
            endX = w - cornerRadius
        ),
        topLeft = Offset(bw * 1.5f, bw * 1.5f),
        size = Size(w - bw * 3f, hlH),
        cornerRadius = CornerRadius(cornerRadius - bw * 1.5f),
        blendMode = BlendMode.Plus
    )
}

@Composable
fun GlassCardSmall(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val localDensity = LocalDensity.current
    val crPx = with(localDensity) { 20.dp.toPx() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD0D2DA).copy(alpha = 0.14f),
                        Color(0xFFC2C4CD).copy(alpha = 0.10f),
                        Color(0xFFB4B6C1).copy(alpha = 0.07f)
                    )
                )
            )
            .drawBehind {
                val sw = size.width
                val sh = size.height
                val dpScale = this.density
                val bws = 1.3f * dpScale

                val borderBrushS = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.70f),
                        Color.White.copy(alpha = 0.38f),
                        Color(0xFFEED9B8).copy(alpha = 0.42f),
                        Color(0xFFE8C9A0).copy(alpha = 0.48f)
                    )
                )
                drawRoundRect(
                    brush = borderBrushS,
                    topLeft = Offset.Zero,
                    size = Size(sw, sh),
                    cornerRadius = CornerRadius(crPx),
                    style = Stroke(width = bws)
                )

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0f)
                        ),
                        startX = crPx,
                        endX = sw - crPx
                    ),
                    topLeft = Offset(bws * 1.5f, bws * 1.5f),
                    size = Size(sw - bws * 3f, 0.5f * dpScale),
                    cornerRadius = CornerRadius(crPx - bws * 1.5f),
                    blendMode = BlendMode.Plus
                )
            }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        content = content
    )
}
