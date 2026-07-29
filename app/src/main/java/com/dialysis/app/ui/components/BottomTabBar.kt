package com.dialysis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

enum class TabItem(val label: String) {
    Home("首页"),
    Records("记录"),
    Schedule("日程"),
    Contacts("联系")
}

@Composable
fun BottomTabBar(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.16f)
                    )
                )
            )
            .drawBehind {
                val w = size.width
                // Top border
                drawLine(
                    color = Color.White.copy(alpha = 0.28f),
                    start = Offset(0f, 0f),
                    end = Offset(w, 0f),
                    strokeWidth = 0.5.dp.toPx()
                )
                // Top highlight
                drawLine(
                    color = Color.White.copy(alpha = 0.30f),
                    start = Offset(w * 0.12f, 1.dp.toPx()),
                    end = Offset(w * 0.88f, 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                    blendMode = BlendMode.Plus
                )
            }
    ) {
        // Top highlight gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    startX = w * 0.12f,
                    endX = w * 0.88f
                ),
                start = Offset(w * 0.12f, 0f),
                end = Offset(w * 0.88f, 0f),
                strokeWidth = 1.dp.toPx()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            TabItem.values().forEach { tab ->
                TabBarItemView(
                    tab = tab,
                    isSelected = selectedTab == tab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun TabBarItemView(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        when (tab) {
            TabItem.Home -> HomeIcon(selected = isSelected)
            TabItem.Records -> RecordsIcon(selected = isSelected)
            TabItem.Schedule -> ScheduleIcon(selected = isSelected)
            TabItem.Contacts -> ContactsIcon(selected = isSelected)
        }
        Text(
            text = tab.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) AccentBlue else TabInactive
        )
    }
}

@Composable
private fun HomeIcon(selected: Boolean) {
    val color = if (selected) AccentBlue else TabInactive
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.13f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.87f, h * 0.48f)
            lineTo(w * 0.87f, h * 0.83f)
            lineTo(w * 0.62f, h * 0.83f)
            lineTo(w * 0.62f, h * 0.60f)
            lineTo(w * 0.38f, h * 0.60f)
            lineTo(w * 0.38f, h * 0.83f)
            lineTo(w * 0.13f, h * 0.83f)
            close()
        }
        if (selected) {
            drawPath(path, color)
        } else {
            drawPath(path, color, style = Stroke(width = 1.6.dp.toPx()))
        }
    }
}

@Composable
private fun RecordsIcon(selected: Boolean) {
    val color = if (selected) AccentBlue else TabInactive
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val sw = 1.7.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.17f, h * 0.20f)
            lineTo(w * 0.58f, h * 0.20f)
            lineTo(w * 0.83f, h * 0.45f)
            lineTo(w * 0.83f, h * 0.83f)
            lineTo(w * 0.17f, h * 0.83f)
            close()
        }
        drawPath(path, color, style = Stroke(width = sw))
        drawLine(color, Offset(w * 0.58f, h * 0.20f), Offset(w * 0.58f, h * 0.45f), strokeWidth = sw)
        drawLine(color, Offset(w * 0.58f, h * 0.45f), Offset(w * 0.83f, h * 0.45f), strokeWidth = sw)
        drawLine(color, Offset(w * 0.33f, h * 0.58f), Offset(w * 0.70f, h * 0.58f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.33f, h * 0.72f), Offset(w * 0.58f, h * 0.72f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
private fun ScheduleIcon(selected: Boolean) {
    val color = if (selected) AccentBlue else TabInactive
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val sw = 1.7.dp.toPx()
        val cornerPx = 3.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.13f, h * 0.22f),
            size = Size(w * 0.74f, h * 0.66f),
            cornerRadius = CornerRadius(cornerPx),
            style = Stroke(width = sw)
        )
        drawLine(color, Offset(w * 0.13f, h * 0.38f), Offset(w * 0.87f, h * 0.38f), strokeWidth = sw)
        drawLine(color, Offset(w * 0.32f, h * 0.13f), Offset(w * 0.32f, h * 0.33f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.68f, h * 0.13f), Offset(w * 0.68f, h * 0.33f), strokeWidth = sw, cap = StrokeCap.Round)
        val dotR = 1.5.dp.toPx()
        drawCircle(color, radius = dotR, center = Offset(w * 0.32f, h * 0.55f))
        drawCircle(color, radius = dotR, center = Offset(w * 0.50f, h * 0.55f))
        drawCircle(color, radius = dotR, center = Offset(w * 0.68f, h * 0.55f))
        drawCircle(color, radius = dotR, center = Offset(w * 0.32f, h * 0.72f))
        drawCircle(color, radius = dotR, center = Offset(w * 0.50f, h * 0.72f))
    }
}

@Composable
private fun ContactsIcon(selected: Boolean) {
    val color = if (selected) AccentBlue else TabInactive
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val sw = 1.7.dp.toPx()
        drawCircle(
            color = color,
            radius = w * 0.17f,
            center = Offset(w * 0.5f, h * 0.35f),
            style = Stroke(width = sw)
        )
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.88f)
            quadraticBezierTo(w * 0.18f, h * 0.58f, w * 0.5f, h * 0.58f)
            quadraticBezierTo(w * 0.82f, h * 0.58f, w * 0.82f, h * 0.88f)
        }
        drawPath(path, color, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}
