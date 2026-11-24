package com.papb.petto.ui.donation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.ui.theme.PettoCardBackground
import com.papb.petto.ui.theme.PettoDarkBrownText
import com.papb.petto.ui.theme.PettoPrimaryBrown
import com.papb.petto.ui.theme.PettoSubtitleColor
import com.papb.petto.ui.theme.PettoSuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationInputBottomSheet(
    campaign: DonationCampaign,
    initialAmount: Double = 50_000.0,
    onDismissRequest: () -> Unit,
    onSubmitDonation: (donorName: String, amount: Double, paymentMethod: String, message: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedAmount by remember { mutableDoubleStateOf(initialAmount) }
    var customAmountText by remember { mutableStateOf(if (initialAmount > 0) initialAmount.toLong().toString() else "") }
    var donorName by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }
    var donorMessage by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("QRIS") }

    val quickAmounts = listOf(25_000.0, 50_000.0, 100_000.0, 250_000.0)
    val paymentMethods = listOf("QRIS", "BCA Virtual Account", "Mandiri Virtual Account", "GoPay")

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFFFFF8F5),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Donasi untuk ${campaign.shelterName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PettoDarkBrownText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = campaign.title,
                fontSize = 13.sp,
                color = PettoSubtitleColor,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pilih Nominal Donasi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PettoDarkBrownText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickAmounts.forEach { amt ->
                    val isSelected = selectedAmount == amt
                    Surface(
                        onClick = {
                            selectedAmount = amt
                            customAmountText = amt.toLong().toString()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PettoPrimaryBrown else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PettoPrimaryBrown else Color(0xFFE8DCD4)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = amt.toCompactRupiah(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else PettoDarkBrownText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customAmountText,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    customAmountText = digits
                    selectedAmount = digits.toDoubleOrNull() ?: 0.0
                },
                label = { Text("Nominal Lainnya (Rp)") },
                prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PettoPrimaryBrown,
                    unfocusedBorderColor = Color(0xFFE8DCD4),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Informasi Donatur",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PettoDarkBrownText
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = donorName,
                onValueChange = { donorName = it },
                label = { Text("Nama Lengkap") },
                enabled = !isAnonymous,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PettoPrimaryBrown,
                    unfocusedBorderColor = Color(0xFFE8DCD4),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAnonymous = !isAnonymous }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = isAnonymous,
                    onCheckedChange = { isAnonymous = it },
                    colors = CheckboxDefaults.colors(checkedColor = PettoPrimaryBrown)
                )
                Text(
                    text = "Berdonasi secara anonim (Hamba Allah)",
                    fontSize = 13.sp,
                    color = PettoDarkBrownText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = donorMessage,
                onValueChange = { donorMessage = it },
                label = { Text("Pesan atau Doa (Opsional)") },
                placeholder = { Text("Semoga cepat sembuh dan sehat selalu...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PettoPrimaryBrown,
                    unfocusedBorderColor = Color(0xFFE8DCD4),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Metode Pembayaran (Midtrans Sandbox)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PettoDarkBrownText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentMethods.forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        onClick = { selectedMethod = method },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) PettoPrimaryBrown else Color(0xFFE8DCD4)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = method,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PettoPrimaryBrown else PettoDarkBrownText
                            )
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(
                                        2.dp,
                                        if (isSelected) PettoPrimaryBrown else Color.Gray,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(PettoPrimaryBrown, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val finalName = if (isAnonymous || donorName.isBlank()) "Hamba Allah" else donorName.trim()
                    val finalAmount = if (selectedAmount > 0) selectedAmount else 10_000.0
                    onSubmitDonation(
                        finalName,
                        finalAmount,
                        selectedMethod,
                        donorMessage.ifBlank { null }
                    )
                },
                enabled = selectedAmount > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PettoPrimaryBrown),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Lanjut Pembayaran (${selectedAmount.toRupiahFormat()})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MidtransSandboxDialog(
    state: CheckoutState.Ready,
    onSuccessCallback: (DonationTransaction) -> Unit,
    onFailureCallback: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF009688), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Midtrans Sandbox Gateway",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoDarkBrownText
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Transaksi simulasi sandbox berhasil dibuat. Silakan pilih hasil simulasi transaksi untuk pengujian sistem Petto:",
                    fontSize = 13.sp,
                    color = PettoSubtitleColor,
                    lineHeight = 18.sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF7F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8DCD4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order ID:", fontSize = 12.sp, color = PettoSubtitleColor)
                            Text(
                                state.transaction.transactionId,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoDarkBrownText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nominal:", fontSize = 12.sp, color = PettoSubtitleColor)
                            Text(
                                state.transaction.amount.toRupiahFormat(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoPrimaryBrown
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Metode:", fontSize = 12.sp, color = PettoSubtitleColor)
                            Text(
                                state.transaction.paymentMethod,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PettoDarkBrownText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Snap Token:", fontSize = 11.sp, color = PettoSubtitleColor)
                            Text(
                                state.snapToken.take(16) + "...",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onSuccessCallback(state.transaction) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PettoSuccessGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓ Simulasikan Pembayaran Sukses", fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.redirectUrl))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Buka Web Sandbox Midtrans ↗", color = PettoPrimaryBrown, fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = { onFailureCallback("Pembayaran dibatalkan oleh pengguna.") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batalkan Pembayaran", color = Color(0xFFBA1A1A))
                }
            }
        }
    )
}

@Composable
fun DonationSuccessDialog(
    transaction: DonationTransaction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎉", fontSize = 28.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Donasi Berhasil!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoDarkBrownText,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Terima kasih, ${transaction.donorName}!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PettoPrimaryBrown,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Donasi sebesar ${transaction.amount.toRupiahFormat()} berhasil disalurkan dan tercatat di sistem.",
                    fontSize = 13.sp,
                    color = PettoSubtitleColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                if (!transaction.message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF7F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${transaction.message}\"",
                            fontSize = 12.sp,
                            color = PettoDarkBrownText,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PettoPrimaryBrown),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Selesai & Kembali", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    )
}
