package com.papb.petto.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PetDto(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String? = null,
    val name: String,
    val species: String,
    val breed: String,
    val gender: String,
    @SerialName("age_months") val ageMonths: String,
    @SerialName("weight_kg") val weightKg: String,
    @SerialName("is_spayed_neutered") val isSpayedNeutered: Boolean = false,
    val notes: String = "",
    @SerialName("image_url") val imageUrl: String? = null
)
