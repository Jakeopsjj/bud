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

enum class GlassTheme { Sunny, Cloudy, Night, Sos }

@Composable
fun SkyBackground(
    modifier: Modifier = Modifier,
    theme: GlassTheme = GlassTheme.Sunny
) {
    val colors = when (theme) {
        GlassTheme.Sunny -> listOf(SkyTop, SkyMid, SkyHorizon)
        GlassTheme.Cloudy -> listOf(CloudyTop, CloudyMid, CloudyHorizon)
        GlassTheme.Night -> listOf(NightTop, NightMid, NightHorizon)
        GlassTheme.Sos -> listOf(SosTop, SosMid, SosHorizon)
    }
    val cloudColor = when (theme) {
        GlassTheme.Night -> CloudColorNight
        GlassTheme.Sos -> Color(0x10FF0000)
        else -> CloudColor
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(colors = colors),
            size = size
        )
        // Cloud blobs
        val cloudAlpha = if (theme == GlassTheme.Night) 0.08f
            else if (theme == GlassTheme.Sos) 0.05f
            else if (theme == GlassTheme.Cloudy) 0.22f
            else 0.15f
        drawCloud(this, Offset(size.width * 0.2f, size.height * 0.08f), 80f, cloudAlpha, cloudColor)
        drawCloud(this, Offset(size.width * 0.78f, size.height * 0.12f), 100f, cloudAlpha * 0.8f, cloudColor)
        drawCloud(this, Offset(size.width * 0.35f, size.height * 0.22f), 60f, cloudAlpha * 0.7f, cloudColor)
        drawCloud(this, Offset(size.width * 0.88f, size.height * 0.3f), 90f, cloudAlpha * 0.6f, cloudColor)
        drawCloud(this, Offset(size.width * 0.15f, size.height * 0.42f), 70f, cloudAlpha * 0.6f, cloudColor)
        drawCloud(this, Offset(size.width * 0.6f, size.height * 0.5f), 110f, cloudAlpha * 0.5f, cloudColor)
        drawCloud(this, Offset(size.width * 0.85f, size.height * 0.6f), 75f, cloudAlpha * 0.7f, cloudColor)
        drawCloud(this, Offset(size.width * 0.3f, size.height * 0.7f), 90f, cloudAlpha * 0.6f, cloudColor)
        drawCloud(this, Offset(size.width * 0.7f, size.height * 0.82f), 80f, cloudAlpha * 0.8f, cloudColor)
        drawCloud(this, Offset(size.width * 0.1f, size.height * 0.88f), 65f, cloudAlpha * 0.7f, cloudColor)

        // Sun/moon glow
        if (theme == GlassTheme.Sunny) {
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
        } else if (theme == GlassTheme.Night) {
            // Moon glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x20B0C0FF),
                        Color(0x108090DD),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.15f),
                    radius = size.width * 0.25f
                ),
                radius = size.width * 0.25f,
                center = Offset(size.width * 0.8f, size.height * 0.15f)
            )
            // Stars
            listOf(
                Offset(size.width * 0.15f, size.height * 0.1f),
                Offset(size.width * 0.4f, size.height * 0.06f),
                Offset(size.width * 0.6f, size.height * 0.2f),
                Offset(size.width * 0.25f, size.height * 0.28f),
                Offset(size.width * 0.9f, size.height * 0.35f),
                Offset(size.width * 0.1f, size.height * 0.45f),
                Offset(size.width * 0.5f, size.height * 0.5f)
            ).forEach {
                drawCircle(Color.White.copy(alpha = 0.45f), radius = 1.5f, center = it)
            }
        } else if (theme == GlassTheme.Sos) {
            // Red ambient glow at top
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x30FF3030),
                        Color(0x10FF0000),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.0f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.5f, size.height * 0.0f)
            )
        }
    }
}

private fun drawCloud(scope: DrawScope, center: Offset, radius: Float, alpha: Float, colorOverride: Color? = null) {
    val cloudColor = colorOverride ?: Color.White.copy(alpha = alpha)
    val fill = if (colorOverride != null) colorOverride else cloudColor
    val path = Path()
    val r = radius
    path.addOval(Rect(center.x - r, center.y - r * 0.4f, center.x + r, center.y + r * 0.5f))
    path.addOval(Rect(center.x - r * 1.3f, center.y - r * 0.2f, center.x - r * 0.3f, center.y + r * 0.4f))
    path.addOval(Rect(center.x + r * 0.2f, center.y - r * 0.3f, center.x + r * 1.2f, center.y + r * 0.3f))
    path.addOval(Rect(center.x - r * 0.5f, center.y - r * 0.6f, center.x + r * 0.5f, center.y + r * 0.2f))
    scope.drawPath(path, fill)
}

