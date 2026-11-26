package com.papb.petto.ui.petform

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.ui.addpet.GenderOption
import com.papb.petto.ui.theme.PettoTheme

@Composable
fun PetFormScreen(
    isEditMode: Boolean = false,
    petId: String? = null,
    viewModel: PetFormViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onFormSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isEditMode, petId) {
        viewModel.setupForm(isEditMode, petId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onFormSuccess()
        }
    }

    PetFormContent(
        state = uiState,
        onBackClick = onBackClick,
        onPetNameChange = viewModel::onPetNameChange,
        onSpeciesSelected = viewModel::onSpeciesSelected,
        onDropdownExpandedChange = viewModel::onDropdownExpandedChange,
        onBreedChange = viewModel::onBreedChange,
        onGenderChange = viewModel::onGenderChange,
        onAgeChange = viewModel::onAgeChange,
        onWeightChange = viewModel::onWeightChange,
        onSpayedNeuteredChange = viewModel::onSpayedNeuteredChange,
        onNotesChange = viewModel::onNotesChange,
        onSubmitClick = viewModel::onSubmitClicked
    )
}

@Composable
fun PetFormContent(
    state: PetFormState,
    onBackClick: () -> Unit,
    onPetNameChange: (String) -> Unit,
    onSpeciesSelected: (String) -> Unit,
    onDropdownExpandedChange: (Boolean) -> Unit,
    onBreedChange: (String) -> Unit,
    onGenderChange: (GenderOption) -> Unit,
    onAgeChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onSpayedNeuteredChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    val headerTitle = if (state.isEditMode) "Edit Pet" else "Add Pet"
    val submitButtonText = if (state.isEditMode) "Save Changes" else "Add Pet"

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
                    text = headerTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PET PHOTO CONTAINER (Dynamic: Pre-filled image if edit, placeholder if add) ---
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(130.dp)) {
                    if (state.isEditMode) {
                        // Edit Mode: Pre-filled Pet Photo
                        Image(
                            painter = painterResource(id = state.imageRes),
                            contentDescription = state.petName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        // Add Mode: Empty Avatar Circle
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE0D5CC),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Pet",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

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
                                contentDescription = "Upload Photo",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- FORM: PET NAME ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pet Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.petName,
                    onValueChange = onPetNameChange,
                    placeholder = { Text("Input your pet name", color = Color.Gray) },
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

            // --- FORM ROW: SPECIES & BREED ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Species Dropdown Field
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Species",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box {
                        OutlinedTextField(
                            value = state.selectedSpecies.ifEmpty { "Select" },
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.dropdown),
                                    contentDescription = "Dropdown",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onDropdownExpandedChange(!state.isDropdownExpanded) }
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colors.surfaceVariant,
                                unfocusedContainerColor = colors.surfaceVariant,
                                focusedBorderColor = colors.outline,
                                unfocusedBorderColor = colors.outline,
                                focusedTextColor = if (state.selectedSpecies.isEmpty()) Color.Gray else colors.onBackground,
                                unfocusedTextColor = if (state.selectedSpecies.isEmpty()) Color.Gray else colors.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDropdownExpandedChange(!state.isDropdownExpanded) }
                        )

                        DropdownMenu(
                            expanded = state.isDropdownExpanded,
                            onDismissRequest = { onDropdownExpandedChange(false) },
                            modifier = Modifier.background(colors.surfaceVariant)
                        ) {
                            state.speciesOptions.forEach { species ->
                                DropdownMenuItem(
                                    text = { Text(species, color = colors.onBackground) },
                                    onClick = { onSpeciesSelected(species) }
                                )
                            }
                        }
                    }
                }

                // Breed Field
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Breed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.breed,
                        onValueChange = onBreedChange,
                        placeholder = { Text("", color = Color.Gray) },
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM: ANIMATED GENDER SEGMENTED TAB SWITCHER ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gender",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))

                AnimatedGenderSegmentedControl(
                    selectedGender = state.gender,
                    onGenderSelected = onGenderChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM ROW: AGE & WEIGHT ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Age Field
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Age",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.ageMonths,
                        onValueChange = onAgeChange,
                        placeholder = { Text("months", color = Color.Gray) },
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

                // Weight Field
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weight",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.weightKg,
                        onValueChange = onWeightChange,
                        placeholder = { Text("kg", color = Color.Gray) },
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- FORM: SPAYED / NEUTERED TOGGLE ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spayed / Neutered",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                Switch(
                    checked = state.isSpayedNeutered,
                    onCheckedChange = onSpayedNeuteredChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = colors.outline.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM: DIETARY & MEDICAL NOTES ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Dietary & Medical Notes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("", color = Color.Gray) },
                    singleLine = false,
                    minLines = 4,
                    maxLines = 6,
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

            // --- PRIMARY SUBMIT BUTTON (Add Pet / Save Changes) ---
            Button(
                onClick = onSubmitClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = submitButtonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Animated Gender Segmented Control Component ---
@Composable
private fun AnimatedGenderSegmentedControl(
    selectedGender: GenderOption,
    onGenderSelected: (GenderOption) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val alignmentBias by animateFloatAsState(
        targetValue = if (selectedGender == GenderOption.FEMALE) -1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gender_slide_anim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant, RoundedCornerShape(16.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(CircleShape)
                .background(colors.primary)
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .align(BiasAlignment(horizontalBias = alignmentBias, verticalBias = 0f))
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, colors.primary, CircleShape)
            )

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Female Tab Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable { onGenderSelected(GenderOption.FEMALE) },
                    contentAlignment = Alignment.Center
                ) {
                    val isSelected = selectedGender == GenderOption.FEMALE
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = if (isSelected) R.drawable.female_color else R.drawable.female_white),
                            contentDescription = "Female",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Female",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.primary else Color.White
                        )
                    }
                }

                // Male Tab Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable { onGenderSelected(GenderOption.MALE) },
                    contentAlignment = Alignment.Center
                ) {
                    val isSelected = selectedGender == GenderOption.MALE
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = if (isSelected) R.drawable.female_color else R.drawable.male_white),
                            contentDescription = "Male",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Male",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.primary else Color.White
                        )
                    }
                }
            }
        }
    }
}

