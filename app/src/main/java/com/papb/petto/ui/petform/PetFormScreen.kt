package com.papb.petto.ui.petform

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
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

    LaunchedEffect(Unit) {
        viewModel.successEvent.collect {
            viewModel.resetFormState()
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
        onSubmitClick = viewModel::onSubmitClicked,
        onPhotoPicked = viewModel::onPhotoPicked
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
    onSubmitClick: () -> Unit,
    onPhotoPicked: (Uri) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let(onPhotoPicked) }

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
                    text = if (state.isEditMode) "Edit Pet Details" else "Add New Pet",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(120.dp)) {
                    val photoModifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                    val photoModel: Any? = state.photoUri ?: state.imageUrl
                    if (photoModel != null) {
                        AsyncImage(
                            model = photoModel,
                            contentDescription = "Pet Image",
                            placeholder = painterResource(id = state.imageRes),
                            error = painterResource(id = state.imageRes),
                            contentScale = ContentScale.Crop,
                            modifier = photoModifier
                        )
                    } else {
                        Image(
                            painter = painterResource(id = state.imageRes),
                            contentDescription = "Pet Image",
                            contentScale = ContentScale.Crop,
                            modifier = photoModifier
                        )
                    }

                    Surface(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = CircleShape,
                        color = colors.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
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

            Spacer(modifier = Modifier.height(24.dp))

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
                    placeholder = { Text("Enter pet name", color = Color.Gray) },
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Species",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))

                var fieldWidthPx by remember { mutableStateOf(0) }
                val density = LocalDensity.current

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { fieldWidthPx = it.width }
                ) {
                    OutlinedTextField(
                        value = state.selectedSpecies,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select species", color = Color.Gray) },
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.dropdown),
                                contentDescription = "Dropdown",
                                modifier = Modifier.size(16.dp)
                            )
                        },
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

                    // Lapisan transparan: TextField readOnly menelan tap, jadi tap ditangkap di sini
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { onDropdownExpandedChange(!state.isDropdownExpanded) }
                    )

                    DropdownMenu(
                        expanded = state.isDropdownExpanded,
                        onDismissRequest = { onDropdownExpandedChange(false) },
                        modifier = Modifier
                            .width(with(density) { fieldWidthPx.toDp() })
                            .background(colors.surfaceVariant)
                    ) {
                        state.speciesOptions.forEach { species ->
                            DropdownMenuItem(
                                text = { Text(text = species, color = colors.onBackground) },
                                onClick = { onSpeciesSelected(species) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
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
                    placeholder = { Text("Enter breed (e.g. Maltese)", color = Color.Gray) },
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gender",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val femaleSelected = state.gender == GenderOption.FEMALE
                    Surface(
                        onClick = { onGenderChange(GenderOption.FEMALE) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (femaleSelected) colors.primary else colors.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = if (femaleSelected) R.drawable.female_white else R.drawable.female_color),
                                contentDescription = "Female",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Female",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (femaleSelected) Color.White else colors.onBackground
                            )
                        }
                    }

                    val maleSelected = state.gender == GenderOption.MALE
                    Surface(
                        onClick = { onGenderChange(GenderOption.MALE) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (maleSelected) colors.primary else colors.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.male_white),
                                contentDescription = "Male",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Male",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (maleSelected) Color.White else colors.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Age (months)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.ageMonths,
                        onValueChange = onAgeChange,
                        placeholder = { Text("e.g. 10", color = Color.Gray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Weight (kg)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.weightKg,
                        onValueChange = onWeightChange,
                        placeholder = { Text("e.g. 2.1", color = Color.Gray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spayed / Neutered",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                        Text(
                            text = "Reproductive status",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = state.isSpayedNeutered,
                        onCheckedChange = onSpayedNeuteredChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.primary,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = colors.outline
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Medical Notes / Allergies",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("Any allergies or medical conditions...", color = Color.Gray) },
                    minLines = 3,
                    maxLines = 5,
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

            state.errorMessage?.let { errorMsg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMsg,
                    color = Color(0xFFD50000),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onSubmitClick,
                enabled = !state.isLoading,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (state.isEditMode) "Save Changes" else "Add Pet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PetFormScreenPreview() {
    PettoTheme {
        PetFormContent(
            state = PetFormState(),
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
