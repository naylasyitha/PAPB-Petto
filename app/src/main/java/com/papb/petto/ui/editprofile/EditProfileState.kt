package com.papb.petto.ui.editprofile

import android.net.Uri
import com.papb.petto.R

data class EditProfileState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val homeAddressTitle: String = "No address set",
    val homeAddressSubtitle: String = "",
    val alertRadiusKm: Float = 3f, // 1 km to 5 km
    val avatarRes: Int = R.drawable.user_profile,
    val avatarUri: Uri? = null, // foto yang baru dipilih dari galeri (belum disimpan)
    val avatarUrl: String? = null, // foto yang sudah tersimpan di akun
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
