package com.papb.petto.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class GeoPoint(val lat: Double, val lng: Double)

/**
 * title      : kelurahan / kecamatan
 * subtitle   : kota / kabupaten + provinsi
 * regionName : kota / kabupaten, dipakai sebagai wilayah laporan
 */
data class PlaceInfo(val title: String, val subtitle: String, val regionName: String)

class LocationRepository(private val context: Context) {

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoPoint? {
        if (!hasPermission()) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    cont.resume(loc?.let { GeoPoint(it.latitude, it.longitude) })
                }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    @Suppress("DEPRECATION")
    suspend fun reverseGeocode(lat: Double, lng: Double): PlaceInfo? = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) return@withContext null
            val address = Geocoder(context, Locale("id", "ID"))
                .getFromLocation(lat, lng, 1)
                ?.firstOrNull() ?: return@withContext null

            val city = address.subAdminArea ?: address.locality ?: "Unknown area"
            val title = address.subLocality ?: address.locality ?: city
            val subtitle = listOfNotNull(city, address.adminArea).distinct().joinToString(", ")
            PlaceInfo(title = title, subtitle = subtitle, regionName = city)
        } catch (e: Exception) {
            null
        }
    }
}
