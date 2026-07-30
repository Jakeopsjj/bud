package com.dialysis.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.dialysis.app.data.DialysisCenter
import com.dialysis.app.data.ReminderManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

// Color palette for dark glass theme
private val BgDark = Color(0xFF0A0E1A)
private val CardGlass = Color(0x1AFFFFFF)
private val CardGlassStroke = Color(0x26FFFFFF)
private val AccentBlue = Color(0xFF4FC3F7)
private val AccentGreen = Color(0xFF66BB6A)
private val TextWhite = Color(0xFFFFFFFF)
private val TextWhiteSecondary = Color(0xB3FFFFFF)

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

    // Request location permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var selectedCenter by remember { mutableStateOf<DialysisCenter?>(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // osmdroid is configured globally in MainActivity, no extra setup needed here

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        // Map
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.0)
                    val startPoint = GeoPoint(initialCenter.lat, initialCenter.lng)
                    controller.setCenter(startPoint)

                    // Add center markers
                    centers.forEach { center ->
                        val marker = Marker(this).apply {
                            position = GeoPoint(center.lat, center.lng)
                            title = center.name
                            snippet = center.address
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(ctx, android.R.drawable.ic_menu_mylocation)
                            setOnMarkerClickListener { _, _ ->
                                selectedCenter = center
                                true
                            }
                        }
                        overlays.add(marker)
                    }

                    // Location overlay
                    if (hasLocationPermission) {
                        try {
                            val locationProvider = GpsMyLocationProvider(ctx)
                            val myLocationOverlay = MyLocationNewOverlay(locationProvider, this)
                            myLocationOverlay.enableMyLocation()
                            myLocationOverlay.enableFollowLocation()
                            overlays.add(myLocationOverlay)

                            // Get last known location
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

                    // Close info window on map tap
                    overlays.add(object : Overlay() {
                        override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
                            selectedCenter = null
                            return false
                        }
                    })

                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = "附近透析中心",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = TextWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BgDark.copy(alpha = 0.8f)
            )
        )

        // Bottom card for selected center or list hint
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (selectedCenter != null) {
                CenterDetailCard(
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
                            val uri = if (userLocation != null) {
                                Uri.parse("google.navigation:q=${c.lat},${c.lng}")
                            } else {
                                Uri.parse("geo:0,0?q=${Uri.encode(c.name + " " + c.address)}")
                            }
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                // Fallback to generic geo intent
                                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${c.lat},${c.lng}(${Uri.encode(c.name)})"))
                                context.startActivity(fallbackIntent)
                            }
                        }
                    },
                    onDismiss = { selectedCenter = null }
                )
            } else {
                // Info hint card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardGlass.copy(alpha = 0.85f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "点击地图上的标记查看透析中心详情\n" +
                               "共 ${centers.size} 个透析中心",
                        color = TextWhiteSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // My location button
        FloatingActionButton(
            onClick = {
                if (!hasLocationPermission) {
                    // Request permission through activity
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
                            mapView?.controller?.animateTo(userLocation)
                            mapView?.controller?.setZoom(15.0)
                        } else {
                            // If no last known, animate to first center
                            val center = centers.first()
                            mapView?.controller?.animateTo(GeoPoint(center.lat, center.lng))
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (selectedCenter != null) 140.dp else 80.dp),
            containerColor = AccentBlue.copy(alpha = 0.9f),
            shape = CircleShape
        ) {
            Icon(Icons.Default.MyLocation, "我的位置", tint = Color.White)
        }
    }
}

@Composable
private fun CenterDetailCard(
    center: DialysisCenter,
    userLocation: GeoPoint?,
    onCall: () -> Unit,
    onNavigate: () -> Unit,
    onDismiss: () -> Unit
) {
    // Calculate distance if user location available
    val distanceText = remember(center, userLocation) {
        if (userLocation != null && center.lat != 0.0 && center.lng != 0.0) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                center.lat, center.lng,
                results
            )
            val distKm = results[0] / 1000.0
            if (distKm < 1.0) "${(distKm * 1000).toInt()}m" else String.format("%.1fkm", distKm)
        } else {
            center.distance ?: ""
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BgDark.copy(alpha = 0.92f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = center.name,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = center.address,
                        color = TextWhiteSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (center.level != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AccentGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(center.level, color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (distanceText.isNotEmpty()) {
                            Text(
                                text = "📍 $distanceText",
                                color = AccentBlue,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                IconButton(onClick = onDismiss) {
                    Text("✕", color = TextWhiteSecondary, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Call button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (center.phone != null) Color.White.copy(alpha = 0.15f)
                            else Color.White.copy(alpha = 0.05f)
                        )
                        .clickable(enabled = center.phone != null, onClick = onCall),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null,
                            tint = if (center.phone != null) AccentGreen else TextWhiteSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (center.phone != null) "拨打电话" else "无电话",
                            color = if (center.phone != null) TextWhite else TextWhiteSecondary.copy(alpha = 0.4f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Navigate button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentBlue.copy(alpha = 0.85f))
                        .clickable(onClick = onNavigate),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Navigation, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "开始导航",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
