package com.dialysis.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.dialysis.app.data.DialysisCenter
import com.dialysis.app.data.ReminderManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToInt

private val BgDark = Color(0xFF0D1117)
private val CardGlass = Color(0xE6161B22)
private val CardStroke = Color(0x33FFFFFF)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentBlueLight = Color(0xFF60A5FA)
private val AccentRed = Color(0xFFEF4444)
private val AccentRedDark = Color(0xFFDC2626)
private val AccentGreen = Color(0xFF10B981)
private val TextWhite = Color(0xFFF8FAFC)
private val TextWhiteSecondary = Color(0x99FFFFFF)
private val TextMuted = Color(0x66FFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    initialCenterId: String? = null
) {
    val context = LocalContext.current
    val centers = remember { ReminderManager.getDefaultDialysisCenters() }
    val initialCenter = remember(initialCenterId) {
        centers.find { it.id == initialCenterId } ?: centers.first()
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var selectedCenter by remember { mutableStateOf<DialysisCenter?>(initialCenter) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    val markers = remember { mutableMapOf<String, Marker>() }

    val defaultMarkerIcon = remember { createHospitalMarkerIcon(context, isSelected = false) }
    val selectedMarkerIcon = remember { createHospitalMarkerIcon(context, isSelected = true) }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    val mapView = this
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)
                    controller.setZoom(14.5)
                    val startPoint = GeoPoint(initialCenter.lat, initialCenter.lng)
                    controller.setCenter(startPoint)

                    centers.forEach { center ->
                        val isInit = center.id == initialCenterId
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(center.lat, center.lng)
                            title = center.name
                            snippet = center.address
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            infoWindow = null
                            icon = if (isInit) selectedMarkerIcon else defaultMarkerIcon
                            setOnMarkerClickListener { _, _ ->
                                selectedCenter = center
                                markers.forEach { (id, m) ->
                                    m.icon = if (id == center.id) selectedMarkerIcon else defaultMarkerIcon
                                }
                                mapView.invalidate()
                                true
                            }
                        }
                        markers[center.id] = marker
                        overlays.add(marker)
                    }

                    if (hasLocationPermission) {
                        try {
                            val locationProvider = GpsMyLocationProvider(ctx)
                            val myLocationOverlay = MyLocationNewOverlay(locationProvider, mapView)
                            myLocationOverlay.enableMyLocation()
                            overlays.add(myLocationOverlay)

                            val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val lastKnown = try {
                                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            } catch (e: SecurityException) { null }
                            if (lastKnown != null) {
                                userLocation = GeoPoint(lastKnown.latitude, lastKnown.longitude)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    overlays.add(object : Overlay() {
                        override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
                            return false
                        }
                    })

                    mapViewRef = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar with glass effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BgDark.copy(alpha = 0.9f),
                            BgDark.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(CardGlass)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = TextWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "附近透析中心",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                Box(
                    modifier = Modifier
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${centers.size} 个中心",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (!hasLocationPermission) {
                    val activity = context as? androidx.activity.ComponentActivity
                    activity?.requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        1001
                    )
                } else {
                    try {
                        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (lastKnown != null) {
                            userLocation = GeoPoint(lastKnown.latitude, lastKnown.longitude)
                            mapViewRef?.controller?.animateTo(userLocation)
                            mapViewRef?.controller?.setZoom(15.0)
                        } else {
                            mapViewRef?.controller?.animateTo(GeoPoint(initialCenter.lat, initialCenter.lng))
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (selectedCenter != null) 200.dp else 24.dp),
            containerColor = CardGlass,
            contentColor = TextWhite,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.MyLocation,
                "我的位置",
                tint = AccentBlueLight,
                modifier = Modifier.size(22.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = if (selectedCenter != null) 200.dp else 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardGlass)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "双指缩放 · 拖动平移",
                color = TextWhiteSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (selectedCenter != null) {
            CenterDetailSheet(
                center = selectedCenter!!,
                userLocation = userLocation,
                onCall = {
                    selectedCenter?.phone?.let { phone ->
                        val callIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        context.startActivity(callIntent)
                    }
                },
                onNavigate = {
                    selectedCenter?.let { c ->
                        val gmmIntentUri = if (userLocation != null) {
                            Uri.parse("google.navigation:q=${c.lat},${c.lng}&mode=d")
                        } else {
                            Uri.parse("geo:0,0?q=${c.lat},${c.lng}(${Uri.encode(c.name)})")
                        }
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        val available = runCatching {
                            context.packageManager.queryIntentActivities(mapIntent, 0).isNotEmpty()
                        }.getOrDefault(false)
                        if (!available) {
                            mapIntent.data = Uri.parse("geo:0,0?q=${c.lat},${c.lng}")
                        }
                        context.startActivity(mapIntent)
                    }
                },
                onDismiss = { selectedCenter = null }
            )
        }
    }
}

@Composable
private fun CenterDetailSheet(
    center: DialysisCenter,
    userLocation: GeoPoint?,
    onCall: () -> Unit,
    onNavigate: () -> Unit,
    onDismiss: () -> Unit
) {
    val distanceText = remember(center, userLocation) {
        if (userLocation != null && center.lat != 0.0 && center.lng != 0.0) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                center.lat, center.lng,
                results
            )
            val distKm = results[0] / 1000.0
            if (distKm < 1.0) "${(distKm * 1000).roundToInt()}米"
            else String.format("%.1f公里", distKm)
        } else {
            center.distance ?: ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CardGlass)
                .clickable(enabled = false, onClick = {})
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AccentRed, AccentRedDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    HospitalCrossIcon(tint = Color.White, size = 26.dp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = center.name,
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 23.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = center.address,
                        color = TextWhiteSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (center.level != null) {
                    Tag(text = center.level, backgroundColor = AccentGreen.copy(alpha = 0.15f), textColor = AccentGreen)
                }
                if (distanceText.isNotEmpty()) {
                    Tag(text = "📍 $distanceText", backgroundColor = AccentBlue.copy(alpha = 0.15f), textColor = AccentBlueLight)
                }
                if (center.phone != null) {
                    Tag(text = "📞 可预约", backgroundColor = Color.White.copy(alpha = 0.08f), textColor = TextWhiteSecondary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp)) },
                    text = if (center.phone != null) "拨打电话" else "暂无电话",
                    backgroundColor = Color.White.copy(alpha = 0.08f),
                    contentColor = if (center.phone != null) TextWhite else TextMuted,
                    enabled = center.phone != null,
                    onClick = onCall
                )
                ActionButton(
                    modifier = Modifier.weight(1.3f),
                    icon = { Icon(Icons.Default.Navigation, null, modifier = Modifier.size(18.dp)) },
                    text = "开始导航",
                    backgroundColor = AccentBlue,
                    contentColor = Color.White,
                    enabled = true,
                    onClick = onNavigate
                )
            }
        }
    }
}

