package com.papb.petto.ui.petdetail

import com.papb.petto.R

data class HealthScheduleItem(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val status: String,
    val isOverdue: Boolean = false
)

data class PastRecordItem(
    val id: String,
    val title: String,
    val date: String,
    val location: String
)

data class PetDetailState(
    val petId: String = "",
    val petName: String = "",
    val speciesBreed: String = "",
    val ageText: String = "-",
    val weightText: String = "-",
    val reproductiveText: String = "-",
    val isFemale: Boolean = true,
    val imageRes: Int = R.drawable.ic_pet_default,
    val imageUrl: String? = null,
    // Snowie & Hotdog di Profile adalah pet contoh (bukan data database)
    val isSamplePet: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val healthSchedules: List<HealthScheduleItem> = emptyList(),
    val pastRecords: List<PastRecordItem> = emptyList(),
    val scheduleToDelete: HealthScheduleItem? = null,
    val showDeleteDialog: Boolean = false
)
