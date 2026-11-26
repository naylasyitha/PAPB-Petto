package com.papb.petto.ui.petform

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.PetRepositoryImpl
import com.papb.petto.ui.addpet.GenderOption
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetFormViewModel(
    private val petRepository: PetRepository = PetRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetFormState())
    val uiState: StateFlow<PetFormState> = _uiState.asStateFlow()

    private val _successEvent = Channel<Unit>(Channel.BUFFERED)
    val successEvent = _successEvent.receiveAsFlow()

    private var loadedPet: PetDto? = null

    fun resetFormState() {
        _uiState.update { it.copy(isSuccess = false, errorMessage = null, isLoading = false) }
    }

    fun setupForm(isEditMode: Boolean, petId: String? = null) {
        loadedPet = null
        while (_successEvent.tryReceive().isSuccess) { /* drain */ }

        if (!isEditMode) {
            _uiState.value = PetFormState(isEditMode = false)
            return
        }

        if (petId == null) {
            _uiState.value = PetFormState(isEditMode = true, errorMessage = "Pet not selected")
            return
        }

        _uiState.value = PetFormState(isEditMode = true, petId = petId, isLoading = true)
        viewModelScope.launch {
            petRepository.getPetById(petId)
                .onSuccess { dto ->
                    if (dto == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Pet not found") }
                    } else {
                        loadedPet = dto
                        _uiState.value = dto.toFormState()
                    }
                }
                .onFailure { e ->
                    Log.e("PetFormViewModel", "Gagal memuat pet $petId", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load pet")
                    }
                }
        }
    }

    fun onPetNameChange(name: String) {
        _uiState.update { it.copy(petName = name, errorMessage = null) }
    }

    fun onSpeciesSelected(species: String) {
        _uiState.update { it.copy(selectedSpecies = species, isDropdownExpanded = false, errorMessage = null) }
    }

    fun onDropdownExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(isDropdownExpanded = expanded) }
    }

    fun onBreedChange(breed: String) {
        _uiState.update { it.copy(breed = breed, errorMessage = null) }
    }

    fun onGenderChange(gender: GenderOption) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onAgeChange(age: String) {
        _uiState.update { it.copy(ageMonths = age.filter { c -> c.isDigit() }, errorMessage = null) }
    }

    fun onWeightChange(weight: String) {
        val filtered = weight.filter { c -> c.isDigit() || c == '.' }
        if (filtered.count { it == '.' } > 1) return
        _uiState.update { it.copy(weightKg = filtered, errorMessage = null) }
    }

    fun onSpayedNeuteredChange(spayed: Boolean) {
        _uiState.update { it.copy(isSpayedNeutered = spayed) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onSubmitClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        if (state.petName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter pet name") }
            return
        }
        if (state.selectedSpecies.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please select species") }
            return
        }

        val speciesDb = PetSpecies.entries
            .firstOrNull { it.label == state.selectedSpecies }
            ?.dbValue ?: PetSpecies.OTHER.dbValue
        val genderDb = state.gender.name.lowercase()
        val ageDb = state.ageMonths.ifBlank { "0" }
        val weightDb = state.weightKg.ifBlank { "0" }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result: Result<Unit> = if (state.isEditMode) {
                val base = loadedPet
                if (base == null) {
                    Result.failure(IllegalStateException("Pet data is not loaded yet"))
                } else {
                    petRepository.updatePet(
                        base.copy(
                            name = state.petName.trim(),
                            species = speciesDb,
                            breed = state.breed.trim(),
                            gender = genderDb,
                            ageMonths = ageDb,
                            weightKg = weightDb,
                            isSpayedNeutered = state.isSpayedNeutered,
                            notes = state.notes.trim()
                        )
                    )
                }
            } else {
                petRepository.addPet(
                    PetDto(
                        name = state.petName.trim(),
                        species = speciesDb,
                        breed = state.breed.trim(),
                        gender = genderDb,
                        ageMonths = ageDb,
                        weightKg = weightDb,
                        isSpayedNeutered = state.isSpayedNeutered,
                        notes = state.notes.trim()
                    )
                )
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _successEvent.trySend(Unit)
                }
                .onFailure { e ->
                    Log.e("PetFormViewModel", "Simpan pet gagal", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Failed to save pet")
                    }
                }
        }
    }

    private fun PetDto.toFormState() = PetFormState(
        isEditMode = true,
        petId = id,
        petName = name,
        selectedSpecies = PetSpecies.fromDb(species.lowercase()).label,
        breed = breed,
        gender = if (gender.equals("male", ignoreCase = true)) GenderOption.MALE else GenderOption.FEMALE,
        ageMonths = ageMonths,
        weightKg = weightKg,
        isSpayedNeutered = isSpayedNeutered,
        notes = notes
    )
}
