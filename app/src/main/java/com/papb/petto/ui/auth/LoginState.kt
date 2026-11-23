package com.papb.petto.ui.auth

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isRememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)