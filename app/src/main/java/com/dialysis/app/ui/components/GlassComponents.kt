package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
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
    content: @Composable BoxScope.() -> Unit
) {
    val bgColor = if (isWeather) GlassWeatherBg else GlassWhite
    val borderColor = if (isWeather) GlassWeatherBorder else GlassBorder

    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart,
        content = content
    )
}

@Composable
fun GlassCardSmall(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = GlassWhite,
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.5.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        content = content
    )
}
