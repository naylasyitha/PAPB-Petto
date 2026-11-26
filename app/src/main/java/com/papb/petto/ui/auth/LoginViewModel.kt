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

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private val _loginSuccess = Channel<Unit>(Channel.BUFFERED)
    val loginSuccessEvent = _loginSuccess.receiveAsFlow()

    fun clearForm() {
        _uiState.value = LoginState()
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(email = newEmail, errorMessage = null, infoMessage = null, showResendConfirmation = false)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onRememberMeChange(isRemembered: Boolean) {
        _uiState.update { it.copy(isRememberMe = isRemembered) }
    }

    fun onLoginClicked() {
        if (_uiState.value.isLoading) return

        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Email and password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email, password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, password = "") }
                _loginSuccess.trySend(Unit)
            }.onFailure { exception ->
                Log.e("LoginViewModel", "Login gagal", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = friendlyLoginError(exception),
                        showResendConfirmation = isEmailNotConfirmed(exception)
                    )
                }
            }
        }
    }

    fun onResendConfirmationClicked() {
        val email = _uiState.value.email.trim()
        if (email.isEmpty() || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            authRepository.resendConfirmationEmail(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            infoMessage = "Confirmation email sent to $email. Check your inbox (and spam)."
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("LoginViewModel", "Kirim ulang email konfirmasi gagal", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.localizedMessage ?: "Failed to send confirmation email"
                        )
                    }
                }
        }
    }

    fun onGoogleLoginClicked() {
        // TODO: Integrasi Google Sign-In
    }

    private fun isEmailNotConfirmed(e: Throwable): Boolean =
        e.localizedMessage.orEmpty().contains("not confirmed", ignoreCase = true)

    private fun friendlyLoginError(e: Throwable): String {
        val msg = e.localizedMessage.orEmpty()
        return when {
            msg.contains("not confirmed", ignoreCase = true) ->
                "Your email isn't confirmed yet. Open the confirmation link we sent to your inbox (check spam too), then log in again."
            msg.contains("invalid login credentials", ignoreCase = true) ->
                "Wrong email or password."
            msg.isBlank() -> "Login failed. Please check your credentials."
            else -> msg
        }
    }
}
