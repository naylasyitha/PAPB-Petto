package com.papb.petto.ui.petdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.R
import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.model.ScheduleDto
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.SampleAwarePetRepository
import com.papb.petto.data.repository.ScheduleRepository
import com.papb.petto.data.repository.SampleAwareScheduleRepository
import com.papb.petto.data.sample.SampleData
import com.papb.petto.util.ScheduleDates
import com.papb.petto.util.executeCatching
import com.papb.petto.util.formatPetAge
import com.papb.petto.util.formatPetWeight
import com.papb.petto.util.shortMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetDetailViewModel(
    private val petRepository: PetRepository = SampleAwarePetRepository(),
    private val scheduleRepository: ScheduleRepository = SampleAwareScheduleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetDetailState())
    val uiState: StateFlow<PetDetailState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadPetDetails(petId: String?) {
        loadJob?.cancel()

        if (petId == null) {
            _uiState.value = PetDetailState(errorMessage = "Pet not selected")
            return
        }

        if (_uiState.value.petId != petId) {
            _uiState.value = PetDetailState(petId = petId, isLoading = true)
        } else {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        }

        loadJob = viewModelScope.launch {
            petRepository.getPetById(petId)
                .onSuccess { dto ->
                    if (dto == null) {
                        _uiState.update { it.copy(errorMessage = "Pet not found") }
                    } else {
                        _uiState.update { it.applyPet(dto) }
                    }
                }
                .onFailure { e ->
                    Log.e("PetDetailViewModel", "Gagal memuat pet $petId", e)
                    _uiState.update { it.copy(errorMessage = e.shortMessage("Failed to load pet")) }
                }
            loadSchedules(petId)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadSchedules(petId: String) {
        scheduleRepository.getSchedulesForPet(petId)
            .onSuccess { dtos ->
                val upcoming = dtos
                    .filter { it.safeStatus != "done" }
                    .sortedBy { ScheduleDates.parse(it.safeNextDueDate)?.toEpochDay() ?: Long.MAX_VALUE }
                    .map { it.toScheduleItem() }
                val past = dtos
                    .filter { it.safeStatus == "done" }
                    .map {
                        PastRecordItem(
                            id = it.id,
                            title = it.careName,
                            date = it.safeLastDoneDate,
                            location = it.safeNotes.ifBlank { "No notes" }
                        )
                    }
                _uiState.update { it.copy(healthSchedules = upcoming, pastRecords = past) }
            }
            .onFailure { e ->
                Log.e("PetDetailViewModel", "Gagal memuat jadwal", e)
                _uiState.update { it.copy(errorMessage = e.shortMessage("Failed to load schedules")) }
            }
    }

    fun onDeleteScheduleClicked(schedule: HealthScheduleItem) {
        _uiState.update { it.copy(scheduleToDelete = schedule, showDeleteDialog = true) }
    }

    fun confirmDeleteSchedule() {
        val toDelete = _uiState.value.scheduleToDelete ?: return
        val petId = _uiState.value.petId
        _uiState.update { it.copy(showDeleteDialog = false, scheduleToDelete = null) }
        viewModelScope.launch {
            val failure = executeCatching { scheduleRepository.deleteSchedule(toDelete.id) }
            if (failure != null) {
                Log.e("PetDetailViewModel", "Hapus jadwal gagal", failure)
                _uiState.update { it.copy(errorMessage = failure.shortMessage("Failed to delete schedule")) }
            } else {
                loadSchedules(petId)
            }
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, scheduleToDelete = null) }
    }

    fun onMarkAsDoneClicked(scheduleId: String) {
        val petId = _uiState.value.petId
        viewModelScope.launch {
            val failure = executeCatching { scheduleRepository.markScheduleAsDone(scheduleId) }
            if (failure != null) {
                Log.e("PetDetailViewModel", "Tandai selesai gagal", failure)
                _uiState.update { it.copy(errorMessage = failure.shortMessage("Failed to update schedule")) }
            } else {
                loadSchedules(petId)
            }
        }
    }

    private fun PetDetailState.applyPet(dto: PetDto): PetDetailState {
        val speciesLabel = PetSpecies.fromDb(dto.species.lowercase()).label
        return copy(
            petId = dto.id,
            petName = dto.name,
            speciesBreed = if (dto.safeBreed.isBlank()) speciesLabel else "$speciesLabel • ${dto.safeBreed}",
            ageText = formatPetAge(dto.safeAgeMonths.toIntOrNull() ?: 0),
            weightText = formatPetWeight(dto.safeWeightKg),
            reproductiveText = if (dto.safeIsSpayedNeutered) "Neutered" else "Intact",
            isFemale = !dto.safeGender.equals("male", ignoreCase = true),
            imageRes = SampleData.imageResFor(dto.id) ?: R.drawable.ic_pet_default,
            errorMessage = null
        )
    }

    private fun ScheduleDto.toScheduleItem(): HealthScheduleItem {
        val overdue = safeStatus.equals("overdue", ignoreCase = true) ||
            ScheduleDates.isBeforeToday(safeNextDueDate)
        return HealthScheduleItem(
            id = id,
            title = careName,
            date = safeNextDueDate,
            location = safeNotes.ifBlank { "No notes" },
            status = if (overdue) "overdue" else safeStatus,
            isOverdue = overdue
        )
    }
}
