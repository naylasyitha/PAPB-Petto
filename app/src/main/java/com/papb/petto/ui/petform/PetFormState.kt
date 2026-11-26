package com.papb.petto.ui.petform

import com.papb.petto.R
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.ui.addpet.GenderOption

data class PetFormState(
    val isEditMode: Boolean = false,
    val petId: String? = null,
    val petName: String = "",
    val selectedSpecies: String = "",
    val speciesOptions: List<String> = PetSpecies.entries.map { it.label },
    val breed: String = "",
    val gender: GenderOption = GenderOption.FEMALE,
    val ageMonths: String = "",
    val weightKg: String = "",
    val isSpayedNeutered: Boolean = false,
    val notes: String = "",
    val imageRes: Int = R.drawable.ic_pet_default,
    val isDropdownExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
