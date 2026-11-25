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
import com.papb.petto.ui.home.HomeScreen
import com.papb.petto.ui.missing.report.ReportMissingScreen
import com.papb.petto.ui.notifications.NotificationsScreen
import com.papb.petto.ui.notifications.NotificationsViewModel
import com.papb.petto.ui.theme.PettoTheme

enum class AuthScreenState {
    LOGIN,
    REGISTER,
    HOME,
    COMMUNITY,
    REPORT_MISSING,
    NOTIFICATIONS
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
    var currentScreen by remember { mutableStateOf(AuthScreenState.HOME) }
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val notificationsState by notificationsViewModel.uiState.collectAsState()

    LaunchedEffect(currentScreen) {
        if (currentScreen == AuthScreenState.HOME || currentScreen == AuthScreenState.COMMUNITY) {
            notificationsViewModel.refresh()
        }
    }

    fun navigateToTab(tab: BottomTab) {
        currentScreen = when (tab) {
            BottomTab.HOME -> AuthScreenState.HOME
            BottomTab.COMMUNITY -> AuthScreenState.COMMUNITY
            else -> currentScreen
        }
    }

    when (currentScreen) {
        AuthScreenState.LOGIN -> {
            LoginScreen(
                onNavigateToRegister = { currentScreen = AuthScreenState.REGISTER },
                onNavigateToHome = { currentScreen = AuthScreenState.HOME }
            )
        }
        AuthScreenState.REGISTER -> {
            RegisterScreen(
                onNavigateToLogin = { currentScreen = AuthScreenState.LOGIN },
                onNavigateToHome = { currentScreen = AuthScreenState.HOME }
            )
        }
        AuthScreenState.HOME -> {
            HomeScreen(
                unreadNotificationCount = notificationsState.unreadCount,
                onTabNavigate = ::navigateToTab,
                onNotificationClick = { currentScreen = AuthScreenState.NOTIFICATIONS },
                onQuickAccessClick = { action ->
                    if (action == "missing_pet") currentScreen = AuthScreenState.REPORT_MISSING
                }
            )
        }
        AuthScreenState.COMMUNITY -> {
            CommunityScreen(
                unreadNotificationCount = notificationsState.unreadCount,
                onTabSelected = ::navigateToTab,
                onNotificationClick = { currentScreen = AuthScreenState.NOTIFICATIONS },
                onProfileClick = { /* TODO setelah branch Profile di-merge: ke PROFILE */ },
                onReportMissingClick = { currentScreen = AuthScreenState.REPORT_MISSING }
            )
        }
        AuthScreenState.REPORT_MISSING -> {
            BackHandler { currentScreen = AuthScreenState.COMMUNITY }
            ReportMissingScreen(
                onBackClick = { currentScreen = AuthScreenState.COMMUNITY },
                onSuccessFinished = { currentScreen = AuthScreenState.COMMUNITY }
            )
        }
        AuthScreenState.NOTIFICATIONS -> {
            BackHandler { currentScreen = AuthScreenState.HOME }
            NotificationsScreen(
                viewModel = notificationsViewModel,
                onBackClick = { currentScreen = AuthScreenState.HOME }
            )
        }
    }
}
