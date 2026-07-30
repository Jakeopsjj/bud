package com.dialysis.app.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dialysis.app.ui.theme.*

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val iconType: OnboardingIcon
)

enum class OnboardingIcon {
    Welcome, Vitals, Medication, Emergency, Permissions
}

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "欢迎使用透析伴侣",
                subtitle = "您的贴心透析管理助手",
                description = "我们将帮助您记录体征数据、管理用药、提醒透析时间，并在紧急时刻快速联系医生和家人。",
                iconType = OnboardingIcon.Welcome
            ),
            OnboardingPage(
                title = "体征数据一目了然",
                subtitle = "血压 · 体重 · 饮水 · 心率",
                description = "自动记录每日体征变化，生成趋势图表，让您和医生随时了解身体状况。",
                iconType = OnboardingIcon.Vitals
            ),
            OnboardingPage(
                title = "用药提醒不漏服",
                subtitle = "透析前准备清单",
                description = "设定用药时间，按时提醒服药。透析前自动生成准备清单，证件、药品、器材一样不落下。",
                iconType = OnboardingIcon.Medication
            ),
            OnboardingPage(
                title = "紧急求助一键直达",
                subtitle = "SOS紧急呼叫",
                description = "遇紧急情况一键拨打120，快速联系主治医生和家属，为生命争分夺秒。",
                iconType = OnboardingIcon.Emergency
            ),
            OnboardingPage(
                title = "开启必要权限",
                subtitle = "为正常使用应用功能",
                description = "请允许以下权限，确保电话呼叫和用药提醒功能正常工作。您也可以稍后在设置中修改。",
                iconType = OnboardingIcon.Permissions
            )
        )
    }

    var currentPage by remember { mutableStateOf(0) }

    // Permission states
    var callPhoneGranted by remember { mutableStateOf(false) }
    var notificationsGranted by remember { mutableStateOf(false) }

    // Permission launchers
    val callPhoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> callPhoneGranted = granted }

    val notificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> notificationsGranted = granted }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyTop, SkyMid, SkyHorizon)
                )
            )
    ) {
        // Decorative clouds
        CloudDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // Page indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .then(
                                if (index == currentPage)
                                    Modifier.width(24.dp)
                                else
                                    Modifier.width(6.dp)
                            )
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            // Page content
            key(currentPage) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) +
                            slideInVertically(animationSpec = tween(500)) { it / 3 }
                ) {
                    OnboardingPageContent(
                        page = pages[currentPage],
                        callPhoneGranted = callPhoneGranted,
                        notificationsGranted = notificationsGranted,
                        onRequestCallPhone = {
                            callPhoneLauncher.launch(Manifest.permission.CALL_PHONE)
                        },
                        onRequestNotifications = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                notificationsGranted = true
                            }
                        },
                        onOpenSettings = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            )
                            settingsLauncher.launch(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip button
                if (currentPage < pages.size - 1) {
                    TextButton(onClick = {
                        currentPage = pages.size - 1
                    }) {
                        Text(
                            text = "跳过",
                            fontSize = 16.sp,
                            color = TextWhiteSecondary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(64.dp))
                }

                // Next/Get Started button
                GlassButton(
                    text = if (currentPage < pages.size - 1) "下一步" else "开始使用",
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onComplete()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    callPhoneGranted: Boolean,
    notificationsGranted: Boolean,
    onRequestCallPhone: () -> Unit,
    onRequestNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        OnboardingIcon(type = page.iconType)

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = page.subtitle,
            fontSize = 15.sp,
            color = TextWhiteSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 14.sp,
            color = TextWhiteTertiary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Permission items (only on last page)
        if (page.iconType == OnboardingIcon.Permissions) {
            Spacer(modifier = Modifier.height(24.dp))
            PermissionItem(
                icon = { PhoneIcon() },
                title = "电话权限",
                description = "用于拨打紧急联系电话和120急救",
                granted = callPhoneGranted,
                onRequest = onRequestCallPhone,
                onOpenSettings = onOpenSettings
            )
            Spacer(modifier = Modifier.height(10.dp))
            PermissionItem(
                icon = { BellIcon() },
                title = "通知权限",
                description = "用于发送用药提醒和透析时间提醒",
                granted = notificationsGranted,
                onRequest = onRequestNotifications,
                onOpenSettings = onOpenSettings
            )
        }
    }
}

@Composable
private fun PermissionItem(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    granted: Boolean,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = TextWhiteTertiary
                    )
                }
            }

            // Status / Action button
            if (granted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.15f, size.height * 0.5f)
                            lineTo(size.width * 0.4f, size.height * 0.75f)
                            lineTo(size.width * 0.85f, size.height * 0.25f)
                        }
                        drawPath(path, AccentGreen, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                    }
                    Text(
                        text = "已允许",
                        fontSize = 12.sp,
                        color = AccentGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AccentBlue, AccentBlueLight)
                            )
                        )
                        .clickable { onRequest() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "去开启",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassSurface(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassSunnyTop1,
                        GlassSunnyBody,
                        GlassSunnyBottom
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        content = content
    )
}

