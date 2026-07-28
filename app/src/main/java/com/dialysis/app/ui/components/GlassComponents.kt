package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.*
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
import kotlin.math.PI
import kotlin.random.Random

private data class Sparkle(
    val nx: Float,
    val ny: Float,
    val sizeDp: Float,
    val periodSec: Float,
    val phase: Float,
    val maxAlpha: Float
)

private data class GlowHighlight(
    val nx: Float,
    val ny: Float,
    val radiusDp: Float,
    val periodSec: Float,
    val phase: Float,
    val maxAlpha: Float
)

private data class LightSource(
    val nx: Float,
    val ny: Float,
    val radiusScale: Float
)

private fun generateSparkles(count: Int, edgePadding: Float = 0.12f, rng: Random = Random.Default): List<Sparkle> {
    return List(count) {
        Sparkle(
            nx = edgePadding + rng.nextFloat() * (1f - 2 * edgePadding),
            ny = edgePadding + rng.nextFloat() * (1f - 2 * edgePadding),
            sizeDp = 1.2f + rng.nextFloat() * 2.0f,
            periodSec = 1.5f + rng.nextFloat() * 2.5f,
            phase = rng.nextFloat() * (2f * PI.toFloat()),
            maxAlpha = 0.4f + rng.nextFloat() * 0.5f
        )
    }
}

private fun generateHighlights(count: Int, edgePadding: Float = 0.2f, rng: Random = Random.Default): List<GlowHighlight> {
    return List(count) {
        GlowHighlight(
            nx = edgePadding + rng.nextFloat() * (1f - 2 * edgePadding),
            ny = edgePadding + rng.nextFloat() * (1f - 2 * edgePadding),
            radiusDp = 18f + rng.nextFloat() * 25f,
            periodSec = 2.5f + rng.nextFloat() * 3f,
            phase = rng.nextFloat() * (2f * PI.toFloat()),
            maxAlpha = 0.15f + rng.nextFloat() * 0.25f
        )
    }
}

private fun generateLightSource(rng: Random = Random.Default): LightSource {
    return LightSource(
        nx = 0.15f + rng.nextFloat() * 0.7f,
        ny = 0.10f + rng.nextFloat() * 0.5f,
        radiusScale = 0.9f + rng.nextFloat() * 0.4f
    )
}

private val GlassWhiteTop = Color(0xFFE6E6EB).copy(alpha = 0.13f)
private val GlassWhiteMid = Color(0xFFDADAE0).copy(alpha = 0.085f)
private val GlassWhiteBot = Color(0xFFC8C8D2).copy(alpha = 0.05f)

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

    val sparkles = remember { generateSparkles(if (isWeather) 8 else 6) }
    val highlights = remember { generateHighlights(if (isWeather) 3 else 2) }
    val lightSource = remember { generateLightSource() }

    val transition = rememberInfiniteTransition(label = "glass_anim")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )

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
                                GlassWhiteTop,
                                GlassWhiteMid,
                                GlassWhiteBot
                            )
                        )
                    )
                }
            )
            .drawBehind {
                drawGlassEffects(crPx, isWeather, sparkles, highlights, lightSource, time)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}

