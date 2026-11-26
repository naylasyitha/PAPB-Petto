package com.papb.petto.ui.donation

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.R
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.model.FundAllocationItem
import com.papb.petto.ui.theme.PettoDarkBrownText
import com.papb.petto.ui.theme.PettoPrimaryBrown
import com.papb.petto.ui.theme.PettoProgressBarFill
import com.papb.petto.ui.theme.PettoProgressBarTrack
import com.papb.petto.ui.theme.PettoSubtitleColor
import com.papb.petto.ui.theme.PettoSuccessGreen
import com.papb.petto.ui.theme.PettoUrgentRed

@Composable
fun CampaignDetailScreen(
    campaign: DonationCampaign,
    recentDonors: List<DonationTransaction>,
    onBack: () -> Unit,
    onDonateNowClick: (DonationCampaign) -> Unit
) {
    val scrollState = rememberScrollState()
    val remainingDays = campaign.getRemainingDays()
    val isUrgent = remainingDays in 1..2 && campaign.status == "ACTIVE"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
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
                                colors = listOf(
                                    Color(0x9927180D),
                                    Color.Transparent,
                                    Color(0xCC27180D)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = Color(0xCCFFFFFF),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PettoDarkBrownText)
                        }
                    }

                    if (isUrgent) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = PettoUrgentRed
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("🚨", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "URGENT • $remainingDays DAYS LEFT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = campaign.location,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFF8F5)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0x6627180D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Text(
                            text = campaign.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color(0xFFE8DCD4), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFDBCB),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🐾", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = campaign.shelterName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PettoDarkBrownText
                                )
                                if (campaign.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF186A22),
                                        modifier = Modifier.size(15.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("✓", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Text(
                                text = "Penyelenggara Terverifikasi",
                                fontSize = 11.sp,
                                color = PettoSubtitleColor
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(50.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9E4814)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Info Shelter",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E4814)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color(0xFFE8DCD4), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = campaign.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PettoDarkBrownText,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { (campaign.percentage / 100f).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = PettoProgressBarFill,
                        trackColor = PettoProgressBarTrack
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = campaign.collectedAmount.toRupiahFormat(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoPrimaryBrown
                            )
                            Text(
                                text = "terkumpul dari ${campaign.targetAmount.toRupiahFormat()}",
                                fontSize = 12.sp,
                                color = PettoSubtitleColor
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF0E8)
                        ) {
                            Text(
                                text = "${String.format("%.0f", campaign.percentage)}% Tercapai",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoPrimaryBrown,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⏱ $remainingDays hari lagi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUrgent) PettoUrgentRed else PettoDarkBrownText
                        )
                        Text(
                            text = "👥 ${recentDonors.size + 12} Donatur Berpartisipasi",
                            fontSize = 12.sp,
                            color = PettoSubtitleColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "About this Campaign",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoDarkBrownText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE8DCD4), RoundedCornerShape(14.dp))
                ) {
                    Text(
                        text = campaign.description,
                        fontSize = 13.sp,
                        color = PettoDarkBrownText,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transparent Fund Allocation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PettoDarkBrownText
                    )
                    Text(
                        text = "100% Verified",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PettoSuccessGreen
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE8DCD4), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        campaign.fundAllocations.forEach { item ->
                            FundAllocationRow(item = item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Donors",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PettoDarkBrownText
                    )
                    Text(
                        text = "${recentDonors.size} donasi tercatat",
                        fontSize = 12.sp,
                        color = PettoSubtitleColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE8DCD4), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (recentDonors.isEmpty()) {
                            Text(
                                text = "Belum ada donatur terbaru. Jadilah yang pertama membantu shelter ini!",
                                fontSize = 12.sp,
                                color = PettoSubtitleColor,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
                            recentDonors.forEach { donor ->
                                RecentDonorItem(transaction = donor)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Surface(
            shadowElevation = 12.dp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { onDonateNowClick(campaign) },
                    enabled = campaign.status == "ACTIVE",
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PettoPrimaryBrown),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (campaign.status == "ACTIVE") "Donate Now" else "Campaign Completed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FundAllocationRow(item: FundAllocationItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.categoryName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PettoDarkBrownText,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.amount.toRupiahFormat(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PettoPrimaryBrown
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { (item.percentage / 100f) },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PettoPrimaryBrown,
                trackColor = Color(0xFFF1E4DC)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "${item.percentage}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PettoSubtitleColor
            )
        }
    }
}

@Composable
private fun RecentDonorItem(transaction: DonationTransaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFFBF1E9),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = transaction.donorName.firstOrNull()?.uppercase() ?: "H",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoPrimaryBrown
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.donorName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoDarkBrownText
                )
                Text(
                    text = transaction.amount.toRupiahFormat(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoPrimaryBrown
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatRelativeTime(transaction.timestamp),
                    fontSize = 11.sp,
                    color = PettoSubtitleColor
                )
                Text(
                    text = transaction.paymentMethod,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            if (!transaction.message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "\"${transaction.message}\"",
                    fontSize = 11.sp,
                    color = PettoSubtitleColor
                )
            }
        }
    }
}
