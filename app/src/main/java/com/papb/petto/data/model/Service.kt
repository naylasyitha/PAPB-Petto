package com.papb.petto.data.model

data class Service(
    val id: Int,
    val name: String,
    val category: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val openHours: String? = null,
    val isOpenNow: Boolean = true,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val tag: String? = null,
    val distanceKm: Double? = null
)