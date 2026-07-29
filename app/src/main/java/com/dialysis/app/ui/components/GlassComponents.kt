package com.dialysis.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dialysis.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SkyBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    SkyTop,
                    SkyMid,
                    SkyHorizon
                ),
                startY = 0f,
                endY = size.height
            ),
            size = size
        )
        // Soft cloud-like blobs scattered across sky
        drawCloud(this, Offset(size.width * 0.2f, size.height * 0.08f), 80f, 0.18f)
        drawCloud(this, Offset(size.width * 0.78f, size.height * 0.12f), 100f, 0.14f)
        drawCloud(this, Offset(size.width * 0.35f, size.height * 0.22f), 60f, 0.12f)
        drawCloud(this, Offset(size.width * 0.88f, size.height * 0.3f), 90f, 0.10f)
        drawCloud(this, Offset(size.width * 0.15f, size.height * 0.42f), 70f, 0.10f)
        drawCloud(this, Offset(size.width * 0.6f, size.height * 0.5f), 110f, 0.08f)
        drawCloud(this, Offset(size.width * 0.85f, size.height * 0.6f), 75f, 0.12f)
        drawCloud(this, Offset(size.width * 0.3f, size.height * 0.7f), 90f, 0.10f)
        drawCloud(this, Offset(size.width * 0.7f, size.height * 0.82f), 80f, 0.15f)
        drawCloud(this, Offset(size.width * 0.1f, size.height * 0.88f), 65f, 0.12f)
        // Sun glow in upper area
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x40FFF0B0),
                    Color(0x20FFE080),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.8f, size.height * 0.08f),
                radius = size.width * 0.35f
            ),
            radius = size.width * 0.35f,
            center = Offset(size.width * 0.8f, size.height * 0.08f)
        )
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

