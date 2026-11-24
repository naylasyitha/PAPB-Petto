package com.papb.petto.ui.addpet

enum class GenderOption {
    FEMALE,
    MALE
}

data class AddPetState(
    val petName: String = "",
    val selectedSpecies: String = "",
    val speciesOptions: List<String> = listOf("Dog", "Cat", "Bunny", "Bird", "Reptile", "Other"),
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
