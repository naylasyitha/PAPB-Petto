package com.papb.petto.ui.petdetail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PetDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PetDetailState())
    val uiState: StateFlow<PetDetailState> = _uiState.asStateFlow()

    fun onDeleteScheduleClicked(schedule: HealthScheduleItem) {
        _uiState.update {
            it.copy(scheduleToDelete = schedule, showDeleteDialog = true)
        }
    }

    fun confirmDeleteSchedule() {
        val toDelete = _uiState.value.scheduleToDelete
        if (toDelete != null) {
            _uiState.update { state ->
                state.copy(
                    healthSchedules = state.healthSchedules.filter { it.id != toDelete.id },
                    showDeleteDialog = false,
                    scheduleToDelete = null
                )
            }
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, scheduleToDelete = null) }
    }

    fun onMarkAsDoneClicked(scheduleId: String) {
        val schedule = _uiState.value.healthSchedules.find { it.id == scheduleId }
        if (schedule != null) {
            val newRecord = PastRecordItem(
                id = schedule.id,
                title = schedule.title,
                date = schedule.date,
                location = schedule.location
            )
            _uiState.update { state ->
                state.copy(
                    healthSchedules = state.healthSchedules.filter { it.id != scheduleId },
                    pastRecords = listOf(newRecord) + state.pastRecords
                )
            }
        }
    }
}
