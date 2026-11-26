package com.papb.petto.data.remote.dto

import com.papb.petto.data.model.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val id: Int,
    val name: String,
    val category: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    @SerialName("open_hours") val openHours: String? = null,
    @SerialName("is_open_now") val isOpenNow: Boolean = true,
    val rating: Double? = null,
    @SerialName("review_count") val reviewCount: Int? = null,
    val tag: String? = null
)

fun ServiceDto.toService(): Service = Service(
    id = id,
    name = name,
    category = category,
    address = address,
    latitude = latitude,
    longitude = longitude,
    phone = phone,
    openHours = openHours,
    isOpenNow = isOpenNow,
    rating = rating,
    reviewCount = reviewCount,
    tag = tag
)