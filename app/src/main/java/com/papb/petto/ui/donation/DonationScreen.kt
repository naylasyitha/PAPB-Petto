package com.papb.petto.ui.donation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.R
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.components.FloatingBottomNavigation
import com.papb.petto.ui.theme.PettoDarkBrownText
import com.papb.petto.ui.theme.PettoPrimaryBrown
import com.papb.petto.ui.theme.PettoProgressBarFill
import com.papb.petto.ui.theme.PettoProgressBarTrack
import com.papb.petto.ui.theme.PettoSubtitleColor
import com.papb.petto.ui.theme.PettoSuccessGreen
import com.papb.petto.ui.theme.PettoUrgentRed

@Composable
fun DonationScreen(
    uiState: DonationUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onCampaignClick: (String) -> Unit,
    onDonateNowClick: (DonationCampaign) -> Unit,
    onQuickDonate: (DonationCampaign, Double) -> Unit,
    onBack: () -> Unit = {},
    currentTab: BottomTab = BottomTab.DONATION,
    onTabSelected: (BottomTab) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F5))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DonationAppBar(onBack = onBack)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                item {
                    ImpactSummaryBanner()
                    Spacer(modifier = Modifier.height(14.dp))
                }

                item {
                    DonationSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchQueryChange
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    CategoryFilterRow(
                        selectedCategory = uiState.selectedCategory,
                        onSelectCategory = onSelectCategory
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PettoPrimaryBrown)
                        }
                    }
                } else if (uiState.filteredCampaigns.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada kampanye yang cocok dengan pencarian.",
                                fontSize = 14.sp,
                                color = PettoSubtitleColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(uiState.filteredCampaigns, key = { it.id }) { campaign ->
                        DonationCampaignCard(
                            campaign = campaign,
                            onClick = { onCampaignClick(campaign.id) },
                            onDonateClick = { onDonateNowClick(campaign) },
                            onQuickGiftClick = { amount -> onQuickDonate(campaign, amount) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            FloatingBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
private fun DonationAppBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFFFFF8F5))
            .border(width = 0.5.dp, color = Color(0xFFF4EBE5))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color(0xFFFBF1E9),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PettoDarkBrownText)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Shelter Donation",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PettoDarkBrownText
            )
        }

        Surface(
            shape = CircleShape,
            color = Color(0xFFFBF1E9),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.notification),
                    contentDescription = "Notification",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ImpactSummaryBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFE3D0),
                            Color(0xFFFFEADD),
                            Color(0xFFFFF1E9)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFFFF0E8),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9F4A15)),
                    modifier = Modifier.height(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2F7D31))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MONTHLY COMMUNITY IMPACT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9F4A15),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Together We Saved 1,420+ Strays This Month",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoDarkBrownText,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp 184M raised across 24 verified shelters in Indonesia",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PettoSubtitleColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricHighlightCard(
                        value = "Rp 184M",
                        label = "Raised",
                        modifier = Modifier.weight(1f)
                    )
                    MetricHighlightCard(
                        value = "24",
                        label = "Shelters",
                        modifier = Modifier.weight(1f)
                    )
                    MetricHighlightCard(
                        value = "1,420+",
                        label = "Strays Saved",
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricHighlightCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFF8F5),
        shadowElevation = 1.dp,
        modifier = modifier.height(48.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PettoPrimaryBrown
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = PettoDarkBrownText
            )
        }
    }
}

@Composable
private fun DonationSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari shelter, lokasi, atau kampanye...") },
        leadingIcon = {
            Text("🔍", fontSize = 16.sp)
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PettoPrimaryBrown,
            unfocusedBorderColor = Color(0xFFE8DCD4),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf(
        "All",
        "Urgent First",
        "Emergency Medical",
        "Food & Medical",
        "Shelter Renovation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory.equals(category, ignoreCase = true)
            Surface(
                onClick = { onSelectCategory(category) },
                shape = RoundedCornerShape(50.dp),
                color = if (isSelected) PettoPrimaryBrown else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) PettoPrimaryBrown else Color(0xFFE8DCD4)
                ),
                modifier = Modifier.height(34.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else PettoDarkBrownText
                    )
                }
            }
        }
    }
}

@Composable
fun DonationCampaignCard(
    campaign: DonationCampaign,
    onClick: () -> Unit,
    onDonateClick: () -> Unit,
    onQuickGiftClick: (Double) -> Unit
) {
    val remainingDays = campaign.getRemainingDays()
    val isUrgent = remainingDays in 1..2 && campaign.status == "ACTIVE"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = if (isUrgent) 1.5.dp else 1.dp,
                color = if (isUrgent) Color(0xFF622906) else Color(0xFFE8DCD4),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.donation_home),
                    contentDescription = campaign.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x9927180D)),
                                startY = 80f
                            )
                        )
                )

                if (isUrgent) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = PettoUrgentRed,
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("🚨", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "URGENT • $remainingDays DAYS LEFT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                } else if (campaign.status == "COMPLETED") {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFF2E7D32),
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "COMPLETED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = campaign.shelterName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (campaign.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF186A22),
                                modifier = Modifier.size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0x66000000),
                        modifier = Modifier.height(20.dp)
                    ) {
                        Text(
                            text = campaign.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = campaign.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoDarkBrownText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "📍 ${campaign.location}",
                    fontSize = 12.sp,
                    color = PettoSubtitleColor
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { (campaign.percentage / 100f).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PettoProgressBarFill,
                    trackColor = PettoProgressBarTrack
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = campaign.collectedAmount.toRupiahFormat(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoPrimaryBrown
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${String.format("%.0f", campaign.percentage)}%)",
                                fontSize = 12.sp,
                                color = PettoSubtitleColor
                            )
                        }
                        Text(
                            text = "Target: ${campaign.targetAmount.toRupiahFormat()}",
                            fontSize = 11.sp,
                            color = PettoSubtitleColor
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = "Remaining Days",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$remainingDays hari lagi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUrgent) PettoUrgentRed else PettoDarkBrownText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Quick Gift:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PettoSubtitleColor
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(25_000.0, 50_000.0, 100_000.0).forEach { giftAmount ->
                        OutlinedButton(
                            onClick = { onQuickGiftClick(giftAmount) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8DCD4)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                        ) {
                            Text(
                                text = giftAmount.toCompactRupiah(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoPrimaryBrown
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDonateClick,
                    enabled = campaign.status == "ACTIVE",
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUrgent) Color(0xFF9E4814) else PettoPrimaryBrown
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (campaign.status == "ACTIVE") "Donate Now" else "Campaign Completed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
