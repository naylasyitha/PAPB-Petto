package com.papb.petto.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.R

enum class BottomTab {
    HOME,
    LOCATION,
    COMMUNITY,
    DONATION,
    PROFILE
}

@Composable
fun FloatingBottomNavigation(
    currentTab: BottomTab = BottomTab.HOME,
    onTabSelected: (BottomTab) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentTab == BottomTab.HOME) {
                ActiveTabItem(
                    iconRes = R.drawable.home_home,
                    label = "Home",
                    onClick = { onTabSelected(BottomTab.HOME) }
                )
            } else {
                InactiveTabIcon(
                    iconRes = R.drawable.home_home,
                    contentDescription = "Home",
                    onClick = { onTabSelected(BottomTab.HOME) }
                )
            }

            if (currentTab == BottomTab.LOCATION) {
                ActiveTabItem(
                    iconRes = R.drawable.location,
                    label = "Location",
                    onClick = { onTabSelected(BottomTab.LOCATION) }
                )
            } else {
                InactiveTabIcon(
                    iconRes = R.drawable.location,
                    contentDescription = "Location",
                    onClick = { onTabSelected(BottomTab.LOCATION) }
                )
            }

            if (currentTab == BottomTab.COMMUNITY) {
                ActiveTabItem(
                    iconRes = R.drawable.community,
                    label = "Community",
                    onClick = { onTabSelected(BottomTab.COMMUNITY) }
                )
            } else {
                InactiveTabIcon(
                    iconRes = R.drawable.community,
                    contentDescription = "Community",
                    onClick = { onTabSelected(BottomTab.COMMUNITY) }
                )
            }

            if (currentTab == BottomTab.DONATION) {
                ActiveTabItem(
                    iconRes = R.drawable.donation,
                    label = "Donation",
                    onClick = { onTabSelected(BottomTab.DONATION) }
                )
            } else {
                InactiveTabIcon(
                    iconRes = R.drawable.donation,
                    contentDescription = "Donation",
                    onClick = { onTabSelected(BottomTab.DONATION) }
                )
            }

            if (currentTab == BottomTab.PROFILE) {
                ActiveTabItem(
                    iconRes = R.drawable.profile,
                    label = "Profile",
                    onClick = { onTabSelected(BottomTab.PROFILE) }
                )
            } else {
                InactiveTabIcon(
                    iconRes = R.drawable.profile,
                    contentDescription = "Profile",
                    onClick = { onTabSelected(BottomTab.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun ActiveTabItem(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(primaryColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun InactiveTabIcon(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(subtitleColor)
        )
    }
}
