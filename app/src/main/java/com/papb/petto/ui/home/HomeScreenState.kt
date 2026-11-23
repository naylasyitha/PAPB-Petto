package com.papb.petto.ui.home

import com.papb.petto.ui.components.BottomTab

data class PetScheduleItem(
    val title: String,
    val petName: String,
    val dateLocation: String,
    val statusTag: String,
    val isOverdue: Boolean = false
)

data class HomeScreenState(
    val userName: String = "Anasya",
    val petCountText: String = "2 happy, healthy pets today",
    val currentTab: BottomTab = BottomTab.HOME,
    val schedules: List<PetScheduleItem> = listOf(
        PetScheduleItem("Deworming Routine", "Snowie", "Yesterday, Oct 14, 2026 • Klinik Satwa Sehat", "Overdue", isOverdue = true),
        PetScheduleItem("Annual Rabies Vaccination", "Snowie", "Oct 17, 2026 • Klinik Satwa Sehat", "D-1"),
        PetScheduleItem("Full Grooming and Spa", "Snowie", "Oct 20, 2026 • Pawcare Salon", "D-3"),
        PetScheduleItem("Dental cleaning", "Hotdog", "Oct 20, 2026 • Klinik Satwa Sehat", "D-3")
    ),
    val nearbySummaryText: String = "3 clinics & 5 pet shops are still open",
    val donationTitle: String = "Help fund surgery & food for 45 stray cats",
    val donationSubtitle: String = "Urgent funds for panleukopenia treatment and nutritious food for stray cats this month.",
    val donationShelter: String = "Shelter Sahabat Satwa Bogor",
    val donationRaisedText: String = "Rp 18.250.000",
    val donationPercentage: Int = 72,
    val donationDaysLeft: String = "5 days left"
)
