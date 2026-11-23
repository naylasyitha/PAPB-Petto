package com.papb.petto.ui.home

import androidx.lifecycle.ViewModel
import com.papb.petto.ui.components.BottomTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    fun onTabSelected(tab: BottomTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }
}
