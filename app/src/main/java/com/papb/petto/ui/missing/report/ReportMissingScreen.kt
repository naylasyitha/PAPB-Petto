package com.papb.petto.ui.missing.report

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.papb.petto.data.model.PetSpecies
import com.papb.petto.ui.components.StaticOsmMap
import com.papb.petto.ui.theme.PettoTheme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReportMissingScreen(
    viewModel: ReportMissingViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccessFinished: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            delay(2000)
            onSuccessFinished()
        }
    }

    ReportMissingContent(
        state = state,
        fallbackLat = viewModel.fallbackLat,
        fallbackLng = viewModel.fallbackLng,
        onBackClick = onBackClick,
        onPhotosPicked = viewModel::onPhotosPicked,
        onRemovePhoto = viewModel::onRemovePhoto,
        onNameChange = viewModel::onNameChange,
        onSpeciesChange = viewModel::onSpeciesChange,
        onBreedChange = viewModel::onBreedChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onLocationPicked = viewModel::onLocationPicked,
        onDateSelected = viewModel::onDateSelected,
        onTimeSelected = viewModel::onTimeSelected,
        onPhoneChange = viewModel::onPhoneChange,
        onRewardChange = viewModel::onRewardChange,
        onSubmitClick = viewModel::onSubmitClicked,
        onSuccessDismiss = onSuccessFinished
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMissingContent(
    state: ReportMissingState,
    fallbackLat: Double,
    fallbackLng: Double,
    onBackClick: () -> Unit,
    onPhotosPicked: (List<android.net.Uri>) -> Unit,
    onRemovePhoto: (Int) -> Unit,
    onNameChange: (String) -> Unit,
    onSpeciesChange: (PetSpecies) -> Unit,
    onBreedChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationPicked: (Double, Double) -> Unit,
    onDateSelected: (Long) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRewardChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onSuccessDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var showMapPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(ReportMissingState.MAX_PHOTOS)
    ) { uris -> if (uris.isNotEmpty()) onPhotosPicked(uris) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = colors.primary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Report",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            Spacer(Modifier.height(20.dp))

            // Photos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FieldLabel(text = "Pet Photos", required = true)
                Text(
                    text = "Max ${ReportMissingState.MAX_PHOTOS} photos",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(ReportMissingState.MAX_PHOTOS) { index ->
                    val uri = state.photoUris.getOrNull(index)
                    PhotoSlot(
                        uri = uri,
                        isAddSlot = uri == null && index == state.photoUris.size,
                        onAddClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemoveClick = { onRemovePhoto(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Clear full-body photos help community members identify pets faster.",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // Basic Information
            SectionCard(title = "Basic Information") {
                FieldLabel(text = "Pet Name", required = true)
                Spacer(Modifier.height(6.dp))
                FormTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    placeholder = "e.g. Milo or Pluto"
                )

                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(text = "Species", required = true)
                        Spacer(Modifier.height(6.dp))
                        SpeciesDropdown(selected = state.species, onSelected = onSpeciesChange)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(text = "Breed", required = true)
                        Spacer(Modifier.height(6.dp))
                        FormTextField(
                            value = state.breed,
                            onValueChange = onBreedChange,
                            placeholder = "e.g. Golden Mix"
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Description")
                Spacer(Modifier.height(6.dp))
                FormTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    placeholder = "Coat color, distinctive patches, harness color, behavioral traits (e.g. friendly, easily frightened by birds)...",
                    singleLine = false,
                    minHeight = 110.dp
                )
                Text(
                    text = "${state.description.length}/${ReportMissingState.MAX_DESCRIPTION}",
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Last Seen Details
            SectionCard(title = "Last Seen Details") {
                FieldLabel(text = "Area", required = true)
                Spacer(Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.primary,
                    modifier = Modifier.fillMaxWidth().clickable { showMapPicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    state.isLocating -> "Detecting area..."
                                    state.areaTitle.isNotBlank() -> state.areaTitle
                                    else -> "Choose last seen location"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (state.areaSubtitle.isNotBlank() && !state.isLocating) {
                                Text(
                                    text = state.areaSubtitle,
                                    fontSize = 12.sp,
                                    color = Color(0xCCFFFFFF)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                LocationPreview(
                    lat = state.pickedLat,
                    lng = state.pickedLng,
                    onClick = { showMapPicker = true }
                )

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Only an approximate search area is shown to others, never your exact point.",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(text = "Date", required = true)
                        Spacer(Modifier.height(6.dp))
                        PickerField(
                            text = state.dateMillisUtc?.let {
                                DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
                                    .format(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC))
                            } ?: "Select date",
                            icon = { Icon(Icons.Filled.DateRange, null, modifier = Modifier.size(20.dp)) },
                            onClick = { showDatePicker = true }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(text = "Time", required = true)
                        Spacer(Modifier.height(6.dp))
                        PickerField(
                            text = if (state.hour != null && state.minute != null) {
                                LocalTime.of(state.hour, state.minute)
                                    .format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
                            } else "Select time",
                            icon = { Text("🕒", fontSize = 16.sp) },
                            onClick = { showTimePicker = true }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Contact & Incentives
            SectionCard(title = "Contact & Incentives") {
                FieldLabel(text = "Emergency Phone Number", required = true)
                Spacer(Modifier.height(6.dp))
                FormTextField(
                    value = state.phoneDigits,
                    onValueChange = onPhoneChange,
                    placeholder = "812 3456 7890",
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        ) {
                            Icon(Icons.Filled.Phone, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("+62", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Spacer(Modifier.height(14.dp))

                FieldLabel(text = "Finder's Reward (Optional)")
                Spacer(Modifier.height(6.dp))
                FormTextField(
                    value = state.rewardDigits,
                    onValueChange = onRewardChange,
                    placeholder = "e.g. 500000",
                    keyboardType = KeyboardType.Number,
                    leadingIcon = {
                        Text(
                            "IDR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 14.dp, end = 4.dp)
                        )
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.rewardDigits.toLongOrNull()?.takeIf { it > 0 }
                        ?.let { "Reward shown as ${com.papb.petto.util.FormatUtils.formatRupiah(it)}" }
                        ?: "Offering a modest reward can incentivize community neighborhood checks.",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "By submitting, your report will appear in local pet discovery feeds and nearby users will be notified.",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                lineHeight = 16.sp
            )

            state.errorMessage?.let {
                Spacer(Modifier.height(10.dp))
                Text(text = it, color = colors.error, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubmitClick,
                enabled = !state.isSubmitting,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Upload Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // Dialogs
    if (showMapPicker) {
        MapPickerDialog(
            initialLat = state.pickedLat ?: fallbackLat,
            initialLng = state.pickedLng ?: fallbackLng,
            onConfirm = { lat, lng ->
                showMapPicker = false
                onLocationPicked(lat, lng)
            },
            onDismiss = { showMapPicker = false }
        )
    }

    if (showDatePicker) {
        val dayMs = 24L * 60 * 60 * 1000
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.dateMillisUtc,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val now = System.currentTimeMillis()
                    return utcTimeMillis <= now + dayMs && utcTimeMillis >= now - 30 * dayMs
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let(onDateSelected)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.hour ?: 12,
            initialMinute = state.minute ?: 0,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
            text = { TimePicker(state = timePickerState) }
        )
    }

    if (state.isSuccess) {
        SuccessDialog(onDismiss = onSuccessDismiss)
    }
}

// ---------------------------------------------------------------------------
// Building blocks
// ---------------------------------------------------------------------------

@Composable
private fun FieldLabel(text: String, required: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    Row {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
        if (required) {
            Text(text = " *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.error)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colors.background,
        border = BorderStroke(1.5.dp, Color(0xFFB5541C)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minHeight: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = colors.onSurfaceVariant) },
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            focusedBorderColor = colors.outline,
            unfocusedBorderColor = colors.outline,
            focusedTextColor = colors.onBackground,
            unfocusedTextColor = colors.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (minHeight != androidx.compose.ui.unit.Dp.Unspecified) Modifier.height(minHeight) else Modifier)
    )
}

@Composable
private fun PickerField(text: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.outline),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(8.dp))
            Text(text = text, fontSize = 14.sp, color = colors.onBackground)
        }
    }
}

@Composable
private fun SpeciesDropdown(selected: PetSpecies?, onSelected: (PetSpecies) -> Unit) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceVariant,
            border = BorderStroke(1.dp, colors.outline),
            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.label ?: "Select",
                    fontSize = 14.sp,
                    color = if (selected == null) colors.onSurfaceVariant else colors.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PetSpecies.entries.forEach { species ->
                DropdownMenuItem(
                    text = { Text(species.label) },
                    onClick = {
                        expanded = false
                        onSelected(species)
                    }
                )
            }
        }
    }
}

@Composable
private fun PhotoSlot(
    uri: android.net.Uri?,
    isAddSlot: Boolean,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(116.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
            .then(if (isAddSlot) Modifier.clickable(onClick = onAddClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        when {
            uri != null -> {
                AsyncImage(
                    model = uri,
                    contentDescription = "Pet photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    onClick = onRemoveClick,
                    shape = CircleShape,
                    color = Color(0x99000000),
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove photo",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            isAddSlot -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add photo",
                    tint = colors.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text("Add Photo", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }

            else -> Text("Other angle", fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

/** Preview peta kecil untuk pelapor. */
@Composable
private fun LocationPreview(lat: Double?, lng: Double?, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
    ) {
        if (lat != null && lng != null) {
            StaticOsmMap(lat = lat, lng = lng, showMarker = true, modifier = Modifier.fillMaxSize())
        } else {
            Text(
                text = "Tap to pick the location on the map",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClick))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFF7F2),
            shadowElevation = 4.dp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
        ) {
            Text(
                text = "Adjust on Map",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38220F),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = colors.background) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Report Uploaded Successfully",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Let's hope you can find them ASAP!",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}

// Compose Preview
@Composable
private fun PreviewReport(state: ReportMissingState) {
    PettoTheme {
        ReportMissingContent(
            state = state,
            fallbackLat = -6.2088,
            fallbackLng = 106.8456,
            onBackClick = {},
            onPhotosPicked = {},
            onRemovePhoto = {},
            onNameChange = {},
            onSpeciesChange = {},
            onBreedChange = {},
            onDescriptionChange = {},
            onLocationPicked = { _, _ -> },
            onDateSelected = {},
            onTimeSelected = { _, _ -> },
            onPhoneChange = {},
            onRewardChange = {},
            onSubmitClick = {},
            onSuccessDismiss = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportMissingEmptyPreview() {
    PreviewReport(ReportMissingState())
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportMissingFilledPreview() {
    PreviewReport(
        ReportMissingState(
            name = "Stella",
            species = PetSpecies.DOG,
            breed = "Golden Retriever",
            description = "Wearing a pink scarf. Very friendly but easily startled by loud motorbikes.",
            pickedLat = -6.2605,
            pickedLng = 106.8106,
            areaTitle = "Kebayoran Baru",
            areaSubtitle = "Kota Jakarta Selatan, DKI Jakarta",
            regionName = "Kota Jakarta Selatan",
            dateMillisUtc = 1_792_000_000_000L,
            hour = 14,
            minute = 30,
            phoneDigits = "81234567890",
            rewardDigits = "800000",
            errorMessage = "Please add at least 1 photo"
        )
    )
}
