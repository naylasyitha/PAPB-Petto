package com.papb.petto.ui.missing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.papb.petto.ui.theme.PettoTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.PetSpecies

private const val DESCRIPTION_PREVIEW_CHARS = 110

/** Tab "Missing" di CommunityScreen */
@Composable
fun MissingPetsContent(
    state: MissingPetsState,
    onRefresh: () -> Unit,
    onSearchChange: (String) -> Unit,
    onSpeciesSelected: (PetSpecies?) -> Unit,
    onRegionSelected: (String?) -> Unit,
    onPageSelected: (Int) -> Unit,
    onCardClick: (MissingCardItem) -> Unit,
    onDetailDismiss: () -> Unit,
    onMarkFound: (MissingCardItem) -> Unit,
    onLocationDialogConfirm: () -> Unit,
    onLocationDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) { onRefresh() }

    val systemBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp, top = 12.dp, bottom = 180.dp + systemBarBottom
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            RegionSelector(
                state = state,
                onRegionSelected = onRegionSelected
            )
        }

        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                placeholder = {
                    Text(
                        "Search by name, breed, or color...",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.background,
                    unfocusedContainerColor = colors.background,
                    focusedBorderColor = colors.outline,
                    unfocusedBorderColor = colors.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            SpeciesChips(
                state = state,
                onSpeciesSelected = onSpeciesSelected
            )
        }

        when {
            state.isLoading && state.items.isEmpty() -> item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
            }

            state.errorMessage != null && state.items.isEmpty() -> item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage,
                        color = colors.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = onRefresh) { Text("Try again") }
                }
            }

            state.items.isEmpty() -> item {
                Text(
                    text = "No missing pets found here. Good news!",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(32.dp)
                )
            }

            else -> {
                items(state.items.size) { index ->
                    val item = state.items[index]
                    MissingPetCard(item = item, onClick = { onCardClick(item) })
                }
                if (state.totalPages > 1) {
                    item { Pagination(state = state, onPageSelected = onPageSelected) }
                }
            }
        }
    }

    state.selectedItem?.let { MissingDetailDialog(item = it, onDismiss = onDetailDismiss, onMarkFound = onMarkFound) }

    if (state.showLocationDialog) {
        AlertDialog(
            onDismissRequest = onLocationDialogDismiss,
            title = { Text("Show pets near you?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Petto uses your approximate location to show how far each search area is " +
                            "and to alert you about missing pets nearby. You can still browse by area without it."
                )
            },
            confirmButton = {
                TextButton(onClick = onLocationDialogConfirm) { Text("Allow") }
            },
            dismissButton = {
                TextButton(onClick = onLocationDialogDismiss) { Text("Not now") }
            }
        )
    }
}

@Composable
private fun RegionSelector(
    state: MissingPetsState,
    onRegionSelected: (String?) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    val defaultLabel = if (state.hasUserLocation) "Near me" else "All areas"

    Box {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = colors.surfaceVariant,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = state.selectedRegion ?: defaultLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onBackground
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(defaultLabel) },
                onClick = {
                    expanded = false
                    onRegionSelected(null)
                }
            )
            state.regions.forEach { region ->
                DropdownMenuItem(
                    text = { Text("${region.name} (${region.count})") },
                    onClick = {
                        expanded = false
                        onRegionSelected(region.name)
                    }
                )
            }
        }
    }
}

@Composable
private fun SpeciesChips(
    state: MissingPetsState,
    onSpeciesSelected: (PetSpecies?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                label = "All Missing (${state.totalCount})",
                selected = state.selectedSpecies == null,
                onClick = { onSpeciesSelected(null) }
            )
        }
        items(PetSpecies.entries.size) { index ->
            val species = PetSpecies.entries[index]
            FilterChip(
                label = "${species.chipLabel} (${state.speciesCounts[species] ?: 0})",
                selected = state.selectedSpecies == species,
                onClick = { onSpeciesSelected(species) }
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFFD9651E) else colors.background,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else colors.onBackground,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun MissingPetCard(item: MissingCardItem, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.background),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, Color(0xFFB5541C), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box {
                PetPhotoPager(
                    photoUrls = item.photoUrls,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxWidth().height(210.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xCC2C221E),
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                ) {
                    Text(
                        text = item.postedAgoText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                item.rewardText?.let {
                    RewardChip(
                        text = "Reward: $it",
                        modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(8.dp))
                BreedTag(text = item.breed)
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = item.lastSeenDateText,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
                item.distanceText?.let {
                    Spacer(Modifier.size(8.dp))
                    Box(Modifier.size(4.dp).clip(CircleShape).background(colors.outline))
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            if (!item.description.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                val preview = if (item.description.length > DESCRIPTION_PREVIEW_CHARS) {
                    item.description.take(DESCRIPTION_PREVIEW_CHARS).trimEnd() + "…"
                } else item.description
                Text(
                    text = preview,
                    fontSize = 14.sp,
                    color = colors.onBackground,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = "See Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun Pagination(state: MissingPetsState, onPageSelected: (Int) -> Unit) {
    val colors = MaterialTheme.colorScheme
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
    ) {
        items(state.totalPages) { index ->
            val page = index + 1
            val selected = page == state.page
            Surface(
                shape = CircleShape,
                color = if (selected) colors.primary else colors.surfaceVariant,
                modifier = Modifier.size(34.dp).clickable { onPageSelected(page) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$page",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1600)
@Composable
fun MissingPetsContentPreview() {
    PettoTheme {
        MissingPetsContent(
            state = MissingPreviewData.state,
            onRefresh = {},
            onSearchChange = {},
            onSpeciesSelected = {},
            onRegionSelected = {},
            onPageSelected = {},
            onCardClick = {},
            onDetailDismiss = {},
            onMarkFound = {},
            onLocationDialogConfirm = {},
            onLocationDialogDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MissingPetsEmptyPreview() {
    PettoTheme {
        MissingPetsContent(
            state = MissingPetsState(isLoading = false, hasUserLocation = true),
            onRefresh = {},
            onSearchChange = {},
            onSpeciesSelected = {},
            onRegionSelected = {},
            onPageSelected = {},
            onCardClick = {},
            onDetailDismiss = {},
            onMarkFound = {},
            onLocationDialogConfirm = {},
            onLocationDialogDismiss = {}
        )
    }
}