// --- Compose Previews for Add Mode & Edit Mode ---
@Preview(name = "Add Pet Mode", showBackground = true, showSystemUi = true)
@Composable
fun AddPetModePreview() {
    PettoTheme {
        PetFormContent(
            state = PetFormState(isEditMode = false),
            onBackClick = {},
            onPetNameChange = {},
            onSpeciesSelected = {},
            onDropdownExpandedChange = {},
            onBreedChange = {},
            onGenderChange = {},
            onAgeChange = {},
            onWeightChange = {},
            onSpayedNeuteredChange = {},
            onNotesChange = {},
            onSubmitClick = {}
        )
    }
}

@Preview(name = "Edit Pet Mode", showBackground = true, showSystemUi = true)
@Composable
fun EditPetModePreview() {
    PettoTheme {
        PetFormContent(
            state = PetFormState(
                isEditMode = true,
                petName = "Snowie",
                selectedSpecies = "Dog",
                breed = "Maltese",
                gender = GenderOption.FEMALE,
                ageMonths = "10 months",
                weightKg = "2.1 kg",
                isSpayedNeutered = true,
                notes = "Mild allergy to chicken, loves sweet potato treats.",
                imageRes = R.drawable.snowie
            ),
            onBackClick = {},
            onPetNameChange = {},
            onSpeciesSelected = {},
            onDropdownExpandedChange = {},
            onBreedChange = {},
            onGenderChange = {},
            onAgeChange = {},
            onWeightChange = {},
            onSpayedNeuteredChange = {},
            onNotesChange = {},
            onSubmitClick = {}
        )
    }
}
