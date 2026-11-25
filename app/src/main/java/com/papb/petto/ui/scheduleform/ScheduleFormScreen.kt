package com.papb.petto.ui.scheduleform

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.ui.theme.PettoTheme

@Composable
fun ScheduleFormScreen(
    isEditMode: Boolean = false,
    scheduleId: String? = null,
    viewModel: ScheduleFormViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onFormSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isEditMode, scheduleId) {
        viewModel.setupForm(isEditMode, scheduleId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onFormSuccess()
        }
    }

    ScheduleFormContent(
        state = uiState,
        onBackClick = onBackClick,
        onCareTypeChange = viewModel::onCareTypeChange,
        onCareNameChange = viewModel::onCareNameChange,
        onLastDoneDateChange = viewModel::onLastDoneDateChange,
        onIntervalValueChange = viewModel::onIntervalValueChange,
        onIntervalUnitSelected = viewModel::onIntervalUnitSelected,
        onIntervalDropdownExpandedChange = viewModel::onIntervalDropdownExpandedChange,
        onRemind3DaysChange = viewModel::onRemind3DaysChange,
        onRemind1DayChange = viewModel::onRemind1DayChange,
        onNotesChange = viewModel::onNotesChange,
        onSaveClick = { viewModel.onSaveScheduleClicked(context) }
    )
}

@Composable
fun ScheduleFormContent(
    state: ScheduleFormState,
    onBackClick: () -> Unit,
    onCareTypeChange: (CareTypeOption) -> Unit,
    onCareNameChange: (String) -> Unit,
    onLastDoneDateChange: (String) -> Unit,
    onIntervalValueChange: (String) -> Unit,
    onIntervalUnitSelected: (String) -> Unit,
    onIntervalDropdownExpandedChange: (Boolean) -> Unit,
    onRemind3DaysChange: (Boolean) -> Unit,
    onRemind1DayChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    val headerTitle = if (state.isEditMode) "Edit Schedule" else "Add Schedule"
    val submitButtonText = if (state.isEditMode) "Save Changes" else "Save Schedule"

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

            // --- CARE TYPE SELECTION CHIPS ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Care Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareTypeChip(
                        label = "Vaccination",
                        isSelected = state.careType == CareTypeOption.VACCINATION,
                        onClick = { onCareTypeChange(CareTypeOption.VACCINATION) },
                        modifier = Modifier.weight(1f)
                    )
                    CareTypeChip(
                        label = "Medication",
                        isSelected = state.careType == CareTypeOption.MEDICATION,
                        onClick = { onCareTypeChange(CareTypeOption.MEDICATION) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareTypeChip(
                        label = "Grooming",
                        isSelected = state.careType == CareTypeOption.GROOMING,
                        onClick = { onCareTypeChange(CareTypeOption.GROOMING) },
                        modifier = Modifier.weight(1f)
                    )
                    CareTypeChip(
                        label = "Other",
                        isSelected = state.careType == CareTypeOption.OTHER,
                        onClick = { onCareTypeChange(CareTypeOption.OTHER) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- FORM: CARE NAME ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Care Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.careName,
                    onValueChange = onCareNameChange,
                    placeholder = { Text("Rabies vaccine", color = Color.Gray) },
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

            // --- FORM: LAST DONE DATE ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Last Done Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.lastDoneDate,
                    onValueChange = onLastDoneDateChange,
                    placeholder = { Text("Oct 20, 2025", color = Color.Gray) },
                    singleLine = true,
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Select Date",
                            modifier = Modifier.size(20.dp)
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM ROW: REPEAT INTERVAL ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Repeat Interval",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Interval Number Input
                    OutlinedTextField(
                        value = state.repeatIntervalValue,
                        onValueChange = onIntervalValueChange,
                        placeholder = { Text("12", color = Color.Gray) },
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
                        modifier = Modifier.weight(1f)
                    )

                    // Interval Unit Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.repeatIntervalUnit,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.dropdown),
                                    contentDescription = "Dropdown",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onIntervalDropdownExpandedChange(!state.isIntervalDropdownExpanded) }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIntervalDropdownExpandedChange(!state.isIntervalDropdownExpanded) }
                        )

                        DropdownMenu(
                            expanded = state.isIntervalDropdownExpanded,
                            onDismissRequest = { onIntervalDropdownExpandedChange(false) },
                            modifier = Modifier.background(colors.surfaceVariant)
                        ) {
                            state.intervalUnitOptions.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit, color = colors.onBackground) },
                                    onClick = { onIntervalUnitSelected(unit) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- NEXT DUE DATE BANNER (CALCULATED AUTOMATICALLY) ---
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Due Date",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Green Badge "Calculated automatically"
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32)
                        ) {
                            Text(
                                text = "Calculated automatically",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.nextDueDate,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // White Bell Icon Badge
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.notification),
                                    contentDescription = "Notification Bell",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.calculationFormulaText,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- REMINDERS & ALERTS SECTION ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Reminders & Alerts",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Row 1: H-3 Reminder
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = colors.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Image(
                                            painter = painterResource(id = R.drawable.notification),
                                            contentDescription = "Bell",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Remind 3 days before",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onBackground
                                    )
                                    Text(
                                        text = state.remind3DaysText,
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = state.remind3DaysBefore,
                                onCheckedChange = onRemind3DaysChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.primary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.outline.copy(alpha = 0.5f)
                                )
                            )
                        }

                        HorizontalDivider(
                            color = colors.outline.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Row 2: H-1 Reminder
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = colors.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Image(
                                            painter = painterResource(id = R.drawable.notification),
                                            contentDescription = "Bell",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Remind 1 day before",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.onBackground
                                    )
                                    Text(
                                        text = state.remind1DayText,
                                        fontSize = 12.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = state.remind1DayBefore,
                                onCheckedChange = onRemind1DayChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.primary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.outline.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- FORM: NOTES (OPTIONAL) ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Optional",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("Add clinic name, veterinarian recommendations, or preparation notes...", color = Color.Gray) },
                    singleLine = false,
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

            // --- PRIMARY SUBMIT BUTTON ---
            Button(
                onClick = onSaveClick,
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

// --- Care Type Choice Chip Component ---
@Composable
private fun CareTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) colors.primary else colors.surfaceVariant,
        modifier = modifier
            .height(50.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) colors.primary else colors.outline,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else colors.onBackground
            )
        }
    }
}

