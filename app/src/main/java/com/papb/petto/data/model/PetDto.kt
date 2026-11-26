package com.papb.petto.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PetDto(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String? = null,
    val name: String = "",
    val species: String = "",
    val breed: String? = "",
    val gender: String? = "",
    @SerialName("age_months") val ageMonths: String? = "0",
    @SerialName("weight_kg") val weightKg: String? = "0",
    @SerialName("is_spayed_neutered") val isSpayedNeutered: Boolean? = false,
    val notes: String? = "",
    @SerialName("image_url") val imageUrl: String? = null
) {
    val safeBreed: String get() = breed.orEmpty()
    val safeGender: String get() = gender.orEmpty()
    val safeAgeMonths: String get() = ageMonths.orEmpty().ifBlank { "0" }
    val safeWeightKg: String get() = weightKg.orEmpty().ifBlank { "0" }
    val safeNotes: String get() = notes.orEmpty()
    val safeIsSpayedNeutered: Boolean get() = isSpayedNeutered ?: false
}
