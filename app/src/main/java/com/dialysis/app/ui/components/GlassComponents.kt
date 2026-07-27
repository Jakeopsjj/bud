package com.dialysis.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
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
import kotlin.math.sin
import kotlin.random.Random

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

    path.addOval(androidx.compose.ui.geometry.Rect(center.x - r, center.y - r * 0.4f, center.x + r, center.y + r * 0.5f))
    path.addOval(androidx.compose.ui.geometry.Rect(center.x - r * 1.3f, center.y - r * 0.2f, center.x - r * 0.3f, center.y + r * 0.4f))
    path.addOval(androidx.compose.ui.geometry.Rect(center.x + r * 0.2f, center.y - r * 0.3f, center.x + r * 1.2f, center.y + r * 0.3f))
    path.addOval(androidx.compose.ui.geometry.Rect(center.x - r * 0.5f, center.y - r * 0.6f, center.x + r * 0.5f, center.y + r * 0.2f))

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

    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparklePhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "s1"
    )
    val sparklePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, delayMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "s2"
    )
    val sparklePhase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, delayMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "s3"
    )
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )

    val sparkleSeeds = remember {
        List(5) { Random.nextFloat() to Random.nextFloat() }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (isWeather) Modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A5FB4).copy(alpha = 0.55f),
                            Color(0xFF2980B9).copy(alpha = 0.40f),
                            Color(0xFF3498DB).copy(alpha = 0.30f)
                        )
                    )
                ) else Modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.20f),
                            Color.White.copy(alpha = 0.10f)
                        )
                    )
                )
            )
            .drawBehind {
                drawLiquidGlassEffects(
                    isWeather = isWeather,
                    cornerRadius = crPx,
                    sparklePhase1 = sparklePhase1,
                    sparklePhase2 = sparklePhase2,
                    sparklePhase3 = sparklePhase3,
                    flowPhase = flowPhase,
                    sparkleSeeds = sparkleSeeds
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier.padding(contentPadding), content = content)

        Canvas(modifier = Modifier.matchParentSize()) {
            drawGlassBorder(crPx, isWeather)
        }
    }
}

private fun DrawScope.drawLiquidGlassEffects(
    isWeather: Boolean,
    cornerRadius: Float,
    sparklePhase1: Float,
    sparklePhase2: Float,
    sparklePhase3: Float,
    flowPhase: Float,
    sparkleSeeds: List<Pair<Float, Float>>
) {
    val w = size.width
    val h = size.height

    // 1. 内部高光条（对角线方向流动的光带 - 液态反光效果）
    val flowX = -w * 0.5f + flowPhase * (w * 2f)
    val highlightPath = Path().apply {
        val bandWidth = w * 0.6f
        addOval(
            Rect(
                offset = Offset(flowX, -h * 0.5f),
                size = Size(bandWidth, h * 2f)
            )
        )
    }
    drawPath(
        path = highlightPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.06f),
                Color.Transparent
            ),
            start = Offset(flowX, 0f),
            end = Offset(flowX + w * 0.3f, h)
        ),
        blendMode = BlendMode.Plus
    )

    // 2. 顶部边缘高光（模拟玻璃上沿反光）
    val topHighlightBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.15f),
            Color.Transparent
        ),
        startY = 0f,
        endY = 4.dp.toPx() * 3f
    )
    drawRoundRect(
        brush = topHighlightBrush,
        topLeft = Offset.Zero,
        size = Size(w, 8.dp.toPx()),
        cornerRadius = CornerRadius(cornerRadius),
        blendMode = BlendMode.Plus
    )

    // 3. 左侧边缘高光
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.08f),
                Color.Transparent
            )
        ),
        topLeft = Offset.Zero,
        size = Size(4.dp.toPx(), h),
        cornerRadius = CornerRadius(cornerRadius),
        blendMode = BlendMode.Plus
    )

    // 4. 底部暗边（模拟玻璃厚度阴影）
    val bottomShadow = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.08f),
            Color.Black.copy(alpha = 0.15f)
        ),
        startY = h - 8.dp.toPx(),
        endY = h
    )
    drawRoundRect(
        brush = bottomShadow,
        topLeft = Offset(0f, h - 10.dp.toPx()),
        size = Size(w, 10.dp.toPx()),
        cornerRadius = CornerRadius(cornerRadius)
    )

    // 5. 内部整体渐变覆盖（增加玻璃的折射厚度感）
    if (!isWeather) {
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(w * 0.3f, h * 0.2f),
                radius = w * 0.7f
            ),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(cornerRadius),
            blendMode = BlendMode.Plus
        )
    }

    // 6. 星星闪烁高光
    val phases = listOf(sparklePhase1, sparklePhase2, sparklePhase3, (sparklePhase1 + 0.7f) % 1f, (sparklePhase2 + 0.5f) % 1f)
    sparkleSeeds.forEachIndexed { idx, (sx, sy) ->
        val phase = phases[idx % phases.size]
        val alpha = if (phase < 0.5f) {
            (phase * 2f) * 0.7f + 0.3f
        } else {
            ((1f - phase) * 2f) * 0.7f + 0.3f
        }
        val cx = sx * w
        val cy = sy * h
        val sparkleSize = 2.dp.toPx() + (phase * 3.dp.toPx())

        val starColor = if (isWeather) {
            Color.White.copy(alpha = alpha * 0.8f)
        } else {
            Color.White.copy(alpha = alpha * 0.9f)
        }

        // 十字星光
        drawLine(
            color = starColor,
            start = Offset(cx - sparkleSize * 2f, cy),
            end = Offset(cx + sparkleSize * 2f, cy),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = starColor,
            start = Offset(cx, cy - sparkleSize * 2f),
            end = Offset(cx, cy + sparkleSize * 2f),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round
        )
        // 对角光
        val diagLen = sparkleSize * 1.2f
        drawLine(
            color = starColor.copy(alpha = starColor.alpha * 0.5f),
            start = Offset(cx - diagLen, cy - diagLen),
            end = Offset(cx + diagLen, cy + diagLen),
            strokeWidth = 0.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = starColor.copy(alpha = starColor.alpha * 0.5f),
            start = Offset(cx + diagLen, cy - diagLen),
            end = Offset(cx - diagLen, cy + diagLen),
            strokeWidth = 0.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        // 中心点
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = sparkleSize * 0.6f,
            center = Offset(cx, cy),
            blendMode = BlendMode.Plus
        )
    }
}

