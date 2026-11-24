package com.papb.petto.ui.servicemap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.Service
import java.util.Locale

private val categories = listOf(
    "All" to "All",
    "clinic" to "Clinic",
    "petshop" to "Pet Shop",
    "shelter" to "Shelter"
)

@Composable
fun ServiceMapScreen(
    uiState: ServiceMapUiState,
    onQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDirectionsClick: (Service) -> Unit,
    onCallClick: (Service) -> Unit,
    onServiceClick: (Service) -> Unit,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.size(12.dp))
            Text(
                "Services Map",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search vet clinic, pet shop, or shelter", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = CircleShape
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (value, label) ->
                val selected = uiState.selectedCategory == value
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selected) colors.primary else colors.surfaceVariant)
                        .clickable { onCategoryChange(value) }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = if (selected) colors.onPrimary else colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceVariant)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "${uiState.services.size} layanan ditemukan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.onBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (uiState.hasLocation) {
                        "Diurutkan dari yang terdekat dengan posisimu"
                    } else {
                        "Aktifkan lokasi untuk melihat jarak"
                    },
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Nearby Services",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = colors.onBackground
        )

        Spacer(Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
            }

            uiState.errorMessage != null -> {
                Text(uiState.errorMessage, color = colors.onSurfaceVariant)
            }

            uiState.services.isEmpty() -> {
                Text("Tidak ada layanan yang cocok", color = colors.onSurfaceVariant)
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.services) { service ->
                        ServiceCard(
                            service = service,
                            onDirectionsClick = { onDirectionsClick(service) },
                            onCallClick = { onCallClick(service) },
                            onClick = { onServiceClick(service) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: Service,
    onDirectionsClick: () -> Unit,
    onCallClick: () -> Unit,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.primary.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Pets,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.size(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = if (service.isOpenNow) {
                        "Open Now · Closes ${service.openHours?.substringAfter("- ") ?: "-"}"
                    } else {
                        "Visiting: ${service.openHours ?: "-"}"
                    },
                    fontSize = 11.sp,
                    color = if (service.isOpenNow) colors.primary else colors.onSurfaceVariant
                )
                Text(
                    service.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.onBackground
                )
                Text(
                    service.address,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildString {
                        service.rating?.let { append("$it  ") }
                        service.reviewCount?.let { append("$it Reviews  ") }
                        service.distanceKm?.let {
                            append(String.format(Locale.getDefault(), "%.1f km", it))
                        }
                    },
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onDirectionsClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("Directions", fontSize = 13.sp)
            }

            IconButton(
                onClick = onCallClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Telepon", tint = colors.primary)
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Simpan", tint = colors.primary)
            }
        }
    }
}