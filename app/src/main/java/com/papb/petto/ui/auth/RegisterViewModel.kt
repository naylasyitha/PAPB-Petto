package com.papb.petto.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.repository.AuthRepository
import com.papb.petto.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private companion object {
        const val MAX_NAME = 50
        const val MAX_WA = 13
        const val MIN_WA = 9
        const val MAX_EMAIL = 50
        const val MAX_PASSWORD = 20
        const val MIN_PASSWORD = 6
        val GMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$", RegexOption.IGNORE_CASE)
    }

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    private val _registerSuccess = Channel<Unit>(Channel.BUFFERED)
    val registerSuccessEvent = _registerSuccess.receiveAsFlow()

    fun clearForm() {
        _uiState.value = RegisterState()
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name.take(MAX_NAME), errorMessage = null) }
    }

    fun onWhatsappChange(wa: String) {
        _uiState.update {
            it.copy(whatsappNumber = wa.filter { c -> c.isDigit() }.take(MAX_WA), errorMessage = null)
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email.filter { c -> !c.isWhitespace() }.take(MAX_EMAIL), errorMessage = null)
        }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(password = pass.take(MAX_PASSWORD), errorMessage = null) }
    }

    fun onAgreeTermsChange(agreed: Boolean) {
        _uiState.update { it.copy(isAgreeTerms = agreed, errorMessage = null) }
    }

    fun onRegisterClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        if (state.fullName.isBlank() || state.whatsappNumber.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields must be filled") }
            return
        }
        if (state.whatsappNumber.length < MIN_WA) {
            _uiState.update { it.copy(errorMessage = "WhatsApp number must be $MIN_WA-$MAX_WA digits") }
            return
        }
        if (!GMAIL_REGEX.matches(state.email.trim())) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address (e.g. name@domain.com)") }
            return
        }
        if (state.password.length < MIN_PASSWORD) {
            _uiState.update { it.copy(errorMessage = "Password must be at least $MIN_PASSWORD characters") }
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
                email = state.email.trim().lowercase(),
                pass = state.password
            )

            if (result.isSuccess) {
                runCatching { authRepository.logout() }
                _uiState.value = RegisterState()
                _registerSuccess.trySend(Unit)
            } else {
                val exception = result.exceptionOrNull()
                Log.e("RegisterViewModel", "Register gagal", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception?.localizedMessage
                            ?: "Registration failed. Please try again."
                    )
                }
            }
        }
    }
}
