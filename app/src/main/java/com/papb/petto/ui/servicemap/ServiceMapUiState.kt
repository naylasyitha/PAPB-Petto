package com.papb.petto.ui.servicemap

import com.papb.petto.data.model.Service

data class ServiceMapUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val selectedCategory: String = "All",
    val query: String = "",
    val hasLocation: Boolean = false,
    val permissionDenied: Boolean = false,
    val errorMessage: String? = null
)