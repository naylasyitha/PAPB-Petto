package com.papb.petto.ui.addpet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.PetRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPetViewModel(
    private val petRepository: PetRepository = PetRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPetState())
    val uiState: StateFlow<AddPetState> = _uiState.asStateFlow()

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

    fun onAddPetClicked() {
        val state = _uiState.value
        if (state.isLoading || state.isSuccess) return

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

        val pet = PetDto(
            name = state.petName.trim(),
            species = speciesDb,
            breed = state.breed.trim(),
            gender = state.gender.name.lowercase(),
            ageMonths = state.ageMonths.ifBlank { "0" },
            weightKg = state.weightKg.ifBlank { "0" },
            isSpayedNeutered = state.isSpayedNeutered,
            notes = state.notes.trim()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            petRepository.addPet(pet)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Failed to add pet")
                    }
                }
        }
    }

    fun onSuccessHandled() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
