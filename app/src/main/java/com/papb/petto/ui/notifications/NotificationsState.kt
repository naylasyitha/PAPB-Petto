package com.papb.petto.ui.notifications

import com.papb.petto.data.model.AppNotification
import com.papb.petto.ui.missing.MissingCardItem

data class NotificationsState(
    val items: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedItem: MissingCardItem? = null
)
