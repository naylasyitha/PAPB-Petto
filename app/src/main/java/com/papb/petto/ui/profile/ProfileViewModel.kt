package com.papb.petto.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.R
import com.papb.petto.data.repository.AuthRepository
import com.papb.petto.data.repository.AuthRepositoryImpl
import com.papb.petto.ui.components.BottomTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileState(
            pets = listOf(
                PetProfileItem(
                    id = "1",
                    name = "Snowie",
                    speciesBreed = "Dog • Maltese",
                    ageText = "10 months",
                    imageRes = R.drawable.snowie
                ),
                PetProfileItem(
                    id = "2",
                    name = "Hotdog",
                    speciesBreed = "Dog • Golden Retriever",
                    ageText = "2 years 4 months",
                    imageRes = R.drawable.hotdog
                )
            ),
            settings = listOf(
                SettingMenuItem("Language", R.drawable.language_profile, "language"),
                SettingMenuItem("Help and Reports", R.drawable.help_profile, "help"),
                SettingMenuItem("Notifications", R.drawable.notif_profile, "notifications"),
                SettingMenuItem("About Petto", R.drawable.about_profile, "about")
            )
        )
    )
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun onTabSelected(tab: BottomTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            val result = authRepository.logout()
            result.onSuccess {
                _uiState.update { it.copy(isLoggingOut = false, isLoggedOut = true) }
            }.onFailure {
                _uiState.update { it.copy(isLoggingOut = false) }
            }
        }
    }
}
