package com.papb.petto.ui.missing

import com.papb.petto.data.model.MissingReport
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.util.FormatUtils
import com.papb.petto.util.PriorityAreaCalculator
import java.time.Instant

data class MissingCardItem(
    val id: String,
    val name: String,
    val species: PetSpecies,
    val breed: String,
    val description: String?,
    val photoUrls: List<String>,
    val postedAgoText: String,
    val rewardText: String?,
    val lastSeenDateText: String,
    val distanceText: String?,
    val centerLat: Double,
    val centerLng: Double,
    val radiusMeters: Double,
    val phone: String,
    val regionName: String,
    val isOwner: Boolean = false
)

fun MissingReport.toCardItem(
    userLat: Double?,
    userLng: Double?,
    now: Instant,
    currentUserId: String? = null,
    photoUrl: (String) -> String
): MissingCardItem {
    val speciesEnum = PetSpecies.fromDb(species)
    val lastSeen = PriorityAreaCalculator.parseInstant(lastSeenAt)
    val created = PriorityAreaCalculator.parseInstant(createdAt)
    val radius = PriorityAreaCalculator.radiusMeters(speciesEnum, lastSeen, now)
    val distance = if (userLat != null && userLng != null) {
        PriorityAreaCalculator.distanceToAreaMeters(userLat, userLng, displayLat, displayLng, radius)
    } else null

    return MissingCardItem(
        id = id,
        name = name,
        species = speciesEnum,
        breed = breed,
        description = description,
        photoUrls = photoPaths.map(photoUrl),
        postedAgoText = PriorityAreaCalculator.formatPostedAgo(created, now),
        rewardText = rewardIdr?.takeIf { it > 0 }?.let { FormatUtils.formatRupiah(it) },
        lastSeenDateText = FormatUtils.formatDate(lastSeen),
        distanceText = distance?.let { PriorityAreaCalculator.formatDistance(it) },
        centerLat = displayLat,
        centerLng = displayLng,
        radiusMeters = radius,
        phone = phone,
        regionName = regionName,
        isOwner = currentUserId != null && currentUserId == reporterId
    )
}
