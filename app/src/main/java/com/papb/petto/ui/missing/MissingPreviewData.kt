package com.papb.petto.ui.missing

import com.papb.petto.data.model.PetSpecies

internal object MissingPreviewData {

    val stella = MissingCardItem(
        id = "1",
        name = "Stella",
        species = PetSpecies.DOG,
        breed = "Golden Retriever",
        description = "Wearing a pink scarf and gingham yellow bandana. Very friendly but easily startled by loud motorbikes. " +
                "Responds to her name and loves treats, please approach slowly and call me right away.",
        photoUrls = listOf("https://example.com/stella-1.jpg", "https://example.com/stella-2.jpg"),
        postedAgoText = "2 hours ago",
        rewardText = "Rp800.000",
        lastSeenDateText = "15 Oct 2026",
        distanceText = "1.2 km away",
        centerLat = -6.2605,
        centerLng = 106.8106,
        radiusMeters = 700.0,
        phone = "+6281234567890",
        regionName = "Kota Jakarta Selatan",
        isOwner = true
    )

    val bella = MissingCardItem(
        id = "2",
        name = "Bella",
        species = PetSpecies.CAT,
        breed = "Calico Cat",
        description = "White chest with black patch on her nose. Might hide under cars or small residential alleys.",
        photoUrls = listOf("https://example.com/bella.jpg"),
        postedAgoText = "5 hours ago",
        rewardText = "Rp550.000",
        lastSeenDateText = "15 Oct 2026",
        distanceText = "In search area",
        centerLat = -6.2605,
        centerLng = 106.8106,
        radiusMeters = 375.0,
        phone = "+6281234567890",
        regionName = "Kota Jakarta Selatan"
    )

    val oreo = MissingCardItem(
        id = "3",
        name = "Oreo",
        species = PetSpecies.DOG,
        breed = "French Bulldog",
        description = "Brindle black coat with white patch on chest.",
        photoUrls = listOf("https://example.com/oreo.jpg"),
        postedAgoText = "1 day ago",
        rewardText = null,
        lastSeenDateText = "14 Oct 2026",
        distanceText = "4.5 km away",
        centerLat = -6.2605,
        centerLng = 106.8106,
        radiusMeters = 2900.0,
        phone = "+6281234567890",
        regionName = "Kota Jakarta Selatan"
    )

    val state = MissingPetsState(
        items = listOf(stella, bella, oreo),
        isLoading = false,
        regions = listOf(
            RegionOption("Kota Jakarta Selatan", 12),
            RegionOption("Kota Bogor", 3)
        ),
        totalCount = 12,
        speciesCounts = mapOf(
            PetSpecies.DOG to 4,
            PetSpecies.CAT to 3,
            PetSpecies.BIRD to 2
        ),
        page = 1,
        totalPages = 4,
        hasUserLocation = true
    )
}
