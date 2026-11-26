package com.papb.petto.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.components.FloatingBottomNavigation
import com.papb.petto.ui.theme.PettoTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onTabSelected: (BottomTab) -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onAddPetClick: () -> Unit = {},
    onPetClick: (String) -> Unit = {},
    onSettingClick: (String) -> Unit = {},
    onLogoutSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    ProfileContent(
        state = uiState,
        onTabSelected = { tab ->
            viewModel.onTabSelected(tab)
            onTabSelected(tab)
        },
        onEditProfileClick = onEditProfileClick,
        onAddPetClick = onAddPetClick,
        onPetClick = onPetClick,
        onSettingClick = onSettingClick,
        onLogoutClick = viewModel::onLogoutClicked
    )
}

@Composable
fun ProfileContent(
    state: ProfileState,
    onTabSelected: (BottomTab) -> Unit,
    onEditProfileClick: () -> Unit,
    onAddPetClick: () -> Unit,
    onPetClick: (String) -> Unit,
    onSettingClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        // --- 1. SCROLLABLE PROFILE CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // --- USER HEADER (Avatar, Name, Email, Edit Profile) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar Image
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(90.dp),
                    color = Color.LightGray
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_profile),
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = state.userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.userEmail,
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Edit Profile Button
                    Button(
                        onClick = onEditProfileClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.edit_profile),
                                contentDescription = "Edit",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "edit profile",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- MY PETS SECTION ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Pets",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                Button(
                    onClick = onAddPetClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "+ add pet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            state.pets.forEach { pet ->
                PetProfileCard(pet = pet, onClick = { onPetClick(pet.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SETTINGS SECTION ---
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    state.settings.forEachIndexed { index, setting ->
                        SettingRowItem(
                            item = setting,
                            onClick = { onSettingClick(setting.onClickAction) }
                        )
                        if (index < state.settings.size - 1) {
                            HorizontalDivider(
                                color = colors.outline.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LOG OUT BUTTON ---
            Button(
                onClick = onLogoutClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Log out",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Bottom Spacer agar konten tidak tertutup Floating Navbar
            Spacer(modifier = Modifier.height(100.dp))
        }

        // --- 2. FLOATING BOTTOM NAVIGATION BAR ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            FloatingBottomNavigation(
                currentTab = state.currentTab,
                onTabSelected = onTabSelected
            )
        }
    }
}

// --- Pet Profile Card Component ---
@Composable
private fun PetProfileCard(pet: PetProfileItem, onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pet Image
            Image(
                painter = painterResource(id = pet.imageRes),
                contentDescription = pet.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pet.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = pet.speciesBreed,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = pet.ageText,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
            }

            // Next Arrow Icon
            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = "View detail",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// --- Setting Row Item Component ---
@Composable
private fun SettingRowItem(
    item: SettingMenuItem,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground
            )
        }

        Image(
            painter = painterResource(id = R.drawable.next),
            contentDescription = "Navigate",
            modifier = Modifier.size(18.dp)
        )
    }
}

// --- Compose Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    PettoTheme {
        ProfileContent(
            state = ProfileState(
                pets = listOf(
                    PetProfileItem("1", "Snowie", "Dog • Maltese", "10 months", R.drawable.snowie),
                    PetProfileItem("2", "Hotdog", "Dog • Golden Retriever", "2 years 4 months", R.drawable.hotdog)
                ),
                settings = listOf(
                    SettingMenuItem("Language", R.drawable.language_profile, "language"),
                    SettingMenuItem("Help and Reports", R.drawable.help_profile, "help"),
                    SettingMenuItem("Notifications", R.drawable.notif_profile, "notifications"),
                    SettingMenuItem("About Petto", R.drawable.about_profile, "about")
                )
            ),
            onTabSelected = {},
            onEditProfileClick = {},
            onAddPetClick = {},
            onPetClick = {},
            onSettingClick = {},
            onLogoutClick = {}
        )
    }
}