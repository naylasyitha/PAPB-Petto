package com.papb.petto.ui.scheduleform

enum class CareTypeOption {
    VACCINATION,
    MEDICATION,
    GROOMING,
    OTHER
}

data class ScheduleFormState(
    val isEditMode: Boolean = false,
    val petId: String? = null,
    val scheduleId: String? = null,
    val careType: CareTypeOption = CareTypeOption.VACCINATION,
    val careName: String = "",
    val lastDoneDate: String = "",
    val repeatIntervalValue: String = "12",
    val repeatIntervalUnit: String = "Months",
    val intervalUnitOptions: List<String> = listOf("Months", "Weeks", "Days", "Years"),
    val nextDueDate: String = "-",
    val calculationFormulaText: String = "",
    val remind3DaysBefore: Boolean = true,
    val remind3DaysText: String = "",
    val remind1DayBefore: Boolean = true,
    val remind1DayText: String = "",
    val notes: String = "",
    val isIntervalDropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
