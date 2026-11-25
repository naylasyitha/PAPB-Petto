package com.papb.petto.ui.missing

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.MissingReport
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.data.repository.LocationRepository
import com.papb.petto.data.repository.MissingReportRepository
import com.papb.petto.data.repository.MissingReportRepositoryImpl
import com.papb.petto.data.repository.ProfileRepository
import com.papb.petto.data.repository.ProfileRepositoryImpl
import com.papb.petto.util.PriorityAreaCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.math.ceil

class MissingPetsViewModel @JvmOverloads constructor(
    application: Application,
    private val reportRepository: MissingReportRepository = MissingReportRepositoryImpl(),
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl(),
    private val locationRepository: LocationRepository = LocationRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MissingPetsState())
    val uiState: StateFlow<MissingPetsState> = _uiState.asStateFlow()

    private var allReports: List<MissingReport> = emptyList()
    private var userLat: Double? = null
    private var userLng: Double? = null

    private val prefs = application.getSharedPreferences("petto_prefs", 0)

    init {
        if (!locationRepository.hasPermission() && !prefs.getBoolean(KEY_LOCATION_ASKED, false)) {
            _uiState.update { it.copy(showLocationDialog = true) }
        }
    }

    /** Dipanggil tiap layar Missing tampil, ambil lokasi jika diizinkan, lalu muat ulang laporan. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            fetchUserLocation()
            reportRepository.getActiveReports()
                .onSuccess { reports ->
                    allReports = reports
                    recompute(resetPage = false)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.localizedMessage ?: "Failed to load missing pets"
                        )
                    }
                }
        }
    }

    private suspend fun fetchUserLocation() {
        val point = locationRepository.getCurrentLocation() ?: return
        userLat = point.lat
        userLng = point.lng
        profileRepository.updateMonitoredLocation(point.lat, point.lng)
    }

    // Location permission dialog

    fun onLocationDialogDismissed() {
        prefs.edit().putBoolean(KEY_LOCATION_ASKED, true).apply()
        _uiState.update { it.copy(showLocationDialog = false) }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        prefs.edit().putBoolean(KEY_LOCATION_ASKED, true).apply()
        _uiState.update { it.copy(showLocationDialog = false) }
        if (granted) refresh()
    }

    // Filters

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recompute(resetPage = true)
    }

    fun onSpeciesSelected(species: PetSpecies?) {
        _uiState.update { it.copy(selectedSpecies = species) }
        recompute(resetPage = true)
    }

    fun onRegionSelected(region: String?) {
        _uiState.update { it.copy(selectedRegion = region) }
        recompute(resetPage = true)
    }

    fun onPageSelected(page: Int) {
        _uiState.update { it.copy(page = page) }
        recompute(resetPage = false)
    }

    fun onCardClicked(item: MissingCardItem) {
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun onDetailDismissed() {
        _uiState.update { it.copy(selectedItem = null) }
    }

    /** Pelapor menandai hewannya sudah ketemu, laporan dihapus dari list. */
    fun onMarkFoundClicked(item: MissingCardItem) {
        viewModelScope.launch {
            reportRepository.markAsFound(item.id)
                .onSuccess {
                    allReports = allReports.filter { it.id != item.id }
                    _uiState.update { it.copy(selectedItem = null) }
                    recompute(resetPage = false)
                    Toast.makeText(getApplication(), "Glad you found ${item.name}!", Toast.LENGTH_LONG).show()
                }
                .onFailure { e ->
                    Toast.makeText(
                        getApplication(),
                        e.localizedMessage ?: "Failed to update the report",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    // filter -> score -> sort -> paginate

    private fun recompute(resetPage: Boolean) {
        val state = _uiState.value
        val now = Instant.now()
        val uLat = userLat
        val uLng = userLng
        val hasLocation = uLat != null && uLng != null

        // Laporan yang lewat 30 hari dianggap kedaluwarsa.
        val live = allReports.filter {
            PriorityAreaCalculator.hoursSince(PriorityAreaCalculator.parseInstant(it.lastSeenAt), now) <= MAX_AGE_HOURS
        }

        val regions = live.groupBy { it.regionName }
            .map { (name, list) -> RegionOption(name, list.size) }
            .sortedWith(compareByDescending<RegionOption> { it.count }.thenBy { it.name })

        // Wilayah dipilih manual, atau "Near me", atau semua.
        val regionFiltered = live.filter { report ->
            val region = state.selectedRegion
            when {
                region != null -> report.regionName == region
                hasLocation -> {
                    val species = PetSpecies.fromDb(report.species)
                    val radius = PriorityAreaCalculator.radiusMeters(
                        species, PriorityAreaCalculator.parseInstant(report.lastSeenAt), now
                    )
                    PriorityAreaCalculator.distanceToAreaMeters(
                        uLat!!, uLng!!, report.displayLat, report.displayLng, radius
                    ) <= NEAR_ME_KM * 1000.0
                }
                else -> true
            }
        }

        val speciesCounts = PetSpecies.entries.associateWith { s ->
            regionFiltered.count { it.species == s.dbValue }
        }

        val query = state.searchQuery.trim()
        val filtered = regionFiltered.filter { report ->
            (state.selectedSpecies == null || report.species == state.selectedSpecies.dbValue) &&
                    (query.isEmpty() ||
                            report.name.contains(query, ignoreCase = true) ||
                            report.breed.contains(query, ignoreCase = true) ||
                            (report.description?.contains(query, ignoreCase = true) == true))
        }

        val sorted = filtered.sortedByDescending { report ->
            val species = PetSpecies.fromDb(report.species)
            val lastSeen = PriorityAreaCalculator.parseInstant(report.lastSeenAt)
            val distance = if (hasLocation) {
                val radius = PriorityAreaCalculator.radiusMeters(species, lastSeen, now)
                PriorityAreaCalculator.distanceToAreaMeters(
                    uLat!!, uLng!!, report.displayLat, report.displayLng, radius
                )
            } else null
            PriorityAreaCalculator.priorityScore(lastSeen, now, distance)
        }

        val totalPages = maxOf(1, ceil(sorted.size / PAGE_SIZE.toDouble()).toInt())
        val page = if (resetPage) 1 else state.page.coerceIn(1, totalPages)
        val pageItems = sorted
            .drop((page - 1) * PAGE_SIZE)
            .take(PAGE_SIZE)
            .map { it.toCardItem(uLat, uLng, now, reportRepository.currentUserId(), reportRepository::photoUrl) }

        _uiState.update {
            it.copy(
                items = pageItems,
                regions = regions,
                totalCount = regionFiltered.size,
                speciesCounts = speciesCounts,
                page = page,
                totalPages = totalPages,
                hasUserLocation = hasLocation
            )
        }
    }

    private companion object {
        const val KEY_LOCATION_ASKED = "location_asked"
        const val PAGE_SIZE = 5
        const val NEAR_ME_KM = 25.0
        const val MAX_AGE_HOURS = 30 * 24.0
    }
}