@Composable
private fun GlassButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.85f))
                )
            )
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkyTop
        )
    }
}

// ===== Onboarding Icons =====

@Composable
private fun OnboardingIcon(type: OnboardingIcon) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        GlassWhite.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GlassSunnyTop1,
                            GlassSunnyBody
                        )
                    )
                )
                .border(
                    width = 0.5.dp,
                    color = GlassBorder,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when (type) {
                OnboardingIcon.Welcome -> WelcomeIcon()
                OnboardingIcon.Vitals -> VitalsIcon()
                OnboardingIcon.Medication -> MedicationIcon()
                OnboardingIcon.Emergency -> EmergencyIcon()
                OnboardingIcon.Permissions -> ShieldIcon()
            }
        }
    }
}

@Composable
private fun WelcomeIcon() {
    Canvas(modifier = Modifier.size(40.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        // Heart
        val path = Path().apply {
            moveTo(cx, cy + size.height * 0.28f)
            cubicTo(cx - size.width * 0.45f, cy, cx - size.width * 0.3f, cy - size.height * 0.3f, cx, cy - size.height * 0.05f)
            cubicTo(cx + size.width * 0.3f, cy - size.height * 0.3f, cx + size.width * 0.45f, cy, cx, cy + size.height * 0.28f)
            close()
        }
        drawPath(path, AccentRedSoft)
        // Plus sign
        drawLine(Color.White, Offset(cx - 6.dp.toPx(), cy + size.height * 0.05f), Offset(cx + 6.dp.toPx(), cy + size.height * 0.05f), strokeWidth = 2.5f, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(cx, cy + size.height * 0.05f - 6.dp.toPx()), Offset(cx, cy + size.height * 0.05f + 6.dp.toPx()), strokeWidth = 2.5f, cap = StrokeCap.Round)
    }
}

@Composable
private fun VitalsIcon() {
    Canvas(modifier = Modifier.size(40.dp)) {
        val midY = size.height * 0.5f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(size.width * 0.15f, midY)
            lineTo(size.width * 0.25f, midY - size.height * 0.35f)
            lineTo(size.width * 0.38f, midY + size.height * 0.35f)
            lineTo(size.width * 0.5f, midY - size.height * 0.2f)
            lineTo(size.width * 0.6f, midY + size.height * 0.1f)
            lineTo(size.width * 0.68f, midY)
            lineTo(size.width, midY)
        }
        drawPath(path, AccentBlue, style = Stroke(width = 3f, cap = StrokeCap.Round))
    }
}

