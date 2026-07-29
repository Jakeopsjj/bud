package com.dialysis.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.components.*
import com.dialysis.app.ui.theme.*

data class EmergencyContact(
    val name: String,
    val role: String,
    val avatarGradient: List<Color>,
    val iconType: ContactIcon
)

enum class ContactIcon {
    Phone, Person, Warning
}

@Composable
fun ContactsScreen(
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit
) {
    val contacts = listOf(
        EmergencyContact(
            name = "李主任",
            role = "主治医生",
            avatarGradient = listOf(AccentBlue.copy(alpha = 0.4f), Color(0x405856D6)),
            iconType = ContactIcon.Phone
        ),
        EmergencyContact(
            name = "王小明",
            role = "家属 · 儿子",
            avatarGradient = listOf(AccentOrange.copy(alpha = 0.4f), AccentRed.copy(alpha = 0.4f)),
            iconType = ContactIcon.Person
        ),
        EmergencyContact(
            name = "血透中心",
            role = "24小时值班",
            avatarGradient = listOf(Color(0x40FFCC00), AccentOrange.copy(alpha = 0.4f)),
            iconType = ContactIcon.Warning
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackground(theme = GlassTheme.Sos)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 92.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "紧急求助",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.55f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SOS button
            SosButton()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "紧急情况？长按按钮将自动呼叫120并通知紧急联系人",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Emergency contacts
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "紧急联系人",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                contacts.forEachIndexed { index, contact ->
                    ContactItem(contact = contact)
                    if (index < contacts.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        BottomTabBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            theme = GlassTheme.Sos,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SosButton() {
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulse rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * pulseScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentRed.copy(alpha = pulseAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }

        // Main button
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF453A),
                            Color(0xCCFF3B30),
                            Color(0x99CC0000)
                        ),
                        center = Offset(0.5f, 0.3f)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* SOS action */ },
            contentAlignment = Alignment.Center
        ) {
            // Inner glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height * 0.3f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.minDimension * 0.5f
                    ),
                    radius = size.minDimension * 0.5f,
                    center = center
                )
                // Border
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = size.minDimension / 2 - 1.dp.toPx(),
                    center = Offset(size.width / 2, size.height / 2),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SOS",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "一键求助",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ContactItem(contact: EmergencyContact) {
    LiquidGlassCard(
        theme = GlassTheme.Sos,
        hasSparkles = true,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(12.dp, 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(contact.avatarGradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (contact.iconType) {
                    ContactIcon.Phone -> PhoneAvatarIcon()
                    ContactIcon.Person -> PersonAvatarIcon()
                    ContactIcon.Warning -> WarningAvatarIcon()
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = contact.role,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            // Call button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AccentGreen.copy(alpha = 0.25f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Call action */ },
                contentAlignment = Alignment.Center
            ) {
                CallButtonIcon()
            }
        }
    }
}

@Composable
private fun PhoneAvatarIcon() {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.85f, h * 0.68f)
            lineTo(w * 0.68f, h * 0.68f)
            cubicTo(w * 0.63f, h * 0.68f, w * 0.58f, h * 0.70f, w * 0.54f, h * 0.75f)
            lineTo(w * 0.44f, h * 0.68f)
            cubicTo(w * 0.33f, h * 0.57f, w * 0.27f, h * 0.45f, w * 0.23f, h * 0.34f)
            lineTo(w * 0.33f, h * 0.25f)
            cubicTo(w * 0.40f, h * 0.18f, w * 0.40f, h * 0.08f, w * 0.35f, 0f)
            lineTo(w * 0.18f, 0f)
            cubicTo(w * 0.08f, -0.02f, 0f, h * 0.08f, 0f, h * 0.18f)
            cubicTo(0f, h * 0.60f, w * 0.40f, h, w * 0.85f, h * 0.85f)
            cubicTo(w * 0.95f, h * 0.82f, w, h * 0.75f, w, h * 0.68f)
            lineTo(w * 0.85f, h * 0.68f)
        }
        drawPath(path, Color.White)
    }
}

@Composable
private fun PersonAvatarIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width; val h = size.height
        drawCircle(
            color = Color.White,
            radius = w * 0.25f,
            center = Offset(w * 0.5f, h * 0.3f)
        )
        val bodyPath = Path().apply {
            moveTo(w * 0.1f, h * 0.95f)
            quadraticBezierTo(w * 0.1f, h * 0.55f, w * 0.5f, h * 0.55f)
            quadraticBezierTo(w * 0.9f, h * 0.55f, w * 0.9f, h * 0.95f)
        }
        drawPath(bodyPath, Color.White)
    }
}

@Composable
private fun WarningAvatarIcon() {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(0f, h)
            lineTo(w, h)
            close()
        }
        drawPath(path, Color.White)
        drawLine(
            color = AccentOrange,
            start = Offset(w * 0.5f, h * 0.35f),
            end = Offset(w * 0.5f, h * 0.65f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = AccentOrange,
            radius = 1.5.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.78f)
        )
    }
}

@Composable
private fun CallButtonIcon() {
    Canvas(modifier = Modifier.size(14.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.85f, h * 0.68f)
            lineTo(w * 0.68f, h * 0.68f)
            cubicTo(w * 0.63f, h * 0.68f, w * 0.58f, h * 0.70f, w * 0.54f, h * 0.75f)
            lineTo(w * 0.44f, h * 0.68f)
            cubicTo(w * 0.33f, h * 0.57f, w * 0.27f, h * 0.45f, w * 0.23f, h * 0.34f)
            lineTo(w * 0.33f, h * 0.25f)
            cubicTo(w * 0.40f, h * 0.18f, w * 0.40f, h * 0.08f, w * 0.35f, 0f)
            lineTo(w * 0.18f, 0f)
            cubicTo(w * 0.08f, -0.02f, 0f, h * 0.08f, 0f, h * 0.18f)
            cubicTo(0f, h * 0.60f, w * 0.40f, h, w * 0.85f, h * 0.85f)
            cubicTo(w * 0.95f, h * 0.82f, w, h * 0.75f, w, h * 0.68f)
            lineTo(w * 0.85f, h * 0.68f)
        }
        drawPath(path, AccentGreen, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
