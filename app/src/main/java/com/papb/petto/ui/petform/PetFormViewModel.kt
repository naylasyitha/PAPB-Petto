package com.papb.petto.ui.petform

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.R
import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.SampleAwarePetRepository
import com.papb.petto.data.sample.SampleData
import com.papb.petto.ui.addpet.GenderOption
import com.papb.petto.util.ImageCompressor
import com.papb.petto.util.shortMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetFormViewModel @JvmOverloads constructor(
    application: Application,
    private val petRepository: PetRepository = SampleAwarePetRepository()
) : AndroidViewModel(application) {

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
                        it.copy(isLoading = false, errorMessage = e.shortMessage("Failed to load pet"))
                    }
                }
        }
    }

    fun onPhotoPicked(uri: Uri) {
        _uiState.update { it.copy(photoUri = uri, errorMessage = null) }
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

            val base = loadedPet
            if (state.isEditMode && base == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Pet data is not loaded yet") }
                return@launch
            }

            val pet = (base.takeIf { state.isEditMode } ?: PetDto()).copy(
                name = state.petName.trim(),
                species = speciesDb,
                breed = state.breed.trim(),
                gender = genderDb,
                ageMonths = ageDb,
                weightKg = weightDb,
                isSpayedNeutered = state.isSpayedNeutered,
                notes = state.notes.trim()
            )

            // Foto di-upload dulu, supaya URL-nya ikut tersimpan bersama data hewan.
            val photoUri = state.photoUri
            val imageUrl = if (photoUri == null) {
                pet.imageUrl
            } else {
                val upload = try {
                    petRepository.uploadPhoto(pet.id, ImageCompressor.compress(getApplication(), photoUri))
                } catch (e: Exception) {
                    Result.failure(e)
                }
                upload.getOrElse { e ->
                    Log.e("PetFormViewModel", "Upload foto gagal", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.shortMessage("Failed to upload photo"))
                    }
                    return@launch
                }
            }

            val finalPet = pet.copy(imageUrl = imageUrl)
            val result: Result<Unit> = if (state.isEditMode) {
                petRepository.updatePet(finalPet)
            } else {
                petRepository.addPet(finalPet)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _successEvent.trySend(Unit)
                }
                .onFailure { e ->
                    Log.e("PetFormViewModel", "Simpan pet gagal", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.shortMessage("Failed to save pet"))
                    }
                }
        }
    }

    private fun PetDto.toFormState() = PetFormState(
        isEditMode = true,
        petId = id,
        petName = name,
        selectedSpecies = PetSpecies.fromDb(species.lowercase()).label,
        breed = safeBreed,
        gender = if (safeGender.equals("male", ignoreCase = true)) GenderOption.MALE else GenderOption.FEMALE,
        ageMonths = safeAgeMonths,
        weightKg = safeWeightKg,
        isSpayedNeutered = safeIsSpayedNeutered,
        notes = safeNotes,
        imageRes = SampleData.imageResFor(id) ?: R.drawable.ic_pet_default,
        imageUrl = imageUrl
    )
}
