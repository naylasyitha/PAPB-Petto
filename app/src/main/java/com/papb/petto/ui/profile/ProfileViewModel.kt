package com.papb.petto.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.R
import com.papb.petto.data.model.PetDto
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.repository.AuthRepository
import com.papb.petto.data.repository.AuthRepositoryImpl
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.ProfileRepository
import com.papb.petto.data.repository.ProfileRepositoryImpl
import com.papb.petto.data.repository.SampleAwarePetRepository
import com.papb.petto.data.sample.SampleData
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.util.formatPetAge
import com.papb.petto.util.shortMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val petRepository: PetRepository = SampleAwarePetRepository(),
    private val userRepository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {

    private val settings = listOf(
        SettingMenuItem("Language", R.drawable.language_profile, "language"),
        SettingMenuItem("Help and Reports", R.drawable.help_profile, "help"),
        SettingMenuItem("Notifications", R.drawable.notif_profile, "notifications"),
        SettingMenuItem("About Petto", R.drawable.about_profile, "about")
    )

    // Snowie & Hotdog (pet contoh) selalu tampil di paling atas
    private fun samplePets() = SampleData.pets().map { it.toProfileItem() }

    private val _uiState = MutableStateFlow(ProfileState(pets = samplePets(), settings = settings))
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    // Event sekali pakai: tidak menempel di state (ViewModel ini Activity-scoped).
    private val _logoutEvent = Channel<Unit>(Channel.BUFFERED)
    val logoutEvent = _logoutEvent.receiveAsFlow()

    /** Nama (dari data register) dan email di header. Dipanggil setiap layar Profile tampil. */
    fun loadProfile() {
        viewModelScope.launch {
            val email = userRepository.currentUserEmail().orEmpty()
            userRepository.getMyProfile()
                .onSuccess { profile ->
                    val name = profile?.fullName?.takeIf { it.isNotBlank() }
                        ?: email.substringBefore("@")
                    _uiState.update { it.copy(userName = name, userEmail = email) }
                }
                .onFailure { e ->
                    Log.e("ProfileViewModel", "Gagal memuat profil", e)
                    _uiState.update {
                        it.copy(userName = email.substringBefore("@"), userEmail = email)
                    }
                }
        }
    }

    /** Daftar pet: contoh + dari Supabase. Dipanggil setiap layar Profile tampil. */
    fun loadPets() {
        // tampilkan pet contoh dulu (termasuk hasil edit terakhir), lalu gabung dengan data Supabase
        _uiState.update { it.copy(pets = samplePets()) }
        viewModelScope.launch {
            petRepository.getPetsForCurrentUser()
                .onSuccess { pets ->
                    _uiState.update {
                        it.copy(pets = pets.map { dto -> dto.toProfileItem() }, petsError = null)
                    }
                }
                .onFailure { e ->
                    Log.e("ProfileViewModel", "Gagal memuat daftar pet", e)
                    _uiState.update {
                        it.copy(petsError = e.shortMessage("Could not load your pets"))
                    }
                }
        }
    }

    fun onTabSelected(tab: BottomTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onLogoutClicked() {
        if (_uiState.value.isLoggingOut) return // cegah klik berulang
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            authRepository.logout()
                .onSuccess {
                    // bersihkan data user lama supaya tidak terlihat oleh akun berikutnya
                    _uiState.value = ProfileState(pets = samplePets(), settings = settings)
                    _logoutEvent.trySend(Unit)
                }
                .onFailure { e ->
                    Log.e("ProfileViewModel", "Logout gagal", e)
                    _uiState.update { it.copy(isLoggingOut = false) }
                }
        }
    }

    private fun PetDto.toProfileItem(): PetProfileItem {
        val speciesLabel = PetSpecies.fromDb(species.lowercase()).label
        return PetProfileItem(
            id = id,
            name = name,
            speciesBreed = if (breed.isBlank()) speciesLabel else "$speciesLabel • $breed",
            ageText = formatPetAge(ageMonths.toIntOrNull() ?: 0),
            imageRes = SampleData.imageResFor(id) ?: R.drawable.ic_pet_default
        )
    }
}