/**
 * Liquid Glass card supporting multiple themes.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    theme: GlassTheme = GlassTheme.Sunny,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    hasSparkles: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }

    // Gradient colors per theme
    val bgGradient = when (theme) {
        GlassTheme.Sunny -> listOf(
            Color.White.copy(alpha = 0.22f),
            Color.White.copy(alpha = 0.10f),
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.18f)
        )
        GlassTheme.Cloudy -> listOf(
            Color.White.copy(alpha = 0.24f),
            Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.20f)
        )
        GlassTheme.Night -> listOf(
            Color.White.copy(alpha = 0.13f),
            Color.White.copy(alpha = 0.06f),
            Color.White.copy(alpha = 0.04f),
            Color.White.copy(alpha = 0.12f)
        )
        GlassTheme.Sos -> listOf(
            Color(0x33FF4040).copy(alpha = 0.18f),
            Color(0x22FF2020).copy(alpha = 0.08f),
            Color(0x11FF0000).copy(alpha = 0.05f),
            Color(0x33FF3030).copy(alpha = 0.15f)
        )
    }

    val warmOverlay = when (theme) {
        GlassTheme.Sunny -> Color(0x14FFDC8C)
        GlassTheme.Cloudy -> Color(0x10FFF0C0)
        GlassTheme.Night -> Color(0x08FFB060)
        GlassTheme.Sos -> Color(0x20FF4040)
    }

    val borderAlpha = when (theme) {
        GlassTheme.Sunny -> floatArrayOf(0.50f, 0.35f, 0.20f, 0.30f)
        GlassTheme.Cloudy -> floatArrayOf(0.48f, 0.32f, 0.18f, 0.28f)
        GlassTheme.Night -> floatArrayOf(0.28f, 0.18f, 0.10f, 0.18f)
        GlassTheme.Sos -> floatArrayOf(0.40f, 0.25f, 0.15f, 0.28f)
    }

    val bevelAlpha = when (theme) {
        GlassTheme.Sunny -> 0.55f
        GlassTheme.Cloudy -> 0.50f
        GlassTheme.Night -> 0.30f
        GlassTheme.Sos -> 0.35f
    }

    Box(
        modifier = modifier
            .graphicsLayer { alpha = 0.98f }
            .clip(shape)
            .background(brush = Brush.verticalGradient(colors = bgGradient))
            .then(
                if (theme != GlassTheme.Sos) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(warmOverlay, Color.Transparent),
                            endY = with(density) { 60.dp.toPx() }
                        )
                    )
                } else Modifier
            )
            .drawBehind {
                drawGlassEffects(
                    cornerRadius = with(density) { cornerRadius.toPx() },
                    borderAlpha = borderAlpha,
                    bevelAlpha = bevelAlpha,
                    theme = theme
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
        // Top glossy dome highlight
        Canvas(modifier = Modifier.fillMaxSize()) {
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

private fun DrawScope.drawGlassEffects(
    cornerRadius: Float,
    borderAlpha: FloatArray,
    bevelAlpha: Float,
    theme: GlassTheme
) {
    val w = size.width
    val h = size.height
    val borderW = 0.5.dp.toPx()

    // Bottom shadow
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = if (theme == GlassTheme.Night) 0.18f else 0.04f),
                Color.Black.copy(alpha = if (theme == GlassTheme.Night) 0.25f else 0.10f)
            )
        ),
        topLeft = Offset(0f, h * 0.3f),
        size = Size(w, h * 0.7f),
        cornerRadius = CornerRadius(cornerRadius),
        blendMode = BlendMode.Multiply
    )

    val borderColor = when (theme) {
        GlassTheme.Sos -> Color(0xFFFF8080)
        else -> Color.White
    }
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            borderColor.copy(alpha = borderAlpha[0]),
            borderColor.copy(alpha = borderAlpha[1]),
            borderColor.copy(alpha = borderAlpha[2]),
            borderColor.copy(alpha = borderAlpha[3])
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

    val hlH = 1.5.dp.toPx()
    val inset = borderW * 2f
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = bevelAlpha),
                Color.White.copy(alpha = bevelAlpha),
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
    theme: GlassTheme = GlassTheme.Sunny,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val density = LocalDensity.current
    val cr = 20.dp

    val bg = when (theme) {
        GlassTheme.Sunny -> listOf(
            Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.10f),
            Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.18f)
        )
        GlassTheme.Cloudy -> listOf(
            Color.White.copy(alpha = 0.26f), Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.20f)
        )
        GlassTheme.Night -> listOf(
            Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.05f),
            Color.White.copy(alpha = 0.03f), Color.White.copy(alpha = 0.10f)
        )
        GlassTheme.Sos -> listOf(
            Color(0x30FF3030), Color(0x18FF2020), Color(0x10FF0000), Color(0x28FF2828)
        )
    }
    val borderAlphas = when (theme) {
        GlassTheme.Sunny -> floatArrayOf(0.42f, 0.28f, 0.18f, 0.30f)
        GlassTheme.Cloudy -> floatArrayOf(0.42f, 0.28f, 0.18f, 0.30f)
        GlassTheme.Night -> floatArrayOf(0.22f, 0.14f, 0.08f, 0.16f)
        GlassTheme.Sos -> floatArrayOf(0.35f, 0.22f, 0.12f, 0.22f)
    }
    val bevelA = when (theme) {
        GlassTheme.Night -> 0.28f
        GlassTheme.Sos -> 0.25f
        else -> 0.40f
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = Brush.verticalGradient(colors = bg))
            .then(
                if (theme != GlassTheme.Sos) Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x10FFDC8C), Color.Transparent),
                        endY = with(density) { 50.dp.toPx() }
                    )
                ) else Modifier
            )
            .drawBehind {
                val w = size.width
                val h = size.height
                val crPx = with(density) { cr.toPx() }
                val bw = 0.5.dp.toPx()
                val borderColor = if (theme == GlassTheme.Sos) Color(0xFFFF8080) else Color.White
                val borderBrush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = borderAlphas[0]),
                        borderColor.copy(alpha = borderAlphas[1]),
                        borderColor.copy(alpha = borderAlphas[2]),
                        borderColor.copy(alpha = borderAlphas[3])
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
                            Color.White.copy(alpha = bevelA),
                            Color.White.copy(alpha = bevelA),
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
