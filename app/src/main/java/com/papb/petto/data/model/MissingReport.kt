package com.papb.petto.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MissingReport(
    val id: String,
    @SerialName("reporter_id") val reporterId: String,
    val name: String,
    val species: String,
    val breed: String,
    val description: String? = null,
    @SerialName("photo_paths") val photoPaths: List<String> = emptyList(),
    @SerialName("region_name") val regionName: String,
    @SerialName("display_lat") val displayLat: Double,
    @SerialName("display_lng") val displayLng: Double,
    @SerialName("last_seen_at") val lastSeenAt: String,
    val phone: String,
    @SerialName("reward_idr") val rewardIdr: Long? = null,
    val status: String = "active",
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class AppNotification(
    val id: String,
    @SerialName("report_id") val reportId: String? = null,
    val title: String,
    val body: String,
    @SerialName("read_at") val readAt: String? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class UserProfile(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    val phone: String? = null,
    @SerialName("home_title") val homeTitle: String? = null,
    @SerialName("home_subtitle") val homeSubtitle: String? = null,
    @SerialName("home_lat") val homeLat: Double? = null,
    @SerialName("home_lng") val homeLng: Double? = null,
    @SerialName("alert_radius_km") val alertRadiusKm: Int = 2
)
