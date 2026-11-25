package com.papb.petto.ui.missing.report

import android.net.Uri
import com.papb.petto.data.model.PetSpecies

data class ReportMissingState(
    val photoUris: List<Uri> = emptyList(),
    val name: String = "",
    val species: PetSpecies? = null,
    val breed: String = "",
    val description: String = "",

    val pickedLat: Double? = null,
    val pickedLng: Double? = null,
    val areaTitle: String = "",
    val areaSubtitle: String = "",
    val regionName: String = "",
    val isLocating: Boolean = false,

    val dateMillisUtc: Long? = null,
    val hour: Int? = null,
    val minute: Int? = null,

    val phoneDigits: String = "",
    val rewardDigits: String = "",

    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        const val MAX_PHOTOS = 3
        const val MAX_DESCRIPTION = 300
    }
}