/**
 * Liquid Glass card matching the HTML .glass / .glass-sunny design.
 * - Multi-layer gradient (top specular, warm refraction, cool refraction, body)
 * - 0.5px border with gradient
 * - Inner top highlight (bevel)
 * - Drop shadows
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    isSunny: Boolean = false,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    hasSparkles: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = 0.98f
            }
            .clip(shape)
            .then(
                if (isSunny) {
                    Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    Color.White.copy(alpha = 0.10f),
                                    Color.White.copy(alpha = 0.08f),
                                    Color.White.copy(alpha = 0.18f)
                                )
                            )
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x14FFDC8C),
                                    Color.Transparent
                                ),
                                endY = with(density) { 60.dp.toPx() }
                            )
                        )
                } else {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.20f),
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.06f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    )
                }
            )
            .drawBehind {
                drawGlassEffects(
                    cornerRadius = with(density) { cornerRadius.toPx() },
                    isSunny = isSunny
                )
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Top glossy dome highlight (::after pseudo-element)
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.10f),
                        Color.White.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, 0f),
                    radius = h * 0.5f
                ),
                topLeft = Offset(w * 0.05f, -h * 0.05f),
                size = Size(w * 0.9f, h * 0.5f),
                cornerRadius = CornerRadius(with(density) { cornerRadius.toPx() })
            )
        }

        if (hasSparkles) {
            Sparkles()
        }

        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}

private fun DrawScope.drawGlassEffects(cornerRadius: Float, isSunny: Boolean) {
    val w = size.width
    val h = size.height
    val borderW = 0.5.dp.toPx()

    // Outer shadow (multiple layers for volume)
    // We use drawShadow would require a path; approximate with inset rectangles
    // Outer shadow
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.04f),
                Color.Black.copy(alpha = 0.10f)
            )
        ),
        topLeft = Offset(0f, h * 0.3f),
        size = Size(w, h * 0.7f),
        cornerRadius = CornerRadius(cornerRadius),
        blendMode = BlendMode.Multiply
    )

    // Border - thin bright white with gradient
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isSunny) 0.50f else 0.42f),
            Color.White.copy(alpha = if (isSunny) 0.35f else 0.28f),
            Color.White.copy(alpha = if (isSunny) 0.20f else 0.15f),
            Color.White.copy(alpha = if (isSunny) 0.30f else 0.22f)
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, h)
    )
    drawRoundRect(
        brush = borderBrush,
        topLeft = Offset.Zero,
        size = Size(w, h),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = borderW),
        blendMode = BlendMode.Plus
    )

    // Inner top bevel highlight
    val hlH = 1.5.dp.toPx()
    val inset = borderW * 2f
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = if (isSunny) 0.55f else 0.45f),
                Color.White.copy(alpha = if (isSunny) 0.55f else 0.45f),
                Color.Transparent
            ),
            startX = cornerRadius,
            endX = w - cornerRadius
        ),
        topLeft = Offset(inset, inset),
        size = Size(w - inset * 2f, hlH),
        cornerRadius = CornerRadius((cornerRadius - inset).coerceAtLeast(0f)),
        blendMode = BlendMode.Plus
    )
}

@Composable
private fun Sparkles() {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")
    val scales = listOf(
        infiniteTransition.animateFloat(
            initialValue = 0.3f, targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3000
                    0.3f at 0
                    0.3f at 1200
                    1.2f at 1650
                    0.8f at 2100
                    0.3f at 2550
                    0.3f at 3000
                },
                repeatMode = RepeatMode.Restart
            ), label = "s1"
        ),
        infiniteTransition.animateFloat(
            initialValue = 0.3f, targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 4000
                    0.3f at 0
                    0.3f at 480
                    1.2f at 1080
                    0.8f at 1680
                    0.3f at 2320
                    0.3f at 4000
                },
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(1200)
            ), label = "s2"
        ),
        infiniteTransition.animateFloat(
            initialValue = 0.3f, targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3500
                    0.3f at 0
                    0.3f at 840
                    1.2f at 1540
                    0.8f at 2100
                    0.3f at 2975
                    0.3f at 3500
                },
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(2400)
            ), label = "s3"
        )
    )
    val positions = listOf(
        Offset(18f, 8f),
        Offset(-22f, 12f),
        Offset(28f, -16f)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        scales.forEachIndexed { i, scale ->
            val (xOff, yOff) = positions[i]
            val cx = if (i == 1) size.width + xOff else xOff
            val cy = if (i == 2) size.height + yOff else yOff
            val s = scale.value
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f * (s / 1.2f)),
                        Color.White.copy(alpha = 0.3f * (s / 1.2f)),
                        Color.Transparent
                    )
                ),
                radius = 3.dp.toPx() * s,
                center = Offset(cx, cy),
                blendMode = BlendMode.Plus
            )
        }
    }
}

/** Small vital card glass */
@Composable
fun VitalGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val density = LocalDensity.current
    val cr = 20.dp

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.10f),
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.18f)
                    )
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x14FFDC8C),
                        Color.Transparent
                    ),
                    endY = with(density) { 50.dp.toPx() }
                )
            )
            .drawBehind {
                val w = size.width
                val h = size.height
                val crPx = with(density) { cr.toPx() }
                val bw = 0.5.dp.toPx()
                val borderBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.42f),
                        Color.White.copy(alpha = 0.28f),
                        Color.White.copy(alpha = 0.18f),
                        Color.White.copy(alpha = 0.30f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, h)
                )
                drawRoundRect(
                    brush = borderBrush,
                    size = Size(w, h),
                    cornerRadius = CornerRadius(crPx),
                    style = Stroke(width = bw),
                    blendMode = BlendMode.Plus
                )
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.40f),
                            Color.White.copy(alpha = 0.40f),
                            Color.Transparent
                        ),
                        startX = crPx,
                        endX = w - crPx
                    ),
                    topLeft = Offset(bw * 2f, bw * 2f),
                    size = Size(w - bw * 4f, 1.2.dp.toPx()),
                    cornerRadius = CornerRadius((crPx - bw * 2f).coerceAtLeast(0f)),
                    blendMode = BlendMode.Plus
                )
            }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        content = content
    )
}
