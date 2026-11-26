package com.papb.petto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.ui.auth.LoginScreen
import com.papb.petto.ui.auth.RegisterScreen
import com.papb.petto.ui.community.CommunityScreen
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.editprofile.EditProfileScreen
import com.papb.petto.ui.home.HomeScreen
import com.papb.petto.ui.missing.report.ReportMissingScreen
import com.papb.petto.ui.notifications.NotificationsScreen
import com.papb.petto.ui.notifications.NotificationsViewModel
import com.papb.petto.ui.petdetail.PetDetailScreen
import com.papb.petto.ui.petform.PetFormScreen
import com.papb.petto.ui.profile.ProfileScreen
import com.papb.petto.ui.scheduleform.ScheduleFormScreen
import com.papb.petto.ui.theme.PettoTheme

enum class AppScreenState {
    LOGIN,
    REGISTER,
    HOME,
    COMMUNITY,
    REPORT_MISSING,
    NOTIFICATIONS,
    PROFILE,
    PET_DETAIL,
    ADD_PET,
    EDIT_PET,
    EDIT_PROFILE,
    ADD_SCHEDULE,
    EDIT_SCHEDULE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PettoTheme {
                PettoApp()
            }
        }
    }
}

@Composable
fun PettoApp() {
    var currentScreen by remember { mutableStateOf(AppScreenState.HOME) }
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val notificationsState by notificationsViewModel.uiState.collectAsState()

    LaunchedEffect(currentScreen) {
        if (currentScreen == AppScreenState.HOME || currentScreen == AppScreenState.COMMUNITY) {
            notificationsViewModel.refresh()
        }
    }

    fun navigateToTab(tab: BottomTab) {
        currentScreen = when (tab) {
            BottomTab.HOME -> AppScreenState.HOME
            BottomTab.COMMUNITY -> AppScreenState.COMMUNITY
            BottomTab.PROFILE -> AppScreenState.PROFILE
            else -> currentScreen
        }
    }

    when (currentScreen) {
        AppScreenState.LOGIN -> {
            LoginScreen(
                onNavigateToRegister = { currentScreen = AppScreenState.REGISTER },
                onNavigateToHome = { currentScreen = AppScreenState.HOME }
            )
        }
        AppScreenState.REGISTER -> {
            RegisterScreen(
                onNavigateToLogin = { currentScreen = AppScreenState.LOGIN },
                onNavigateToHome = { currentScreen = AppScreenState.HOME }
            )
        }
        AppScreenState.HOME -> {
            HomeScreen(
                unreadNotificationCount = notificationsState.unreadCount,
                onTabSelected = ::navigateToTab,
                onNotificationClick = { currentScreen = AppScreenState.NOTIFICATIONS },
                onQuickAccessClick = { action ->
                    if (action == "missing_pet") currentScreen = AppScreenState.REPORT_MISSING
                }
            )
        }
        AppScreenState.COMMUNITY -> {
            CommunityScreen(
                unreadNotificationCount = notificationsState.unreadCount,
                onTabSelected = ::navigateToTab,
                onNotificationClick = { currentScreen = AppScreenState.NOTIFICATIONS },
                onProfileClick = { currentScreen = AppScreenState.PROFILE },
                onReportMissingClick = { currentScreen = AppScreenState.REPORT_MISSING }
            )
        }
        AppScreenState.REPORT_MISSING -> {
            BackHandler { currentScreen = AppScreenState.COMMUNITY }
            ReportMissingScreen(
                onBackClick = { currentScreen = AppScreenState.COMMUNITY },
                onSuccessFinished = { currentScreen = AppScreenState.COMMUNITY }
            )
        }
        AppScreenState.NOTIFICATIONS -> {
            BackHandler { currentScreen = AppScreenState.HOME }
            NotificationsScreen(
                viewModel = notificationsViewModel,
                onBackClick = { currentScreen = AppScreenState.HOME }
            )
        }
        AppScreenState.PROFILE -> {
            ProfileScreen(
                onTabSelected = ::navigateToTab,
                onEditProfileClick = {
                    currentScreen = AppScreenState.EDIT_PROFILE
                },
                onAddPetClick = {
                    currentScreen = AppScreenState.ADD_PET
                },
                onPetClick = {
                    currentScreen = AppScreenState.PET_DETAIL
                },
                onLogoutSuccess = {
                    currentScreen = AppScreenState.LOGIN
                }
            )
        }
        AppScreenState.PET_DETAIL -> {
            PetDetailScreen(
                onBackClick = {
                    currentScreen = AppScreenState.PROFILE
                },
                onEditPetClick = {
                    currentScreen = AppScreenState.EDIT_PET
                },
                onAddScheduleClick = {
                    currentScreen = AppScreenState.ADD_SCHEDULE
                },
                onEditScheduleClick = {
                    currentScreen = AppScreenState.EDIT_SCHEDULE
                }
            )
        }
        AppScreenState.ADD_PET -> {
            PetFormScreen(
                isEditMode = false,
                onBackClick = {
                    currentScreen = AppScreenState.PROFILE
                },
                onFormSuccess = {
                    currentScreen = AppScreenState.PROFILE
                }
            )
        }
        AppScreenState.EDIT_PET -> {
            PetFormScreen(
                isEditMode = true,
                petId = "1",
                onBackClick = {
                    currentScreen = AppScreenState.PET_DETAIL
                },
                onFormSuccess = {
                    currentScreen = AppScreenState.PET_DETAIL
                }
            )
        }
        AppScreenState.EDIT_PROFILE -> {
            EditProfileScreen(
                onBackClick = {
                    currentScreen = AppScreenState.PROFILE
                },
                onSaveSuccess = {
                    currentScreen = AppScreenState.PROFILE
                }
            )
        }
        AppScreenState.ADD_SCHEDULE -> {
            ScheduleFormScreen(
                isEditMode = false,
                onBackClick = {
                    currentScreen = AppScreenState.PET_DETAIL
                },
                onFormSuccess = {
                    currentScreen = AppScreenState.PET_DETAIL
                }
            )
        }
        AppScreenState.EDIT_SCHEDULE -> {
            ScheduleFormScreen(
                isEditMode = true,
                scheduleId = "1",
                onBackClick = {
                    currentScreen = AppScreenState.PET_DETAIL
                },
                onFormSuccess = {
                    currentScreen = AppScreenState.PET_DETAIL
                }
            )
        }
    }
}
