package com.papb.petto.ui.missing.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.papb.petto.ui.components.InteractiveOsmMap
import org.osmdroid.views.MapView

/** Peta layar penuh: geser peta, pin tetap di tengah. */
@Composable
fun MapPickerDialog(
    initialLat: Double,
    initialLng: Double,
    onConfirm: (lat: Double, lng: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                InteractiveOsmMap(
                    initialLat = initialLat,
                    initialLng = initialLng,
                    onMapViewReady = { mapView = it },
                    modifier = Modifier.fillMaxSize()
                )

                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Selected location",
                    tint = Color(0xFFD84315),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 22.dp)
                        .size(44.dp)
                )

                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = colors.primary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(16.dp)
                        .size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xE6FFF7F2)
                    ) {
                        Text(
                            text = "Move the map to place the pin. Other users only see an approximate area, never this exact point.",
                            fontSize = 12.sp,
                            color = Color(0xFF38220F),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Button(
                        onClick = {
                            mapView?.mapCenter?.let { onConfirm(it.latitude, it.longitude) }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(
                            text = "Use this location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
