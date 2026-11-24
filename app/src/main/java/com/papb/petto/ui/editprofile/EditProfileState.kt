package com.papb.petto.ui.editprofile

import com.papb.petto.R

data class EditProfileState(
    val fullName: String = "Anasya Laili",
    val email: String = "anasyalaili@gmail.com",
    val phoneNumber: String = "812-3456-7890",
    val homeAddressTitle: String = "Kebayoran Baru",
    val homeAddressSubtitle: String = "South Jakarta, DKI Jakarta",
    val alertRadiusKm: Float = 3f, // 1 km to 5 km
    val avatarRes: Int = R.drawable.user_profile,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
