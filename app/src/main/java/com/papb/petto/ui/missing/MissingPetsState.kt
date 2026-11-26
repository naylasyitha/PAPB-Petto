package com.papb.petto.ui.missing

import com.papb.petto.data.model.PetSpecies

data class RegionOption(
    val name: String,
    val count: Int
)

data class MissingPetsState(
    val items: List<MissingCardItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    val searchQuery: String = "",
    val selectedSpecies: PetSpecies? = null,
    val regions: List<RegionOption> = emptyList(),
    val selectedRegion: String? = null,

    val totalCount: Int = 0,
    val speciesCounts: Map<PetSpecies, Int> = emptyMap(),
    val page: Int = 1,
    val totalPages: Int = 1,

    val hasUserLocation: Boolean = false,
    val showLocationDialog: Boolean = false,

    val selectedItem: MissingCardItem? = null
)
