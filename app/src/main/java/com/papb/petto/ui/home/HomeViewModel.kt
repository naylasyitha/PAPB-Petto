package com.papb.petto.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.repository.PetRepository
import com.papb.petto.data.repository.PetRepositoryImpl
import com.papb.petto.data.repository.ProfileRepository
import com.papb.petto.data.repository.ProfileRepositoryImpl
import com.papb.petto.data.repository.ScheduleRepository
import com.papb.petto.data.repository.ScheduleRepositoryImpl
import com.papb.petto.ui.components.BottomTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: ProfileRepository = ProfileRepositoryImpl(),
    private val petRepository: PetRepository = PetRepositoryImpl(),
    private val scheduleRepository: ScheduleRepository = ScheduleRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadDashboardData()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val email = userRepository.currentUserEmail().orEmpty()
            userRepository.getMyProfile()
                .onSuccess { profile ->
                    val firstName = profile?.fullName?.trim()?.substringBefore(" ")
                        ?.takeIf { it.isNotBlank() }
                        ?: email.substringBefore("@").takeIf { it.isNotBlank() }
                    if (firstName != null) {
                        _uiState.update { it.copy(userName = firstName) }
                    }
                }
                .onFailure { e ->
                    Log.e("HomeViewModel", "Gagal memuat profil", e)
                }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            val petsResult = petRepository.getPetsForCurrentUser()
            petsResult.onSuccess { pets ->
                val petCount = pets.size
                val petCountMsg = if (petCount > 0) "$petCount happy, healthy pets today" else "No pets added yet"

                val firstPetId = pets.firstOrNull()?.id
                if (firstPetId != null) {
                    val schedulesResult = scheduleRepository.getSchedulesForPet(firstPetId)
                    schedulesResult.onSuccess { schedules ->
                        val items = schedules.map { dto ->
                            PetScheduleItem(
                                title = dto.careName,
                                petName = pets.firstOrNull { it.id == dto.petId }?.name ?: "Pet",
                                dateLocation = "${dto.nextDueDate} • ${dto.notes.ifBlank { "Regular Checkup" }}",
                                statusTag = dto.status.replaceFirstChar { it.uppercase() },
                                isOverdue = dto.status.equals("overdue", ignoreCase = true)
                            )
                        }
                        _uiState.update { state ->
                            state.copy(
                                petCountText = petCountMsg,
                                schedules = items.ifEmpty { state.schedules }
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(petCountText = petCountMsg) }
                }
            }
        }
    }

    fun onTabSelected(tab: BottomTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }
}
