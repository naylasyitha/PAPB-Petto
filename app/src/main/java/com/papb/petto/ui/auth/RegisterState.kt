package com.papb.petto.ui.auth

data class RegisterState(
    val fullName: String = "",
    val whatsappNumber: String = "",
    val email: String = "",
    val password: String = "",
    val isAgreeTerms: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
