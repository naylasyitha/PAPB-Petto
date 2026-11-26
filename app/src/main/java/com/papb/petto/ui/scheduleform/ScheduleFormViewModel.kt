package com.papb.petto.ui.scheduleform

import android.content.Context
import androidx.lifecycle.ViewModel
import com.papb.petto.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScheduleFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleFormState())
    val uiState: StateFlow<ScheduleFormState> = _uiState.asStateFlow()

    fun setupForm(isEditMode: Boolean, scheduleId: String? = null) {
        if (isEditMode) {
            _uiState.update {
                ScheduleFormState(
                    isEditMode = true,
                    scheduleId = scheduleId ?: "1",
                    careType = CareTypeOption.VACCINATION,
                    careName = "Annual Rabies Vaccination",
                    lastDoneDate = "Oct 17, 2025",
                    repeatIntervalValue = "12",
                    repeatIntervalUnit = "Months",
                    nextDueDate = "Oct 17, 2026",
                    calculationFormulaText = "Calculated from Last Done Date + 12 months interval.",
                    remind3DaysText = "Push alert on Oct 14, 2026",
                    remind1DayText = "Push alert on Oct 16, 2026",
                    notes = "Klinik Satwa Sehat recommendations"
                )
            }
        } else {
            _uiState.update { ScheduleFormState(isEditMode = false) }
        }
    }

    fun onCareTypeChange(type: CareTypeOption) {
        _uiState.update { it.copy(careType = type) }
    }

    fun onCareNameChange(name: String) {
        _uiState.update { it.copy(careName = name, errorMessage = null) }
    }

    fun onLastDoneDateChange(date: String) {
        _uiState.update {
            it.copy(lastDoneDate = date, errorMessage = null)
        }
        recalculateDueDate()
    }

    fun onIntervalValueChange(value: String) {
        _uiState.update { it.copy(repeatIntervalValue = value) }
        recalculateDueDate()
    }

    fun onIntervalUnitSelected(unit: String) {
        _uiState.update { it.copy(repeatIntervalUnit = unit, isIntervalDropdownExpanded = false) }
        recalculateDueDate()
    }

    fun onIntervalDropdownExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(isIntervalDropdownExpanded = expanded) }
    }

    fun onRemind3DaysChange(remind: Boolean) {
        _uiState.update { it.copy(remind3DaysBefore = remind) }
    }

    fun onRemind1DayChange(remind: Boolean) {
        _uiState.update { it.copy(remind1DayBefore = remind) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    private fun recalculateDueDate() {
        val state = _uiState.value
        val intervalVal = state.repeatIntervalValue.toIntOrNull() ?: 12
        val unit = state.repeatIntervalUnit

        _uiState.update {
            it.copy(
                nextDueDate = "Oct 20, 2026",
                calculationFormulaText = "Calculated from Last Done Date + $intervalVal ${unit.lowercase()} interval.",
                remind3DaysText = "Push alert on Oct 17, 2026",
                remind1DayText = "Push alert on Oct 19, 2026"
            )
        }
    }

    fun onSaveScheduleClicked(context: Context) {
        val state = _uiState.value
        if (state.careName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter care name") }
            return
        }

        // Send local push notification reminder
        NotificationHelper.sendCareReminderNotification(
            context = context,
            title = "🐾 Petto Schedule Saved: ${state.careName}",
            message = "Reminder set for ${state.nextDueDate}. ${if (state.remind3DaysBefore) "H-3 & " else ""}${if (state.remind1DayBefore) "H-1 " else ""}alerts active!"
        )

        _uiState.update { it.copy(isSuccess = true) }
    }
}
