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
    val petId: String = "1",
    val petName: String = "Snowie",
    val speciesBreed: String = "Dog • Maltese",
    val ageText: String = "10 months",
    val weightText: String = "2,1 kg",
    val reproductiveText: String = "Neutered",
    val imageRes: Int = R.drawable.snowie,
    val healthSchedules: List<HealthScheduleItem> = listOf(
        HealthScheduleItem("1", "Annual Rabies Vaccination", "Oct 17, 2026", "Klinik Satwa Sehat", "upcoming"),
        HealthScheduleItem("2", "Full Grooming and Spa", "Oct 30, 2026", "PawCare Salon", "upcoming"),
        HealthScheduleItem("3", "Deworming Routine", "Yesterday, Oct 14, 2026", "Klinik Satwa Sehat", "overdue", isOverdue = true)
    ),
    val pastRecords: List<PastRecordItem> = listOf(
        PastRecordItem("101", "Flea & Tick Prevention", "Sep 30, 2026", "Klinik Satwa Sehat"),
        PastRecordItem("102", "Annual Wellness Exam", "Aug 28, 2026", "Klinik Satwa Sehat")
    ),
    val scheduleToDelete: HealthScheduleItem? = null,
    val showDeleteDialog: Boolean = false
)
