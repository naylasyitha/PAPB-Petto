package com.papb.petto.ui.missing

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.papb.petto.ui.theme.PettoTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.papb.petto.ui.components.StaticOsmMap
import com.papb.petto.util.PriorityAreaCalculator

@Composable
fun MissingDetailDialog(
    item: MissingCardItem,
    onDismiss: () -> Unit,
    onMarkFound: ((MissingCardItem) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        MissingDetailContent(item = item, onDismiss = onDismiss, onMarkFound = onMarkFound)
    }
}

@Composable
fun MissingDetailContent(
    item: MissingCardItem,
    onDismiss: () -> Unit,
    onMarkFound: ((MissingCardItem) -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    var showFoundConfirm by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(max = 720.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (item.photoUrls.isNotEmpty()) {
                Box {
                    PetPhotoPager(
                        photoUrls = item.photoUrls,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxWidth().height(220.dp)
                    )
                    item.rewardText?.let {
                        RewardChip(
                            text = "Reward: $it",
                            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    modifier = Modifier.weight(1f)
                )
                BreedTag(text = item.breed)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${item.species.label} • Last seen ${item.lastSeenDateText} • ${item.postedAgoText}",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )
            item.distanceText?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            if (!item.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = colors.onBackground,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Priority search area",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Text(
                text = "Start searching within about ${PriorityAreaCalculator.formatRadius(item.radiusMeters)}. " +
                        "The area grows the longer the pet has been missing.",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            PriorityAreaMap(
                lat = item.centerLat,
                lng = item.centerLng,
                radiusMeters = item.radiusMeters,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Contact",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Text(
                text = item.phone,
                fontSize = 15.sp,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.phone}"))
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Call", fontWeight = FontWeight.Bold, color = Color.White)
                }
                OutlinedButton(
                    onClick = {
                        val number = item.phone.filter { it.isDigit() }
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$number"))
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("WhatsApp", fontWeight = FontWeight.Bold, color = colors.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (item.isOwner && onMarkFound != null) {
                Button(
                    onClick = { showFoundConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mark as Found", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close", fontWeight = FontWeight.Bold, color = colors.onBackground)
            }
        }
    }

    if (showFoundConfirm) {
        AlertDialog(
            onDismissRequest = { showFoundConfirm = false },
            title = { Text("Mark ${item.name} as found?", fontWeight = FontWeight.Bold) },
            text = { Text("The report will be removed from the missing list for everyone. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showFoundConfirm = false
                    onMarkFound?.invoke(item)
                }) { Text("Yes, found!") }
            },
            dismissButton = {
                TextButton(onClick = { showFoundConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

/** Peta dengan lingkaran area prioritas. Titik pusat TIDAK ditandai. */
@Composable
fun PriorityAreaMap(
    lat: Double,
    lng: Double,
    radiusMeters: Double,
    modifier: Modifier = Modifier
) {
    StaticOsmMap(
        lat = lat,
        lng = lng,
        radiusMeters = radiusMeters,
        modifier = modifier
    )
}

@Composable
fun BreedTag(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFE0CC),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD84315),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun RewardChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFF7F2),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38220F),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun MissingDetailPreview() {
    PettoTheme {
        MissingDetailContent(
            item = MissingPreviewData.stella,
            onDismiss = {},
            onMarkFound = {}
        )
    }
}
