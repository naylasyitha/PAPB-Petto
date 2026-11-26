package com.papb.petto.ui.petdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.PetRepositoryImpl
import com.papb.petto.data.repository.ScheduleRepository
import com.papb.petto.data.repository.ScheduleRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetDetailViewModel(
    private val petRepository: PetRepository = PetRepositoryImpl(),
    private val scheduleRepository: ScheduleRepository = ScheduleRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetDetailState())
    val uiState: StateFlow<PetDetailState> = _uiState.asStateFlow()

    fun loadPetDetails(petId: String = "1") {
        viewModelScope.launch {
            val petResult = petRepository.getPetById(petId)
            petResult.onSuccess { petDto ->
                if (petDto != null) {
                    _uiState.update {
                        it.copy(
                            petId = petDto.id,
                            petName = petDto.name,
                            speciesBreed = "${petDto.species} • ${petDto.breed}",
                            ageText = petDto.ageMonths,
                            weightText = petDto.weightKg,
                            reproductiveText = if (petDto.isSpayedNeutered) "Neutered" else "Intact"
                        )
                    }
                }
            }

            val schedulesResult = scheduleRepository.getSchedulesForPet(petId)
            schedulesResult.onSuccess { dtos ->
                if (dtos.isNotEmpty()) {
                    val upcomingList = dtos.filter { it.status != "done" }.map { dto ->
                        HealthScheduleItem(
                            id = dto.id,
                            title = dto.careName,
                            date = dto.nextDueDate,
                            location = dto.notes.ifBlank { "Klinik Satwa Sehat" },
                            status = dto.status,
                            isOverdue = dto.status.equals("overdue", ignoreCase = true)
                        )
                    }
                    val pastList = dtos.filter { it.status == "done" }.map { dto ->
                        PastRecordItem(
                            id = dto.id,
                            title = dto.careName,
                            date = dto.lastDoneDate,
                            location = dto.notes.ifBlank { "Klinik Satwa Sehat" }
                        )
                    }
                    _uiState.update {
                        it.copy(
                            healthSchedules = upcomingList,
                            pastRecords = pastList
                        )
                    }
                }
            }
        }
    }

    fun onDeleteScheduleClicked(schedule: HealthScheduleItem) {
        _uiState.update {
            it.copy(scheduleToDelete = schedule, showDeleteDialog = true)
        }
    }

    fun confirmDeleteSchedule() {
        val toDelete = _uiState.value.scheduleToDelete
        if (toDelete != null) {
            viewModelScope.launch {
                scheduleRepository.deleteSchedule(toDelete.id)
                _uiState.update { state ->
                    state.copy(
                        healthSchedules = state.healthSchedules.filter { it.id != toDelete.id },
                        showDeleteDialog = false,
                        scheduleToDelete = null
                    )
                }
            }
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, scheduleToDelete = null) }
    }

    fun onMarkAsDoneClicked(scheduleId: String) {
        val schedule = _uiState.value.healthSchedules.find { it.id == scheduleId }
        if (schedule != null) {
            viewModelScope.launch {
                scheduleRepository.markScheduleAsDone(scheduleId)
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
}
