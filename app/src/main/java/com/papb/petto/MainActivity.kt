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
import com.papb.petto.ui.community.CommunityRoute
import com.papb.petto.ui.home.HomeScreen
import com.papb.petto.ui.theme.PettoTheme
import com.papb.petto.ui.servicemap.ServiceMapRoute

enum class AuthScreenState {
    LOGIN,
    REGISTER,
    HOME,
    SERVICE_MAP,
    COMMUNITY
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
                onNavigateToServiceMap = { currentScreen = AuthScreenState.SERVICE_MAP },
                onNavigateToCommunity = { currentScreen = AuthScreenState.COMMUNITY }
            )
        }
        AuthScreenState.SERVICE_MAP -> {
            ServiceMapRoute(
                onBack = { currentScreen = AuthScreenState.HOME }
            )
        }
        AuthScreenState.COMMUNITY -> {
            CommunityRoute(
                onBack = { currentScreen = AuthScreenState.HOME },
                onNavigateToHome = { currentScreen = AuthScreenState.HOME },
                onNavigateToServiceMap = { currentScreen = AuthScreenState.SERVICE_MAP }
            )
        }
    }
}