private fun DrawScope.drawGlassEffects(
    cornerRadius: Float,
    isWeather: Boolean,
    sparkles: List<Sparkle>,
    highlights: List<GlowHighlight>,
    light: LightSource,
    timeNorm: Float
) {
    val w = size.width
    val h = size.height
    val currentTime = timeNorm * 8f
    val dp = density
    val bw = 1.2f * dp

    val srcX = light.nx * w
    val srcY = light.ny * h
    val diagX = w - srcX
    val diagY = h - srcY
    val lightStart = Offset(srcX, srcY)
    val lightEnd = Offset(diagX, diagY)

    // === 1. 对角渐变边框：光源点亮白高光 → 对角微冷蓝折射 ===
    val borderBrush = Brush.linearGradient(
        colors = if (isWeather) {
            listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.55f),
                Color(0xFFB3E5FC).copy(alpha = 0.40f),
                Color(0xFF81D4FA).copy(alpha = 0.30f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.70f),
                Color.White.copy(alpha = 0.42f),
                Color(0xFFE1F5FE).copy(alpha = 0.24f),
                Color(0xFFB3E5FC).copy(alpha = 0.18f)
            )
        },
        start = lightStart,
        end = lightEnd
    )
    drawRoundRect(
        brush = borderBrush,
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = bw)
    )

    // === 2. 光源点附近强烈反光晕（模拟光打在玻璃边缘的反射） ===
    val srcGlowR = cornerRadius * 2.2f * light.radiusScale
    drawCircle(
        brush = Brush.radialGradient(
            colors = if (isWeather) {
                listOf(
                    Color.White.copy(alpha = 0.50f),
                    Color.White.copy(alpha = 0.22f),
                    Color.Transparent
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0.16f),
                    Color.Transparent
                )
            },
            center = lightStart,
            radius = srcGlowR
        ),
        center = lightStart,
        radius = srcGlowR,
        blendMode = BlendMode.Plus
    )

    // === 3. 顶部内边缘反光：沿顶部弧线一道亮白光（固定向上反射天空光） ===
    val topHighlightPath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    offset = Offset(bw * 2f, bw * 2f),
                    size = Size(w - bw * 4f, 2.2f * dp)
                ),
                cornerRadius = CornerRadius(cornerRadius - bw * 2f)
            )
        )
    }
    drawPath(
        path = topHighlightPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.0f),
                Color.White.copy(alpha = if (isWeather) 0.55f else 0.45f),
                Color.White.copy(alpha = if (isWeather) 0.55f else 0.45f),
                Color.White.copy(alpha = 0.0f)
            ),
            startX = cornerRadius,
            endX = w - cornerRadius
        )
    )

    // === 4. 内部对角光感叠加：光源方向亮，对角微暗（模拟玻璃厚度/光照体积） ===
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isWeather) 0.09f else 0.07f),
                Color.Transparent,
                Color.Black.copy(alpha = if (isWeather) 0.07f else 0.045f)
            ),
            start = lightStart,
            end = lightEnd
        ),
        topLeft = Offset(bw, bw),
        size = Size(w - bw * 2f, h - bw * 2f),
        cornerRadius = CornerRadius(cornerRadius - bw),
        blendMode = BlendMode.Softlight
    )

    // === 5. 光源方向大面积柔光（径向渐变，随机位置） ===
    val softR = maxOf(w, h) * 0.8f * light.radiusScale
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = if (isWeather) 0.16f else 0.12f),
                Color.White.copy(alpha = if (isWeather) 0.07f else 0.05f),
                Color.Transparent
            ),
            center = lightStart,
            radius = softR
        ),
        topLeft = Offset(bw, bw),
        size = Size(w - bw * 2f, h - bw * 2f),
        cornerRadius = CornerRadius(cornerRadius - bw),
        blendMode = BlendMode.Plus
    )

    // === 7. 随机高光闪烁（大光晕） ===
    for (hl in highlights) {
        val t = (currentTime / hl.periodSec) * 2f * PI.toFloat() + hl.phase
        val raw = sin(t)
        val pulse = if (raw > 0) raw * raw * raw * raw else 0f
        val alpha = pulse * hl.maxAlpha
        if (alpha > 0.01f) {
            val cx = hl.nx * w
            val cy = hl.ny * h
            val radius = hl.radiusDp * dp
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha),
                        Color.White.copy(alpha = alpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = radius
                ),
                center = Offset(cx, cy),
                radius = radius,
                blendMode = BlendMode.Plus
            )
        }
    }

    // === 8. 随机星星闪烁 ===
    for (s in sparkles) {
        val t = (currentTime / s.periodSec) * 2f * PI.toFloat() + s.phase
        val raw = sin(t)
        val pulse = if (raw > 0) raw * raw else 0f
        val alpha = pulse * s.maxAlpha
        if (alpha > 0.02f) {
            val cx = s.nx * w
            val cy = s.ny * h
            val r = s.sizeDp * dp
            val centerAlpha = (alpha * 0.9f).coerceAtMost(1f)
            drawCircle(
                color = Color.White.copy(alpha = centerAlpha),
                center = Offset(cx, cy),
                radius = r,
                blendMode = BlendMode.Plus
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha * 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = r * 3.5f
                ),
                center = Offset(cx, cy),
                radius = r * 3.5f,
                blendMode = BlendMode.Plus
            )
        }
    }
}

