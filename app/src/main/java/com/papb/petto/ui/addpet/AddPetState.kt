package com.papb.petto.ui.addpet

import com.papb.petto.data.model.PetSpecies

enum class GenderOption {
    FEMALE,
    MALE
}

data class AddPetState(
    val petName: String = "",
    val selectedSpecies: String = "",
    val speciesOptions: List<String> = PetSpecies.entries.map { it.label },
    val breed: String = "",
    val gender: GenderOption = GenderOption.FEMALE,
    val ageMonths: String = "",
    val weightKg: String = "",
    val isSpayedNeutered: Boolean = false,
    val notes: String = "",
    val isDropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
