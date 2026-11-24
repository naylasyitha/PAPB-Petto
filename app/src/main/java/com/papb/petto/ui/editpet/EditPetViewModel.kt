package com.papb.petto.ui.editpet

import androidx.lifecycle.ViewModel
import com.papb.petto.ui.addpet.GenderOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditPetViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditPetState())
    val uiState: StateFlow<EditPetState> = _uiState.asStateFlow()

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
        _uiState.update { it.copy(ageMonths = age, errorMessage = null) }
    }

    fun onWeightChange(weight: String) {
        _uiState.update { it.copy(weightKg = weight, errorMessage = null) }
    }

    fun onSpayedNeuteredChange(spayed: Boolean) {
        _uiState.update { it.copy(isSpayedNeutered = spayed) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onSaveChangesClicked() {
        val state = _uiState.value
        if (state.petName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter pet name") }
            return
        }
        _uiState.update { it.copy(isSuccess = true) }
    }
}
