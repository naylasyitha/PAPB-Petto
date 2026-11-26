package com.papb.petto.ui.editpet

import com.papb.petto.R
import com.papb.petto.ui.addpet.GenderOption

data class EditPetState(
    val petId: String = "1",
    val petName: String = "Snowie",
    val selectedSpecies: String = "Dog",
    val speciesOptions: List<String> = listOf("Dog", "Cat", "Bunny", "Bird", "Reptile", "Other"),
    val breed: String = "Maltese",
    val gender: GenderOption = GenderOption.FEMALE,
    val ageMonths: String = "10 months",
    val weightKg: String = "2.1 kg",
    val isSpayedNeutered: Boolean = true,
    val notes: String = "Mild allergy to chicken, loves sweet potato treats.",
    val imageRes: Int = R.drawable.snowie,
    val isDropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
