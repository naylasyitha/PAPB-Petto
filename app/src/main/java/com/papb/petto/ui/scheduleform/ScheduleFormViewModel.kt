package com.papb.petto.ui.scheduleform

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.ScheduleDto
import com.papb.petto.data.repository.ScheduleRepository
import com.papb.petto.data.repository.SampleAwareScheduleRepository
import com.papb.petto.util.NotificationHelper
import com.papb.petto.util.ScheduleDates
import com.papb.petto.util.executeCatching
import com.papb.petto.util.shortMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleFormViewModel(
    private val scheduleRepository: ScheduleRepository = SampleAwareScheduleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleFormState())
    val uiState: StateFlow<ScheduleFormState> = _uiState.asStateFlow()

    private val _successEvent = Channel<Unit>(Channel.BUFFERED)
    val successEvent = _successEvent.receiveAsFlow()

    private var loadedSchedule: ScheduleDto? = null
    private var setupJob: Job? = null

    fun setupForm(isEditMode: Boolean, petId: String?, scheduleId: String?) {
        setupJob?.cancel()
        loadedSchedule = null
        while (_successEvent.tryReceive().isSuccess) { /* drain */ }

        if (!isEditMode) {
            _uiState.value = withDates(
                ScheduleFormState(petId = petId, lastDoneDate = ScheduleDates.today())
            )
            return
        }

        if (petId == null || scheduleId == null) {
            _uiState.value = ScheduleFormState(isEditMode = true, errorMessage = "Schedule not selected")
            return
        }

        _uiState.value = ScheduleFormState(
            isEditMode = true,
            petId = petId,
            scheduleId = scheduleId,
            isLoading = true
        )
        setupJob = viewModelScope.launch {
            scheduleRepository.getSchedulesForPet(petId)
                .onSuccess { list ->
                    val dto = list.firstOrNull { it.id == scheduleId }
                    if (dto == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Schedule not found") }
                    } else {
                        loadedSchedule = dto
                        _uiState.value = withDates(dto.toFormState(petId))
                    }
                }
                .onFailure { e ->
                    Log.e("ScheduleFormViewModel", "Gagal memuat jadwal", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.shortMessage("Failed to load schedule"))
                    }
                }
        }
    }

    fun onCareTypeChange(type: CareTypeOption) {
        _uiState.update { it.copy(careType = type) }
    }

    fun onCareNameChange(name: String) {
        _uiState.update { it.copy(careName = name, errorMessage = null) }
    }

    fun onLastDoneDateChange(date: String) {
        _uiState.update { withDates(it.copy(lastDoneDate = date, errorMessage = null)) }
    }

    fun onIntervalValueChange(value: String) {
        _uiState.update {
            withDates(it.copy(repeatIntervalValue = value.filter { c -> c.isDigit() }.take(3)))
        }
    }

    fun onIntervalUnitSelected(unit: String) {
        _uiState.update {
            withDates(it.copy(repeatIntervalUnit = unit, isIntervalDropdownExpanded = false))
        }
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

    private fun withDates(state: ScheduleFormState): ScheduleFormState {
        val last = ScheduleDates.parse(state.lastDoneDate)
        val value = state.repeatIntervalValue.toIntOrNull()
        if (last == null || value == null || value <= 0) {
            return state.copy(
                nextDueDate = "-",
                calculationFormulaText = "Pick a last done date and a repeat interval to calculate.",
                remind3DaysText = "",
                remind1DayText = ""
            )
        }
        val next = ScheduleDates.addInterval(last, value, state.repeatIntervalUnit)
        return state.copy(
            nextDueDate = ScheduleDates.format(next),
            calculationFormulaText =
                "Calculated from Last Done Date + $value ${state.repeatIntervalUnit.lowercase()} interval.",
            remind3DaysText = "Push alert on ${ScheduleDates.format(ScheduleDates.minusDays(next, 3))}",
            remind1DayText = "Push alert on ${ScheduleDates.format(ScheduleDates.minusDays(next, 1))}"
        )
    }

    fun onSaveScheduleClicked(context: Context) {
        val state = _uiState.value
        if (state.isLoading) return

        val petId = state.petId
        if (petId == null) {
            _uiState.update { it.copy(errorMessage = "Pet not selected") }
            return
        }
        if (state.careName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter care name") }
            return
        }
        val lastDone = ScheduleDates.parse(state.lastDoneDate)
        if (lastDone == null) {
            _uiState.update { it.copy(errorMessage = "Please pick the last done date") }
            return
        }
        val interval = state.repeatIntervalValue.toIntOrNull()?.takeIf { it > 0 }
        if (interval == null) {
            _uiState.update { it.copy(errorMessage = "Repeat interval must be at least 1") }
            return
        }

        val appContext = context.applicationContext

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val dto = ScheduleDto(
                id = state.scheduleId ?: java.util.UUID.randomUUID().toString(),
                petId = petId,
                careType = state.careType.name,
                careName = state.careName.trim(),
                lastDoneDate = ScheduleDates.format(lastDone),
                repeatIntervalValue = interval,
                repeatIntervalUnit = state.repeatIntervalUnit,
                nextDueDate = state.nextDueDate,
                remind3Days = state.remind3DaysBefore,
                remind1Day = state.remind1DayBefore,
                notes = state.notes.trim(),
                status = loadedSchedule?.status ?: "upcoming"
            )

            val failure = executeCatching {
                if (state.isEditMode) scheduleRepository.updateSchedule(dto)
                else scheduleRepository.addSchedule(dto)
            }

            if (failure != null) {
                Log.e("ScheduleFormViewModel", "Simpan jadwal gagal", failure)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = failure.shortMessage("Failed to save schedule"))
                }
                return@launch
            }

            runCatching {
                NotificationHelper.sendCareReminderNotification(
                    context = appContext,
                    title = "🐾 Petto Schedule Saved: ${state.careName.trim()}",
                    message = "Reminder set for ${state.nextDueDate}. " +
                        "${if (state.remind3DaysBefore) "H-3 & " else ""}" +
                        "${if (state.remind1DayBefore) "H-1 " else ""}alerts active!"
                )
            }

            _uiState.update { it.copy(isLoading = false) }
            _successEvent.trySend(Unit)
        }
    }

    private fun ScheduleDto.toFormState(petId: String) = ScheduleFormState(
        isEditMode = true,
        petId = petId,
        scheduleId = id,
        careType = runCatching { CareTypeOption.valueOf(careType.uppercase()) }
            .getOrDefault(CareTypeOption.OTHER),
        careName = careName,
        lastDoneDate = lastDoneDate,
        repeatIntervalValue = repeatIntervalValue.toString(),
        repeatIntervalUnit = repeatIntervalUnit,
        remind3DaysBefore = remind3Days,
        remind1DayBefore = remind1Day,
        notes = notes
    )
}
