package com.papb.petto.ui.editprofile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.repository.ProfileRepository
import com.papb.petto.data.repository.ProfileRepositoryImpl
import com.papb.petto.util.ImageCompressor
import com.papb.petto.util.shortMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class EditProfileViewModel @JvmOverloads constructor(
    application: Application,
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl()
) : AndroidViewModel(application) {

    private companion object {
        const val NO_ADDRESS = "No address set"

        const val DUMMY_LAT = -6.2441
        const val DUMMY_LNG = 106.7993
    }

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccessEvent = _saveSuccess.receiveAsFlow()

    private var pendingHomeLat: Double? = null
    private var pendingHomeLng: Double? = null

    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, errorMessage = null, isLoading = false) }
    }

    fun loadProfile() {
        pendingHomeLat = null
        pendingHomeLng = null
        viewModelScope.launch {
            val email = profileRepository.currentUserEmail().orEmpty()
            _uiState.update { it.copy(avatarUri = null, avatarUrl = null, isLoading = true, errorMessage = null) }
            profileRepository.getMyProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            fullName = profile?.fullName.orEmpty(),
                            email = email,
                            phoneNumber = cleanPhone(profile?.phone.orEmpty()),
                            homeAddressTitle = profile?.homeTitle ?: NO_ADDRESS,
                            homeAddressSubtitle = profile?.homeSubtitle.orEmpty(),
                            alertRadiusKm = (profile?.alertRadiusKm ?: 2).toFloat().coerceIn(1f, 5f),
                            avatarUrl = profile?.avatarUrl,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("EditProfileViewModel", "Gagal memuat profil", e)
                    _uiState.update {
                        it.copy(
                            email = email,
                            isLoading = false,
                            errorMessage = e.shortMessage("Failed to load profile")
                        )
                    }
                }
        }
    }

    fun onPhotoPicked(uri: Uri) {
        _uiState.update { it.copy(avatarUri = uri, errorMessage = null) }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPhoneNumberChange(phone: String) {
        _uiState.update { it.copy(phoneNumber = cleanPhone(phone), errorMessage = null) }
    }

    fun onAlertRadiusChange(radius: Float) {
        _uiState.update { it.copy(alertRadiusKm = radius) }
    }

    fun onUseCurrentLocationClicked() {
        pendingHomeLat = DUMMY_LAT
        pendingHomeLng = DUMMY_LNG
        _uiState.update {
            it.copy(
                homeAddressTitle = "Kebayoran Baru",
                homeAddressSubtitle = "South Jakarta, DKI Jakarta"
            )
        }
    }

    fun onSaveChangesClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name cannot be empty") }
            return
        }

        val homeLat = pendingHomeLat
        val homeLng = pendingHomeLng

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            var result = profileRepository.saveProfile(
                fullName = state.fullName.trim(),
                phone = cleanPhone(state.phoneNumber).let { if (it.isEmpty()) "" else "0$it" },
                alertRadiusKm = state.alertRadiusKm.roundToInt()
            )
            if (result.isSuccess && homeLat != null && homeLng != null) {
                result = profileRepository.saveHome(
                    title = state.homeAddressTitle,
                    subtitle = state.homeAddressSubtitle,
                    lat = homeLat,
                    lng = homeLng
                )
            }

            val photoUri = state.avatarUri
            if (result.isSuccess && photoUri != null) {
                result = try {
                    profileRepository.uploadAvatar(ImageCompressor.compress(getApplication(), photoUri))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

            result
                .onSuccess {
                    pendingHomeLat = null
                    pendingHomeLng = null
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    _saveSuccess.trySend(Unit)
                }
                .onFailure { e ->
                    Log.e("EditProfileViewModel", "Simpan profil gagal", e)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.shortMessage("Failed to save profile"))
                    }
                }
        }
    }
}

/**
 * Field sudah menampilkan +62, jadi buang 0 / 62 di depan dan karakter selain angka.
 * "081234567890" -> "81234567890". Saat disimpan ditambah 0 lagi (sama seperti format Register).
 */
private fun cleanPhone(raw: String): String {
    var digits = raw.filter { it.isDigit() }
    if (digits.startsWith("62")) digits = digits.drop(2)
    return digits.trimStart('0').take(12)
}