@Composable
fun GlassCardSmall(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val localDensity = LocalDensity.current
    val crPx = with(localDensity) { 20.dp.toPx() }

    val sparkles = remember { generateSparkles(4, edgePadding = 0.15f) }
    val highlights = remember { generateHighlights(1, edgePadding = 0.25f) }
    val lightSource = remember { generateLightSource() }

    val transition = rememberInfiniteTransition(label = "glass_s_anim")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ts"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassWhiteTop,
                        GlassWhiteMid,
                        GlassWhiteBot
                    )
                )
            )
            .drawBehind {
                val sw = size.width
                val sh = size.height
                val currentTime = time * 8f
                val dpScale = density
                val bws = 1f * dpScale

                val srcXs = lightSource.nx * sw
                val srcYs = lightSource.ny * sh
                val lightStartS = Offset(srcXs, srcYs)
                val lightEndS = Offset(sw - srcXs, sh - srcYs)

                // 对角渐变边框
                val borderBrushS = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.62f),
                        Color.White.copy(alpha = 0.38f),
                        Color(0xFFE1F5FE).copy(alpha = 0.20f),
                        Color(0xFFB3E5FC).copy(alpha = 0.15f)
                    ),
                    start = lightStartS,
                    end = lightEndS
                )
                drawRoundRect(
                    brush = borderBrushS,
                    topLeft = Offset.Zero,
                    size = Size(sw, sh),
                    cornerRadius = CornerRadius(crPx),
                    style = Stroke(width = bws)
                )

                // 光源点光晕
                val cgR = crPx * 2f * lightSource.radiusScale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.30f),
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = lightStartS,
                        radius = cgR
                    ),
                    center = lightStartS,
                    radius = cgR,
                    blendMode = BlendMode.Plus
                )

                // 顶部内高光
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0f),
                            Color.White.copy(alpha = 0.38f),
                            Color.White.copy(alpha = 0.38f),
                            Color.White.copy(alpha = 0f)
                        ),
                        startX = crPx,
                        endX = sw - crPx
                    ),
                    topLeft = Offset(bws * 2f, bws * 2f),
                    size = Size(sw - bws * 4f, 1.8f * dpScale),
                    cornerRadius = CornerRadius(crPx - bws * 2f)
                )

                // 内部对角光感
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.04f)
                        ),
                        start = lightStartS,
                        end = lightEndS
                    ),
                    topLeft = Offset(bws, bws),
                    size = Size(sw - bws * 2f, sh - bws * 2f),
                    cornerRadius = CornerRadius(crPx - bws),
                    blendMode = BlendMode.Softlight
                )

                // 大面积光源柔光（径向）
                val softRs = maxOf(sw, sh) * 0.8f * lightSource.radiusScale
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = lightStartS,
                        radius = softRs
                    ),
                    topLeft = Offset(bws, bws),
                    size = Size(sw - bws * 2f, sh - bws * 2f),
                    cornerRadius = CornerRadius(crPx - bws),
                    blendMode = BlendMode.Plus
                )

                for (hl in highlights) {
                    val t = (currentTime / hl.periodSec) * 2f * PI.toFloat() + hl.phase
                    val raw = sin(t)
                    val pulse = if (raw > 0) raw * raw * raw * raw else 0f
                    val alpha = pulse * hl.maxAlpha
                    if (alpha > 0.01f) {
                        val cx = hl.nx * sw
                        val cy = hl.ny * sh
                        val radius = hl.radiusDp * 0.7f * dpScale
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = alpha),
                                    Color.White.copy(alpha = alpha * 0.4f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = radius
                            ),
                            center = Offset(cx, cy),
                            radius = radius,
                            blendMode = BlendMode.Plus
                        )
                    }
                }

                for (s in sparkles) {
                    val t = (currentTime / s.periodSec) * 2f * PI.toFloat() + s.phase
                    val raw = sin(t)
                    val pulse = if (raw > 0) raw * raw else 0f
                    val alpha = pulse * s.maxAlpha
                    if (alpha > 0.02f) {
                        val cx = s.nx * sw
                        val cy = s.ny * sh
                        val r = s.sizeDp * 0.85f * dpScale
                        drawCircle(
                            color = Color.White.copy(alpha = (alpha * 0.9f).coerceAtMost(1f)),
                            center = Offset(cx, cy),
                            radius = r,
                            blendMode = BlendMode.Plus
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = alpha * 0.5f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = r * 3f
                            ),
                            center = Offset(cx, cy),
                            radius = r * 3f,
                            blendMode = BlendMode.Plus
                        )
                    }
                }
            }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        content = content
    )
}
