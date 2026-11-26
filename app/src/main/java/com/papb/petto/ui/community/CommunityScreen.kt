package com.papb.petto.ui.community

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.components.FloatingBottomNavigation
import com.papb.petto.ui.missing.MissingCardItem
import com.papb.petto.ui.missing.MissingPetsContent
import com.papb.petto.ui.missing.MissingPetsState
import com.papb.petto.ui.missing.MissingPetsViewModel
import com.papb.petto.ui.missing.MissingPreviewData
import com.papb.petto.ui.theme.PettoTheme

enum class CommunityTab(val label: String) {
    FORUM("Forum"),
    MISSING("Missing"),
    EDUCATION("Education")
}

@Composable
fun CommunityScreen(
    unreadNotificationCount: Int = 0,
    onTabSelected: (BottomTab) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onReportMissingClick: () -> Unit = {},
    missingViewModel: MissingPetsViewModel = viewModel()
) {
    val missingState by missingViewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        missingViewModel.onLocationPermissionResult(result.values.any { it })
    }

    CommunityContent(
        missingState = missingState,
        unreadNotificationCount = unreadNotificationCount,
        onTabSelected = onTabSelected,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onReportMissingClick = onReportMissingClick,
        onRefresh = missingViewModel::refresh,
        onSearchChange = missingViewModel::onSearchChange,
        onSpeciesSelected = missingViewModel::onSpeciesSelected,
        onRegionSelected = missingViewModel::onRegionSelected,
        onPageSelected = missingViewModel::onPageSelected,
        onCardClick = missingViewModel::onCardClicked,
        onDetailDismiss = missingViewModel::onDetailDismissed,
        onMarkFound = missingViewModel::onMarkFoundClicked,
        onLocationDialogConfirm = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        },
        onLocationDialogDismiss = missingViewModel::onLocationDialogDismissed
    )
}

@Composable
fun CommunityContent(
    missingState: MissingPetsState,
    unreadNotificationCount: Int,
    onTabSelected: (BottomTab) -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onReportMissingClick: () -> Unit,
    onRefresh: () -> Unit,
    onSearchChange: (String) -> Unit,
    onSpeciesSelected: (PetSpecies?) -> Unit,
    onRegionSelected: (String?) -> Unit,
    onPageSelected: (Int) -> Unit,
    onCardClick: (MissingCardItem) -> Unit,
    onDetailDismiss: () -> Unit,
    onMarkFound: (MissingCardItem) -> Unit,
    onLocationDialogConfirm: () -> Unit,
    onLocationDialogDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var currentCommunityTab by remember { mutableStateOf(CommunityTab.MISSING) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Community",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NotificationBell(count = unreadNotificationCount, onClick = onNotificationClick)
                    // TODO setelah branch Profile di-merge: ganti dengan foto profil (R.drawable.user_profile)
                    Surface(
                        onClick = onProfileClick,
                        shape = CircleShape,
                        color = colors.primary,
                        modifier = Modifier.padding(start = 12.dp).size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // --- Slider Forum | Missing | Education ---
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = colors.surfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    CommunityTab.entries.forEach { tab ->
                        val selected = tab == currentCommunityTab
                        Surface(
                            onClick = { currentCommunityTab = tab },
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) colors.primary else Color.Transparent,
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tab.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            when (currentCommunityTab) {
                CommunityTab.MISSING -> MissingPetsContent(
                    state = missingState,
                    onRefresh = onRefresh,
                    onSearchChange = onSearchChange,
                    onSpeciesSelected = onSpeciesSelected,
                    onRegionSelected = onRegionSelected,
                    onPageSelected = onPageSelected,
                    onCardClick = onCardClick,
                    onDetailDismiss = onDetailDismiss,
                    onMarkFound = onMarkFound,
                    onLocationDialogConfirm = onLocationDialogConfirm,
                    onLocationDialogDismiss = onLocationDialogDismiss,
                    modifier = Modifier.weight(1f)
                )

                CommunityTab.FORUM, CommunityTab.EDUCATION -> Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${currentCommunityTab.label} is coming soon",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        if (currentCommunityTab == CommunityTab.MISSING) {
            ExtendedFloatingActionButton(
                onClick = onReportMissingClick,
                containerColor = colors.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Report Missing", fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 96.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            FloatingBottomNavigation(
                currentTab = BottomTab.COMMUNITY,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun NotificationBell(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp
) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = modifier) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = colors.surfaceVariant,
            modifier = Modifier.size(size)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC62828)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 9) "9+" else "$count",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommunityScreenPreview() {
    PettoTheme {
        CommunityContent(
            missingState = MissingPreviewData.state,
            unreadNotificationCount = 3,
            onTabSelected = {},
            onNotificationClick = {},
            onProfileClick = {},
            onReportMissingClick = {},
            onRefresh = {},
            onSearchChange = {},
            onSpeciesSelected = {},
            onRegionSelected = {},
            onPageSelected = {},
            onCardClick = {},
            onDetailDismiss = {},
            onMarkFound = {},
            onLocationDialogConfirm = {},
            onLocationDialogDismiss = {}
        )
    }
}