@Composable
private fun MedicationIcon() {
    Canvas(modifier = Modifier.size(40.dp)) {
        val w = size.width
        val h = size.height
        val pillW = w * 0.55f
        val pillH = h * 0.25f
        val cx = w / 2
        val cy = h / 2
        val left = cx - pillW / 2
        val top = cy - pillH / 2
        // Pill shape
        drawRoundRect(
            color = AccentGreen,
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(pillW, pillH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(pillH / 2, pillH / 2)
        )
        // Divider line
        drawLine(Color.White.copy(alpha = 0.5f), Offset(cx, top), Offset(cx, top + pillH), strokeWidth = 1.5f)
    }
}

@Composable
private fun EmergencyIcon() {
    Canvas(modifier = Modifier.size(40.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val cy = h / 2
        val r = w * 0.32f
        // Circle
        drawCircle(AccentRed, r, Offset(cx, cy))
        // SOS cross (plus)
        val crossW = r * 0.7f
        val crossH = r * 0.2f
        drawRoundRect(Color.White, Offset(cx - crossW/2, cy - crossH/2), androidx.compose.ui.geometry.Size(crossW, crossH), cornerRadius = androidx.compose.ui.geometry.CornerRadius(crossH/2))
        drawRoundRect(Color.White, Offset(cx - crossH/2, cy - crossW/2), androidx.compose.ui.geometry.Size(crossH, crossW), cornerRadius = androidx.compose.ui.geometry.CornerRadius(crossH/2))
    }
}

@Composable
private fun ShieldIcon() {
    Canvas(modifier = Modifier.size(40.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val path = Path().apply {
            moveTo(cx, h * 0.1f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.55f)
            cubicTo(w * 0.85f, h * 0.75f, cx, h * 0.9f, cx, h * 0.9f)
            cubicTo(cx, h * 0.9f, w * 0.15f, h * 0.75f, w * 0.15f, h * 0.55f)
            lineTo(w * 0.15f, h * 0.25f)
            close()
        }
        drawPath(path, AccentBlue)
        // Checkmark
        val checkPath = Path().apply {
            moveTo(cx - w * 0.15f, h * 0.5f)
            lineTo(cx - w * 0.02f, h * 0.62f)
            lineTo(cx + w * 0.18f, h * 0.38f)
        }
        drawPath(checkPath, Color.White, style = Stroke(width = 3f, cap = StrokeCap.Round))
    }
}

@Composable
private fun PhoneIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.2f)
            cubicTo(w * 0.2f, h * 0.1f, w * 0.3f, h * 0.1f, w * 0.4f, h * 0.15f)
            lineTo(w * 0.5f, h * 0.35f)
            cubicTo(w * 0.52f, h * 0.4f, w * 0.5f, h * 0.48f, w * 0.45f, h * 0.52f)
            lineTo(w * 0.48f, h * 0.58f)
            cubicTo(w * 0.6f, h * 0.7f, w * 0.75f, h * 0.75f, w * 0.85f, h * 0.6f)
            lineTo(w * 0.95f, h * 0.68f)
            cubicTo(w, h * 0.75f, w * 0.98f, h * 0.88f, w * 0.85f, h * 0.9f)
            cubicTo(w * 0.35f, h * 0.95f, w * 0.05f, h * 0.5f, w * 0.12f, h * 0.28f)
            close()
        }
        drawPath(path, AccentGreen)
    }
}

@Composable
private fun BellIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        // Bell body
        val path = Path().apply {
            moveTo(cx, h * 0.1f)
            cubicTo(cx - w * 0.3f, h * 0.1f, cx - w * 0.32f, h * 0.45f, cx - w * 0.35f, h * 0.55f)
            lineTo(cx - w * 0.35f, h * 0.65f)
            lineTo(cx + w * 0.35f, h * 0.65f)
            lineTo(cx + w * 0.35f, h * 0.55f)
            cubicTo(cx + w * 0.32f, h * 0.45f, cx + w * 0.3f, h * 0.1f, cx, h * 0.1f)
            close()
        }
        drawPath(path, AccentOrange)
        // Bell bottom
        drawRect(Color.White.copy(alpha = 0f))
        drawRoundRect(
            AccentOrange,
            Offset(cx - w * 0.06f, h * 0.65f),
            androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.15f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.06f)
        )
        // Ringer dot
        drawCircle(AccentRed, w * 0.06f, Offset(cx + w * 0.2f, h * 0.2f))
    }
}

// ===== Cloud Decoration =====

@Composable
private fun CloudDecoration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = w * 0.35f,
            center = Offset(w * 0.2f, h * 0.15f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = w * 0.25f,
            center = Offset(w * 0.85f, h * 0.25f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = w * 0.4f,
            center = Offset(w * 0.5f, h * 0.8f)
        )
    }
}
