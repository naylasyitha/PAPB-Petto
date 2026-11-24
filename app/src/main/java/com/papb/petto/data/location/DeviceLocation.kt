package com.papb.petto.data.location

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val isPrecise: Boolean = false
)