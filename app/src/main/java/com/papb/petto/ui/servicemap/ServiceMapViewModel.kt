package com.papb.petto.ui.servicemap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.location.LocationRepository
import com.papb.petto.data.model.Service
import com.papb.petto.data.repository.ServiceRepository
import com.papb.petto.util.haversineKm
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceMapViewModel(
    private val serviceRepository: ServiceRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceMapUiState())
    val uiState: StateFlow<ServiceMapUiState> = _uiState.asStateFlow()

    private var allServices: List<Service> = emptyList()

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val services = serviceRepository.getServices().getOrThrow()
                val location = if (locationRepository.hasLocationPermission()) {
                    locationRepository.getCurrentLocation().getOrNull()
                } else {
                    null
                }

                allServices = if (location != null) {
                    services.map { service ->
                        service.copy(
                            distanceKm = haversineKm(
                                location.latitude,
                                location.longitude,
                                service.latitude,
                                service.longitude
                            )
                        )
                    }
                } else {
                    services
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasLocation = location != null,
                    permissionDenied = !locationRepository.hasLocationPermission()
                )
                applyFilter()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat data layanan"
                )
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
        applyFilter()
    }

    fun onCategoryChange(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilter()
    }

    fun onPermissionDenied() {
        _uiState.value = _uiState.value.copy(permissionDenied = true)
    }

    private fun applyFilter() {
        val state = _uiState.value

        val filtered = allServices
            .filter { service ->
                state.selectedCategory == "All" || service.category == state.selectedCategory
            }
            .filter { service ->
                state.query.isBlank() ||
                        service.name.contains(state.query, ignoreCase = true) ||
                        service.address.contains(state.query, ignoreCase = true)
            }
            .sortedBy { it.distanceKm ?: Double.MAX_VALUE }

        _uiState.value = _uiState.value.copy(services = filtered)
    }

    companion object {
        fun factory(
            serviceRepository: ServiceRepository,
            locationRepository: LocationRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ServiceMapViewModel(serviceRepository, locationRepository) as T
                }
            }
    }
}