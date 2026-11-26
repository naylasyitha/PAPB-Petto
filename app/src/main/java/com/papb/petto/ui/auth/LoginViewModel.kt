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
            it.copy(email = newEmail, errorMessage = null)
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
                        errorMessage = friendlyLoginError(exception)
                    )
                }
            }
        }
    }

    fun onGoogleLoginClicked() {
        // TODO: Integrasi Google Sign-In
    }

    private fun friendlyLoginError(e: Throwable): String {
        val msg = e.localizedMessage.orEmpty()
        return when {
            msg.contains("invalid login credentials", ignoreCase = true) ->
                "Wrong email or password."
            msg.isBlank() -> "Login failed. Please check your credentials."
            else -> msg
        }
    }
}
