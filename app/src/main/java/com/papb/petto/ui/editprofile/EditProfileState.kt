package com.papb.petto.ui.editprofile

import com.papb.petto.R

data class EditProfileState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val homeAddressTitle: String = "No address set",
    val homeAddressSubtitle: String = "",
    val alertRadiusKm: Float = 3f, // 1 km to 5 km
    val avatarRes: Int = R.drawable.user_profile,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