// --- Compose Previews for Add & Edit Schedule ---
@Preview(name = "Add Schedule Mode", showBackground = true, showSystemUi = true)
@Composable
fun AddScheduleModePreview() {
    PettoTheme {
        ScheduleFormContent(
            state = ScheduleFormState(isEditMode = false),
            onBackClick = {},
            onCareTypeChange = {},
            onCareNameChange = {},
            onLastDoneDateChange = {},
            onIntervalValueChange = {},
            onIntervalUnitSelected = {},
            onIntervalDropdownExpandedChange = {},
            onRemind3DaysChange = {},
            onRemind1DayChange = {},
            onNotesChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(name = "Edit Schedule Mode", showBackground = true, showSystemUi = true)
@Composable
fun EditScheduleModePreview() {
    PettoTheme {
        ScheduleFormContent(
            state = ScheduleFormState(
                isEditMode = true,
                careName = "Annual Rabies Vaccination",
                lastDoneDate = "Oct 17, 2025",
                repeatIntervalValue = "12",
                repeatIntervalUnit = "Months",
                nextDueDate = "Oct 17, 2026",
                calculationFormulaText = "Calculated from Last Done Date + 12 months interval.",
                remind3DaysText = "Push alert on Oct 14, 2026",
                remind1DayText = "Push alert on Oct 16, 2026",
                notes = "Klinik Satwa Sehat recommendations"
            ),
            onBackClick = {},
            onCareTypeChange = {},
            onCareNameChange = {},
            onLastDoneDateChange = {},
            onIntervalValueChange = {},
            onIntervalUnitSelected = {},
            onIntervalDropdownExpandedChange = {},
            onRemind3DaysChange = {},
            onRemind1DayChange = {},
            onNotesChange = {},
            onSaveClick = {}
        )
    }
}
