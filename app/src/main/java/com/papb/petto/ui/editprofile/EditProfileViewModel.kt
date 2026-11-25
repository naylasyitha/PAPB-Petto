package com.papb.petto.ui.editprofile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPhoneNumberChange(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) }
    }

    fun onAlertRadiusChange(radius: Float) {
        _uiState.update { it.copy(alertRadiusKm = radius) }
    }

    fun onUseCurrentLocationClicked() {
        _uiState.update {
            it.copy(
                homeAddressTitle = "Kebayoran Baru",
                homeAddressSubtitle = "South Jakarta, DKI Jakarta"
            )
        }
    }

    fun onSaveChangesClicked() {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name and Email cannot be empty") }
            return
        }
        _uiState.update { it.copy(isSuccess = true) }
    }
}
