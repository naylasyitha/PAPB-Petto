package com.papb.petto.ui.home

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.components.FloatingBottomNavigation
import com.papb.petto.ui.theme.PettoTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNotificationClick: () -> Unit = {},
    onQuickAccessClick: (String) -> Unit = {},
    onNavigateToAllCampaigns: () -> Unit = {},
    onDonateClick: () -> Unit = {},
    onNavigateToServiceMap: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        state = uiState,
        onTabSelected = viewModel::onTabSelected,
        onNotificationClick = onNotificationClick,
        onQuickAccessClick = onQuickAccessClick,
        onNavigateToAllCampaigns = onNavigateToAllCampaigns,
        onDonateClick = onDonateClick,
        onNavigateToServiceMap = onNavigateToServiceMap
    )
}

@Composable
fun HomeContent(
    state: HomeScreenState,
    onTabSelected: (BottomTab) -> Unit,
    onNotificationClick: () -> Unit,
    onQuickAccessClick: (String) -> Unit,
    onNavigateToAllCampaigns: () -> Unit,
    onDonateClick: () -> Unit,
    onNavigateToServiceMap: () -> Unit = {}
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
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, ${state.userName}!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.petCountText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = onNotificationClick,
                    shape = CircleShape,
                    color = colors.surfaceVariant,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.notification),
                            contentDescription = "Notification",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.quick_access_bg),
                        contentDescription = "Quick Access Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Quick Access",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickAccessCard(
                                iconRes = R.drawable.report_missing_pet,
                                label = "Report\na missing pet",
                                onClick = { onQuickAccessClick("missing_pet") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            QuickAccessCard(
                                iconRes = R.drawable.find_clinic,
                                label = "Find\na clinic",
                                onClick = { onQuickAccessClick("find_clinic") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            QuickAccessCard(
                                iconRes = R.drawable.read_articles,
                                label = "Read\narticles",
                                onClick = { onQuickAccessClick("articles") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pets Schedule",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            state.schedules.forEach { schedule ->
                PetScheduleCard(item = schedule)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nearby services",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            NearbyServicesCard(
                summaryText = state.nearbySummaryText,
                onMapClick = onNavigateToServiceMap
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Donation",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(
                    text = "All campaigns",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier.clickable { onNavigateToAllCampaigns() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            DonationCard(
                state = state,
                onDonateClick = onDonateClick
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

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

@Composable
private fun QuickAccessCard(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBrown = MaterialTheme.colorScheme.primary

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF7F2),
        modifier = modifier.height(105.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = primaryBrown,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38220F),
                lineHeight = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PetScheduleCard(item: PetScheduleItem) {
    val colors = MaterialTheme.colorScheme

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
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF382F2A)
                    ) {
                        Text(
                            text = item.petName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                val badgeColor = if (item.isOverdue) Color(0xFFC62828) else Color(0xFFD84315)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = item.statusTag,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.dateLocation,
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NearbyServicesCard(
    summaryText: String,
    onMapClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.map_home),
                    contentDescription = "Map Nearby Services",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF7F2),
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Klinik Dr. Satwa (850 m)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38220F)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF7F2)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PetShop Kemang (1.2 km)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38220F)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = summaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onMapClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    modifier = Modifier.height(42.dp)
                ) {
                    Text(
                        text = "Map ↗",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DonationCard(
    state: HomeScreenState,
    onDonateClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.donation_home),
                    contentDescription = "Donation Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xCC2C221E)
                    ) {
                        Text(
                            text = state.donationShelter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xDCD84315)
                    ) {
                        Text(
                            text = "Verified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                    text = state.donationTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = state.donationSubtitle,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { state.donationPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colors.primary,
                    trackColor = Color(0xFFE0D5CC)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "Raised ",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = "${state.donationRaisedText} ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                        Text(
                            text = "(${state.donationPercentage}%)",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = "Time left",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = state.donationDaysLeft,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDonateClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Donate now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    PettoTheme {
        HomeContent(
            state = HomeScreenState(),
            onTabSelected = {},
            onNotificationClick = {},
            onQuickAccessClick = {},
            onNavigateToAllCampaigns = {},
            onDonateClick = {}
        )
    }
}
