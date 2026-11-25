package com.papb.petto.util

import com.papb.petto.data.model.PetSpecies
import java.time.Instant
import java.time.OffsetDateTime
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object PriorityAreaCalculator {

    data class GrowthParams(val initialM: Double, val perHourM: Double, val capM: Double)

    // radius = min(initial + jam * perJam, cap)
    fun paramsFor(species: PetSpecies): GrowthParams = when (species) {
        PetSpecies.DOG -> GrowthParams(initialM = 500.0, perHourM = 100.0, capM = 5000.0)
        PetSpecies.CAT -> GrowthParams(initialM = 250.0, perHourM = 25.0, capM = 1500.0)
        PetSpecies.BIRD -> GrowthParams(initialM = 300.0, perHourM = 100.0, capM = 3000.0)
        PetSpecies.RABBIT -> GrowthParams(initialM = 250.0, perHourM = 10.0, capM = 500.0)
        PetSpecies.REPTILE -> GrowthParams(initialM = 250.0, perHourM = 15.0, capM = 1000.0)
        PetSpecies.OTHER -> GrowthParams(initialM = 250.0, perHourM = 25.0, capM = 1000.0)
    }

    // Bobot skor prioritas (kedekatan vs kesegaran) dan proximity scale.
    private const val WEIGHT_PROXIMITY = 0.5
    private const val WEIGHT_FRESHNESS = 0.5
    private const val FRESHNESS_HALF_SCALE_HOURS = 48.0
    private const val PROXIMITY_SCALE_KM = 3.0

    fun hoursSince(from: Instant, now: Instant): Double =
        max(0.0, (now.toEpochMilli() - from.toEpochMilli()) / 3_600_000.0)

    fun radiusMeters(species: PetSpecies, lastSeenAt: Instant, now: Instant): Double {
        val p = paramsFor(species)
        return min(p.initialM + hoursSince(lastSeenAt, now) * p.perHourM, p.capM)
    }

    /** Jarak dari pengguna ke TEPI lingkaran terdekat (0 jika pengguna sudah di dalam lingkaran). */
    fun distanceToAreaMeters(
        userLat: Double, userLng: Double,
        centerLat: Double, centerLng: Double,
        radiusM: Double
    ): Double = max(0.0, haversineMeters(userLat, userLng, centerLat, centerLng) - radiusM)

    /** Skor 0..1, makin tinggi makin atas. Tanpa lokasi pengguna, hanya kesegaran yang dipakai. */
    fun priorityScore(lastSeenAt: Instant, now: Instant, distanceToAreaM: Double?): Double {
        val freshness = exp(-hoursSince(lastSeenAt, now) / FRESHNESS_HALF_SCALE_HOURS)
        if (distanceToAreaM == null) return freshness
        val proximity = 1.0 / (1.0 + (distanceToAreaM / 1000.0) / PROXIMITY_SCALE_KM)
        return WEIGHT_PROXIMITY * proximity + WEIGHT_FRESHNESS * freshness
    }

    fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return 2 * r * asin(min(1.0, sqrt(a)))
    }

    fun formatDistance(meters: Double): String {
        if (meters <= 0.0) return "In search area"
        return if (meters < 1000) {
            "${((meters / 10).roundToInt() * 10).coerceAtLeast(10)} m away"
        } else {
            String.format(java.util.Locale.US, "%.1f km away", meters / 1000.0)
        }
    }

    fun formatRadius(meters: Double): String =
        if (meters < 1000) "${(meters / 10).roundToInt() * 10} m"
        else String.format(java.util.Locale.US, "%.1f km", meters / 1000.0)

    /** "Just now" / "X min ago" / "X hours ago" / "X days ago" (>24 jam jadi hari). */
    fun formatPostedAgo(createdAt: Instant, now: Instant): String {
        val minutes = max(0L, (now.toEpochMilli() - createdAt.toEpochMilli()) / 60_000L)
        val hours = minutes / 60
        val days = hours / 24
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            else -> "$days ${if (days == 1L) "day" else "days"} ago"
        }
    }

    /** Supabase mengirim timestamp seperti 2026-11-24T10:00:00.123+00:00. */
    fun parseInstant(value: String): Instant =
        try {
            OffsetDateTime.parse(value).toInstant()
        } catch (e: Exception) {
            Instant.parse(value)
        }
}