@Composable
private fun Tag(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (enabled) backgroundColor else Color.White.copy(alpha = 0.04f),
        animationSpec = tween(200), label = "bg"
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = if (backgroundColor == AccentBlue) 8.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = AccentBlue.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            CompositionLocalProvider(LocalContentColor provides contentColor) { icon() }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = contentColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HospitalCrossIcon(tint: Color, size: androidx.compose.ui.unit.Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = this.size.width * 0.22f
        val half = this.size.width / 2f
        drawRoundRect(
            color = tint,
            topLeft = Offset(half - strokeWidth / 2f, 0f),
            size = androidx.compose.ui.geometry.Size(strokeWidth, this.size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth / 3f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, half - strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(this.size.width, strokeWidth),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth / 3f)
        )
    }
}

private fun createHospitalMarkerIcon(context: Context, isSelected: Boolean): Drawable {
    val density = context.resources.displayMetrics.density
    val baseSize = if (isSelected) 56 else 44
    val size = (baseSize * density).toInt()
    val bitmap = Bitmap.createBitmap(size, (size * 1.3f).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)

    val markerHeight = size * 1.15f
    val pinWidth = size * 0.75f

    val shadowPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#44000000")
        maskFilter = BlurMaskFilter(8f * density, BlurMaskFilter.Blur.NORMAL)
    }
    val cx = size / 2f
    val cy = markerHeight / 2f

    val shadowPath = AndroidPath().apply {
        addCircle(cx, cy - size * 0.1f, pinWidth / 2f, AndroidPath.Direction.CW)
        moveTo(cx - pinWidth * 0.18f, cy + pinWidth * 0.2f)
        lineTo(cx, markerHeight)
        lineTo(cx + pinWidth * 0.18f, cy + pinWidth * 0.2f)
        close()
    }
    canvas.drawPath(shadowPath, shadowPaint)

    val mainColor = AndroidColor.parseColor("#EF4444")
    val pinPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = mainColor
        style = AndroidPaint.Style.FILL
    }
    val pinPath = AndroidPath().apply {
        addCircle(cx, cy - size * 0.1f, pinWidth / 2f, AndroidPath.Direction.CW)
        moveTo(cx - pinWidth * 0.18f, cy + pinWidth * 0.2f)
        lineTo(cx, markerHeight)
        lineTo(cx + pinWidth * 0.18f, cy + pinWidth * 0.2f)
        close()
    }
    canvas.drawPath(pinPath, pinPaint)

    val innerCirclePaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.WHITE
        style = AndroidPaint.Style.FILL
    }
    val innerRadius = pinWidth * 0.30f
    val innerCx = size / 2f
    val innerCy = markerHeight / 2f - size * 0.1f
    canvas.drawCircle(innerCx, innerCy, innerRadius, innerCirclePaint)

    val crossPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = mainColor
        style = AndroidPaint.Style.FILL
    }
    val crossThickness = innerRadius * 0.35f
    val crossLen = innerRadius * 0.9f
    canvas.drawRoundRect(
        RectF(
            innerCx - crossThickness / 2f,
            innerCy - crossLen / 2f,
            innerCx + crossThickness / 2f,
            innerCy + crossLen / 2f
        ),
        crossThickness / 3f, crossThickness / 3f, crossPaint
    )
    canvas.drawRoundRect(
        RectF(
            innerCx - crossLen / 2f,
            innerCy - crossThickness / 2f,
            innerCx + crossLen / 2f,
            innerCy + crossThickness / 2f
        ),
        crossThickness / 3f, crossThickness / 3f, crossPaint
    )

    if (isSelected) {
        val borderPaint = AndroidPaint().apply {
            isAntiAlias = true
            color = AndroidColor.WHITE
            style = AndroidPaint.Style.STROKE
            strokeWidth = 4f * density
        }
        canvas.drawCircle(innerCx, innerCy, pinWidth / 2f, borderPaint)
    }

    return BitmapDrawable(context.resources, bitmap)
}

private fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).roundToInt()
}
