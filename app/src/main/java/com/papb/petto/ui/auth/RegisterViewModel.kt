package com.papb.petto.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.repository.AuthRepository
import com.papb.petto.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onWhatsappChange(wa: String) {
        _uiState.update { it.copy(whatsappNumber = wa, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(password = pass, errorMessage = null) }
    }

    fun onAgreeTermsChange(agreed: Boolean) {
        _uiState.update { it.copy(isAgreeTerms = agreed, errorMessage = null) }
    }

    fun onRegisterClicked() {
        val state = _uiState.value
        if (state.fullName.isBlank() || state.whatsappNumber.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields must be filled") }
            return
        }

        if (!state.isAgreeTerms) {
            _uiState.update { it.copy(errorMessage = "You must agree to the Terms & Conditions") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(
                fullName = state.fullName.trim(),
                whatsapp = state.whatsappNumber.trim(),
                email = state.email.trim(),
                pass = state.password
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Registration failed. Please try again."
                    )
                }
            }
        }
    }
}
