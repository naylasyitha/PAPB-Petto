package com.papb.petto.ui.scheduleform

enum class CareTypeOption {
    VACCINATION,
    MEDICATION,
    GROOMING,
    OTHER
}

data class ScheduleFormState(
    val isEditMode: Boolean = false,
    val scheduleId: String? = null,
    val careType: CareTypeOption = CareTypeOption.VACCINATION,
    val careName: String = "Rabies vaccine",
    val lastDoneDate: String = "Oct 20, 2025",
    val repeatIntervalValue: String = "12",
    val repeatIntervalUnit: String = "Months",
    val intervalUnitOptions: List<String> = listOf("Months", "Weeks", "Days", "Years"),
    val nextDueDate: String = "Oct 20, 2026",
    val calculationFormulaText: String = "Calculated from Last Done Date + 12 months interval.",
    val remind3DaysBefore: Boolean = true,
    val remind3DaysText: String = "Push alert on Oct 17, 2026",
    val remind1DayBefore: Boolean = true,
    val remind1DayText: String = "Push alert on Oct 19, 2026",
    val notes: String = "",
    val isIntervalDropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
