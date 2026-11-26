package com.papb.petto.ui.profile

import com.papb.petto.ui.components.BottomTab

data class PetProfileItem(
    val id: String,
    val name: String,
    val speciesBreed: String,
    val ageText: String,
    val imageRes: Int
)

data class SettingMenuItem(
    val title: String,
    val iconRes: Int,
    val onClickAction: String
)

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val currentTab: BottomTab = BottomTab.PROFILE,
    val pets: List<PetProfileItem> = listOf(),
    val petsError: String? = null,
    val settings: List<SettingMenuItem> = listOf(),
    val isLoggingOut: Boolean = false
)
