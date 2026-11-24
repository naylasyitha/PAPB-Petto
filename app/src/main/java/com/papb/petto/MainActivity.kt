package com.papb.petto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.papb.petto.ui.auth.LoginScreen
import com.papb.petto.ui.auth.RegisterScreen
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.donation.DonationFlowScreen
import com.papb.petto.ui.home.HomeScreen
import com.papb.petto.ui.theme.PettoTheme

enum class AuthScreenState {
    LOGIN,
    REGISTER,
    HOME,
    DONATION
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
                onNavigateToAllCampaigns = { currentScreen = AuthScreenState.DONATION },
                onDonateClick = { currentScreen = AuthScreenState.DONATION },
                onTabSelected = { tab ->
                    if (tab == BottomTab.DONATION) {
                        currentScreen = AuthScreenState.DONATION
                    }
                }
            )
        }
        AuthScreenState.DONATION -> {
            DonationFlowScreen(
                onNavigateBackToHome = { currentScreen = AuthScreenState.HOME },
                currentTab = BottomTab.DONATION,
                onTabSelected = { tab ->
                    if (tab == BottomTab.HOME) {
                        currentScreen = AuthScreenState.HOME
                    }
                }
            )
        }
    }
}
