package com.papb.petto.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.AppNotification
import com.papb.petto.data.repository.LocationRepository
import com.papb.petto.data.repository.MissingReportRepository
import com.papb.petto.data.repository.MissingReportRepositoryImpl
import com.papb.petto.data.repository.NotificationRepository
import com.papb.petto.data.repository.NotificationRepositoryImpl
import com.papb.petto.ui.missing.toCardItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class NotificationsViewModel @JvmOverloads constructor(
    application: Application,
    private val notificationRepository: NotificationRepository = NotificationRepositoryImpl(),
    private val reportRepository: MissingReportRepository = MissingReportRepositoryImpl(),
    private val locationRepository: LocationRepository = LocationRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NotificationsState())
    val uiState: StateFlow<NotificationsState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            notificationRepository.getMyNotifications()
                .onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            items = items,
                            unreadCount = items.count { n -> n.readAt == null },
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.localizedMessage)
                    }
                }
        }
    }

    fun onNotificationClicked(notification: AppNotification) {
        val reportId = notification.reportId ?: return
        viewModelScope.launch {
            if (notification.readAt == null) {
                markReadLocally(notification.id)
                notificationRepository.markAsRead(notification.id)
            }

            val location = locationRepository.getCurrentLocation()
            reportRepository.getReport(reportId)
                .onSuccess { report ->
                    val card = report.toCardItem(
                        userLat = location?.lat,
                        userLng = location?.lng,
                        now = Instant.now(),
                        currentUserId = reportRepository.currentUserId(),
                        photoUrl = reportRepository::photoUrl
                    )
                    _uiState.update { it.copy(selectedItem = card) }
                }
                .onFailure {
                    _uiState.update { it.copy(errorMessage = "This report is no longer available") }
                }
        }
    }

    fun onDetailDismissed() {
        _uiState.update { it.copy(selectedItem = null) }
    }

    private fun markReadLocally(id: String) {
        _uiState.update { state ->
            val items = state.items.map {
                if (it.id == id) it.copy(readAt = Instant.now().toString()) else it
            }
            state.copy(items = items, unreadCount = items.count { it.readAt == null })
        }
    }
}
