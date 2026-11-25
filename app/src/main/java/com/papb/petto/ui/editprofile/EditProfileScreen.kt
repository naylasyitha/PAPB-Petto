package com.papb.petto.ui.editprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.ui.theme.PettoTheme

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveSuccess()
        }
    }

    EditProfileContent(
        state = uiState,
        onBackClick = onBackClick,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onAlertRadiusChange = viewModel::onAlertRadiusChange,
        onUseCurrentLocationClick = viewModel::onUseCurrentLocationClicked,
        onSaveChangesClick = viewModel::onSaveChangesClicked
    )
}

@Composable
fun EditProfileContent(
    state: EditProfileState,
    onBackClick: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAlertRadiusChange: (Float) -> Unit,
    onUseCurrentLocationClick: () -> Unit,
    onSaveChangesClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // --- TOP HEADER (Back Button & Title) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = colors.primary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Edit Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- USER AVATAR WITH CAMERA BADGE ---
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(130.dp)) {
                    Image(
                        painter = painterResource(id = state.avatarRes),
                        contentDescription = "User Profile Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )

                    // Camera Badge Button
                    Surface(
                        onClick = { },
                        shape = CircleShape,
                        color = colors.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = "Change Photo",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- FORM: FULL NAME ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Full Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = onFullNameChange,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = colors.outline,
                        unfocusedBorderColor = colors.outline,
                        focusedTextColor = colors.onBackground,
                        unfocusedTextColor = colors.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM: EMAIL ADDRESS ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email Address",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = colors.outline,
                        unfocusedBorderColor = colors.outline,
                        focusedTextColor = colors.onBackground,
                        unfocusedTextColor = colors.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM: PHONE NUMBER ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Phone Number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.indonesia_flag),
                                contentDescription = "Indonesia Flag",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+62",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onBackground
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = colors.outline,
                        unfocusedBorderColor = colors.outline,
                        focusedTextColor = colors.onBackground,
                        unfocusedTextColor = colors.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Shown on missing pet reports so volunteers and shelters can reach you promptly.",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM: HOME ADDRESS ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Home Address",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Address Card Box
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pin_map),
                            contentDescription = "Pin Location",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = state.homeAddressTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onBackground
                            )
                            Text(
                                text = state.homeAddressSubtitle,
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Map Preview Container
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.map_home),
                            contentDescription = "Home Address Map",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Center Paw Pin
                        Image(
                            painter = painterResource(id = R.drawable.pin_map),
                            contentDescription = "Pin",
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.Center)
                        )

                        // Overlay "Use current location" Button
                        Surface(
                            onClick = onUseCurrentLocationClick,
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFFF7F2),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.current_loc),
                                    contentDescription = "Current Location",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Use current location",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38220F)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- FORM: ALERT RADIUS SLIDER ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Alert Radius",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = state.alertRadiusKm,
                    onValueChange = onAlertRadiusChange,
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primary,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.primary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "1 km", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Text(
                        text = "${state.alertRadiusKm.toInt()} km",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    Text(text = "5 km", fontSize = 12.sp, color = colors.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "You will receive immediate community in-app notifications about lost and rescued pets within this radius.",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Error Message (if any)
            state.errorMessage?.let { errorMsg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMsg,
                    color = colors.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- SAVE CHANGES PRIMARY BUTTON ---
            Button(
                onClick = onSaveChangesClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Compose Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    PettoTheme {
        EditProfileContent(
            state = EditProfileState(),
            onBackClick = {},
            onFullNameChange = {},
            onEmailChange = {},
            onPhoneNumberChange = {},
            onAlertRadiusChange = {},
            onUseCurrentLocationClick = {},
            onSaveChangesClick = {}
        )
    }
}
