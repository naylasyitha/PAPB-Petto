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
import com.papb.petto.ui.editprofile.EditProfileScreen
import com.papb.petto.ui.home.HomeScreen
import com.papb.petto.ui.petdetail.PetDetailScreen
import com.papb.petto.ui.petform.PetFormScreen
import com.papb.petto.ui.profile.ProfileScreen
import com.papb.petto.ui.scheduleform.ScheduleFormScreen
import com.papb.petto.ui.theme.PettoTheme

enum class AppScreenState {
    LOGIN,
    REGISTER,
    HOME,
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
    var currentScreen by remember { mutableStateOf(AppScreenState.PROFILE) }

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
                onTabSelected = { tab ->
                    if (tab == BottomTab.PROFILE) {
                        currentScreen = AppScreenState.PROFILE
                    }
                }
            )
        }
        AppScreenState.PROFILE -> {
            ProfileScreen(
                onTabSelected = { tab ->
                    if (tab == BottomTab.HOME) {
                        currentScreen = AppScreenState.HOME
                    }
                },
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