private fun DrawScope.drawGlassBorder(cornerRadius: Float, isWeather: Boolean) {
    val w = size.width
    val h = size.height
    val borderWidth = 1.2.dp.toPx()

    val borderColors = if (isWeather) {
        listOf(
            Color.White.copy(alpha = 0.9f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.4f)
        )
    } else {
        listOf(
            Color.White.copy(alpha = 0.8f),
            Color.White.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.25f)
        )
    }

    val borderBrush = Brush.verticalGradient(colors = borderColors)

    drawRoundRect(
        brush = borderBrush,
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = borderWidth)
    )

    val shadowAlpha = if (isWeather) 0.08f else 0.12f
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Black.copy(alpha = shadowAlpha),
                Color.Transparent
            ),
            center = Offset(w * 0.5f, h + 6.dp.toPx()),
            radius = w * 0.6f
        ),
        topLeft = Offset(-2.dp.toPx(), h * 0.5f),
        size = Size(w + 4.dp.toPx(), h * 0.5f + 8.dp.toPx()),
        cornerRadius = CornerRadius(cornerRadius + 2.dp.toPx())
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

    val infiniteTransition = rememberInfiniteTransition(label = "sparkle_small")
    val sparklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ss"
    )

    val sparklePos = remember { Random.nextFloat() to Random.nextFloat() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.18f),
                        Color.White.copy(alpha = 0.08f)
                    )
                )
            )
            .drawBehind {
                val sw = size.width
                val sh = size.height

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset.Zero,
                    size = Size(sw, 5.dp.toPx()),
                    cornerRadius = CornerRadius(crPx),
                    blendMode = BlendMode.Plus
                )

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset.Zero,
                    size = Size(3.dp.toPx(), sh),
                    cornerRadius = CornerRadius(crPx),
                    blendMode = BlendMode.Plus
                )

                val alpha = (kotlin.math.sin(sparklePhase * Math.PI * 2).toFloat() + 1f) / 2f
                val scx = sparklePos.first * sw
                val scy = sparklePos.second * sh
                val ss = 1.5.dp.toPx() + sparklePhase * 2.dp.toPx()

                drawLine(
                    color = Color.White.copy(alpha = alpha * 0.7f),
                    start = Offset(scx - ss * 2f, scy),
                    end = Offset(scx + ss * 2f, scy),
                    strokeWidth = 0.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White.copy(alpha = alpha * 0.7f),
                    start = Offset(scx, scy - ss * 2f),
                    end = Offset(scx, scy + ss * 2f),
                    strokeWidth = 0.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = Color.White.copy(alpha = alpha * 0.8f),
                    radius = ss * 0.5f,
                    center = Offset(scx, scy),
                    blendMode = BlendMode.Plus
                )

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f)
                        )
                    ),
                    topLeft = Offset(0f, sh - 6.dp.toPx()),
                    size = Size(sw, 6.dp.toPx()),
                    cornerRadius = CornerRadius(crPx)
                )
            }
            .then(
                Modifier.border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = shape
                )
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        content = content
    )
}
