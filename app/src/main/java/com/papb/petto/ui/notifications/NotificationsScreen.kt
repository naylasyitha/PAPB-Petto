package com.papb.petto.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.AppNotification
import com.papb.petto.ui.missing.MissingDetailDialog
import com.papb.petto.ui.theme.PettoTheme
import com.papb.petto.util.PriorityAreaCalculator
import java.time.Instant

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    NotificationsContent(
        state = state,
        onBackClick = onBackClick,
        onNotificationClick = viewModel::onNotificationClicked
    )

    state.selectedItem?.let {
        MissingDetailDialog(item = it, onDismiss = viewModel::onDetailDismissed)
    }
}

@Composable
fun NotificationsContent(
    state: NotificationsState,
    onBackClick: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = colors.primary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Notifications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            when {
                state.isLoading && state.items.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = colors.primary) }

                state.items.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.errorMessage ?: "No notifications yet.\nWe'll alert you when a pet goes missing near you.",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp, end = 20.dp, bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.items.size) { index ->
                        val item = state.items[index]
                        NotificationRow(item = item, onClick = { onNotificationClick(item) })
                    }
                }
            }
        }

        state.errorMessage?.takeIf { state.items.isNotEmpty() }?.let {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xE62C221E),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: AppNotification, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val unread = item.readAt == null

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (unread) colors.surfaceVariant else colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (unread) Color(0xFFC62828) else Color.Transparent)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = if (unread) FontWeight.Bold else FontWeight.Medium,
                    color = colors.onBackground
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    text = item.body,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = PriorityAreaCalculator.formatPostedAgo(
                        PriorityAreaCalculator.parseInstant(item.createdAt),
                        Instant.now()
                    ),
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    val now = Instant.now()
    PettoTheme {
        NotificationsContent(
            state = NotificationsState(
                items = listOf(
                    AppNotification(
                        id = "1", reportId = "r1",
                        title = "Missing dog nearby: Stella",
                        body = "Golden Retriever was last seen in your alert area (Kota Jakarta Selatan).",
                        readAt = null,
                        createdAt = now.minusSeconds(2 * 3600).toString()
                    ),
                    AppNotification(
                        id = "2", reportId = "r2",
                        title = "Missing cat nearby: Bella",
                        body = "Calico Cat was last seen in your alert area (Kota Jakarta Selatan).",
                        readAt = now.toString(),
                        createdAt = now.minusSeconds(30 * 3600).toString()
                    )
                ),
                unreadCount = 1
            ),
            onBackClick = {},
            onNotificationClick = {}
        )
    }
}
