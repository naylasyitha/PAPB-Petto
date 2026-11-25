package com.papb.petto.ui.missing.report

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.repository.LocationRepository
import com.papb.petto.data.repository.MissingReportRepository
import com.papb.petto.data.repository.MissingReportRepositoryImpl
import com.papb.petto.data.repository.NewMissingReport
import com.papb.petto.data.repository.ProfileRepository
import com.papb.petto.data.repository.ProfileRepositoryImpl
import com.papb.petto.util.FormatUtils
import com.papb.petto.util.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class ReportMissingViewModel @JvmOverloads constructor(
    application: Application,
    private val reportRepository: MissingReportRepository = MissingReportRepositoryImpl(),
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl(),
    private val locationRepository: LocationRepository = LocationRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ReportMissingState())
    val uiState: StateFlow<ReportMissingState> = _uiState.asStateFlow()

    /** Titik awal peta saat memilih lokasi: alamat rumah di profile (jika ada), selain itu Jakarta. */
    var fallbackLat: Double = DEFAULT_LAT
        private set
    var fallbackLng: Double = DEFAULT_LNG
        private set

    init {
        viewModelScope.launch {
            profileRepository.getMyProfile().onSuccess { profile ->
                profile?.let {
                    if (it.homeLat != null && it.homeLng != null) {
                        fallbackLat = it.homeLat
                        fallbackLng = it.homeLng
                    }
                    it.phone?.let { phone ->
                        _uiState.update { s ->
                            if (s.phoneDigits.isEmpty()) {
                                s.copy(phoneDigits = FormatUtils.phoneDigitsForField(phone))
                            } else s
                        }
                    }
                }
            }
        }
        useCurrentLocation()
    }

    // Field handlers

    fun onPhotosPicked(uris: List<Uri>) {
        _uiState.update {
            val merged = (it.photoUris + uris).distinct().take(ReportMissingState.MAX_PHOTOS)
            it.copy(photoUris = merged, errorMessage = null)
        }
    }

    fun onRemovePhoto(index: Int) {
        _uiState.update { it.copy(photoUris = it.photoUris.filterIndexed { i, _ -> i != index }) }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onSpeciesChange(value: PetSpecies) = _uiState.update { it.copy(species = value, errorMessage = null) }
    fun onBreedChange(value: String) = _uiState.update { it.copy(breed = value, errorMessage = null) }

    fun onDescriptionChange(value: String) = _uiState.update {
        it.copy(description = value.take(ReportMissingState.MAX_DESCRIPTION))
    }

    fun onPhoneChange(value: String) = _uiState.update {
        it.copy(phoneDigits = value.filter { c -> c.isDigit() }.take(13), errorMessage = null)
    }

    fun onRewardChange(value: String) = _uiState.update {
        it.copy(rewardDigits = value.filter { c -> c.isDigit() }.take(10))
    }

    fun onDateSelected(millisUtc: Long) = _uiState.update { it.copy(dateMillisUtc = millisUtc, errorMessage = null) }
    fun onTimeSelected(hour: Int, minute: Int) = _uiState.update { it.copy(hour = hour, minute = minute, errorMessage = null) }

    // Location

    fun useCurrentLocation() {
        viewModelScope.launch {
            val point = locationRepository.getCurrentLocation() ?: return@launch
            onLocationPicked(point.lat, point.lng)
        }
    }

    fun onLocationPicked(lat: Double, lng: Double) {
        _uiState.update { it.copy(pickedLat = lat, pickedLng = lng, isLocating = true, errorMessage = null) }
        viewModelScope.launch {
            val place = locationRepository.reverseGeocode(lat, lng)
            _uiState.update {
                it.copy(
                    isLocating = false,
                    areaTitle = place?.title ?: "Selected area",
                    areaSubtitle = place?.subtitle ?: "",
                    regionName = place?.regionName ?: "Unknown area"
                )
            }
        }
    }

    // Submit

    fun onSubmitClicked() {
        val s = _uiState.value
        if (s.isSubmitting) return

        val phone = FormatUtils.normalizeIndonesianPhone(s.phoneDigits)
        val lastSeen = buildLastSeenInstant(s)
        val error = when {
            s.photoUris.isEmpty() -> "Please add at least 1 photo"
            s.name.isBlank() -> "Pet name is required"
            s.species == null -> "Please choose a species"
            s.breed.isBlank() -> "Breed is required"
            s.pickedLat == null || s.pickedLng == null -> "Please choose where your pet was last seen"
            s.isLocating -> "Still detecting the area, please wait a moment"
            lastSeen == null -> "Please set the date and time last seen"
            lastSeen.isAfter(Instant.now().plusSeconds(60)) -> "Last seen time can't be in the future"
            phone == null -> "Please enter a valid phone number"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val photos = try {
                s.photoUris.map { ImageCompressor.compress(getApplication(), it) }
            } catch (e: Exception) {
                fail("Could not read one of the photos. Please pick it again.")
                return@launch
            }

            val paths = reportRepository.uploadPhotos(photos).getOrElse { e ->
                fail(e.localizedMessage ?: "Photo upload failed")
                return@launch
            }

            reportRepository.createReport(
                NewMissingReport(
                    name = s.name.trim(),
                    species = s.species!!,
                    breed = s.breed.trim(),
                    description = s.description.trim().ifBlank { null },
                    photoPaths = paths,
                    lat = s.pickedLat!!,
                    lng = s.pickedLng!!,
                    regionName = s.regionName.ifBlank { "Unknown area" },
                    lastSeenAt = lastSeen!!,
                    phone = phone!!,
                    rewardIdr = s.rewardDigits.toLongOrNull()?.takeIf { it > 0 }
                )
            ).onSuccess {
                _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
            }.onFailure { e ->
                fail(e.localizedMessage ?: "Failed to upload report")
            }
        }
    }

    private fun fail(message: String) {
        _uiState.update { it.copy(isSubmitting = false, errorMessage = message) }
    }

    private fun buildLastSeenInstant(s: ReportMissingState): Instant? {
        val millis = s.dateMillisUtc ?: return null
        val hour = s.hour ?: return null
        val minute = s.minute ?: return null
        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
        return LocalDateTime.of(date, java.time.LocalTime.of(hour, minute))
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    private companion object {
        const val DEFAULT_LAT = -6.2088
        const val DEFAULT_LNG = 106.8456
    }
}
