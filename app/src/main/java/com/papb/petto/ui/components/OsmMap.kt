package com.papb.petto.ui.components

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import java.io.File

/** Peta OpenStreetMap (osmdroid) */
private object OsmSetup {
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        val appContext = context.applicationContext
        Configuration.getInstance().apply {
            // OSM mewajibkan User-Agent yang jelas; cache disimpan di folder cache app.
            userAgentValue = appContext.packageName
            val base = File(appContext.cacheDir, "osmdroid")
            osmdroidBasePath = base
            osmdroidTileCache = File(base, "tiles")
        }
        initialized = true
    }
}

/** MapView yang tidak menangkap sentuhan. */
private class StaticMapView(context: Context) : MapView(context) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean = false
}

private fun MapView.applyDefaults() {
    setTileSource(TileSourceFactory.MAPNIK)
    isTilesScaledToDpi = true
    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
}

/**
 * Peta statis
 * - radiusMeters != null : gambar lingkaran area prioritas dan zoom agar lingkaran pas di peta.
 *   note: Titik pusat sengaja TIDAK diberi penanda.
 * - showMarker : beri penanda di titik (dipakai untuk pratinjau milik pelapor).
 */
@Composable
fun StaticOsmMap(
    lat: Double,
    lng: Double,
    modifier: Modifier = Modifier,
    radiusMeters: Double? = null,
    showMarker: Boolean = false
) {
    if (LocalInspectionMode.current) {
        MapPreviewPlaceholder(modifier, drawCircle = radiusMeters != null, drawPin = showMarker)
        return
    }
    val context = LocalContext.current
    val mapView = remember {
        OsmSetup.init(context)
        StaticMapView(context).apply {
            applyDefaults()
            setMultiTouchControls(false)
        }
    }
    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { map ->
            val center = GeoPoint(lat, lng)
            map.overlays.clear()

            if (radiusMeters != null) {
                val circle = Polygon(map).apply {
                    points = Polygon.pointsAsCircle(center, radiusMeters)
                    fillPaint.color = 0x33D84315
                    outlinePaint.color = 0xFFD84315.toInt()
                    outlinePaint.strokeWidth = 4f
                }
                map.overlays.add(circle)
                // Zoom setelah layout selesai, kalau tidak peta belum punya ukuran.
                map.post { map.zoomToBoundingBox(circle.bounds, false, 80) }
            } else {
                map.controller.setZoom(16.0)
                map.controller.setCenter(center)
            }

            if (showMarker) {
                map.overlays.add(
                    Marker(map).apply {
                        position = center
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                )
            }
            map.overlays.add(CopyrightOverlay(map.context))
            map.invalidate()
        }
    )
}

/** Peta interaktif (geser/zoom). */
@Composable
fun InteractiveOsmMap(
    initialLat: Double,
    initialLng: Double,
    onMapViewReady: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        MapPreviewPlaceholder(modifier, drawCircle = false, drawPin = true)
        return
    }
    val context = LocalContext.current
    val mapView = remember {
        OsmSetup.init(context)
        MapView(context).apply {
            applyDefaults()
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            controller.setCenter(GeoPoint(initialLat, initialLng))
            overlays.add(CopyrightOverlay(context))
        }
    }
    DisposableEffect(mapView) {
        onMapViewReady(mapView)
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(modifier = modifier, factory = { mapView })
}

/** Pengganti peta di Preview. */
@Composable
private fun MapPreviewPlaceholder(modifier: Modifier, drawCircle: Boolean, drawPin: Boolean) {
    Box(
        modifier = modifier.background(Color(0xFFE6DFD3)),
        contentAlignment = Alignment.Center
    ) {
        if (drawCircle) {
            Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(Color(0x33D84315), radius = radius, center = center)
                drawCircle(Color(0xFFD84315), radius = radius, center = center, style = Stroke(width = 4f))
            }
        }
        Text(
            text = if (drawPin) "📍 Map preview" else "Map preview (OSM)",
            fontSize = 12.sp,
            color = Color(0xFF6B5B4B)
        )
    }
}
