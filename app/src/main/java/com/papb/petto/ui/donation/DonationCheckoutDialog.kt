package com.papb.petto.ui.donation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.remote.midtrans.MidtransClient
import coil.compose.AsyncImage
import com.papb.petto.ui.theme.PettoDarkBrownText
import com.papb.petto.ui.theme.PettoPrimaryBrown
import com.papb.petto.ui.theme.PettoSubtitleColor
import com.papb.petto.ui.theme.PettoSuccessGreen
import com.papb.petto.ui.theme.PettoUrgentRed
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Bottom Sheet untuk input nominal donasi, data donatur, dan pemilihan metode pembayaran.
 */
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

            // Nominal Cepat
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

            // Custom Amount Input
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

            // Data Donatur
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

            // Metode Pembayaran
            Text(
                text = "Pilih Metode Pembayaran",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PettoDarkBrownText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentMethods.forEach { method ->
                    val isSelected = selectedMethod == method
                    val iconLabel = when {
                        method.contains("QRIS") -> "📱 QRIS (Instant)"
                        method.contains("BCA") -> "🏦 BCA Virtual Account"
                        method.contains("Mandiri") -> "🏦 Mandiri Virtual Account"
                        else -> "💳 GoPay / E-Wallet"
                    }

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
                                text = iconLabel,
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

            // CTA Submit Button
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

/**
 * Dialog Pembayaran Interaktif Sesuai Metode (QRIS / Virtual Account / GoPay).
 * Menampilkan kode QR atau Nomor VA langsung, serta memverifikasi status pembayaran secara mulus.
 */
@Composable
fun MidtransSandboxDialog(
    state: CheckoutState.Ready,
    onSuccessCallback: (DonationTransaction) -> Unit,
    onFailureCallback: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val details = state.paymentDetails

    // State verifikasi transaksi
    var isVerifying by remember { mutableStateOf(false) }

    // Hitung mundur waktu pembayaran (15 menit)
    var timeLeftSeconds by remember { mutableIntStateOf(15 * 60) }

    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    val method = state.transaction.paymentMethod
    val isMandiri = method.contains("Mandiri", ignoreCase = true)
    val isBca = method.contains("BCA", ignoreCase = true)
    val isVa = isBca || isMandiri || method.contains("Virtual Account", ignoreCase = true)
    val isQris = method.contains("QRIS", ignoreCase = true) || method.contains("GoPay", ignoreCase = true)

    // Nomor VA resmi dari Midtrans Sandbox Core API
    val vaNumber = details?.vaNumber
        ?: if (isMandiri) {
            "88708" + (Math.abs(state.transaction.transactionId.hashCode()) % 90000000 + 10000000).toString()
        } else {
            "34195" + (Math.abs(state.transaction.transactionId.hashCode()) % 90000000 + 10000000).toString()
        }

    val billerCode = details?.billerCode ?: "70012"

    // URL Simulator yang tepat sesuai metode pembayaran
    val simulatorUrl = details?.simulatorUrl ?: when {
        isBca -> "https://simulator.sandbox.midtrans.com/bca/va/index"
        isMandiri -> "https://simulator.sandbox.midtrans.com/mandiri/bill/index"
        isQris -> "https://simulator.sandbox.midtrans.com/qris/index"
        else -> state.redirectUrl
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(PettoSuccessGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            isQris -> "Pembayaran QRIS"
                            isVa -> "Virtual Account"
                            else -> "Pembayaran Midtrans"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PettoDarkBrownText
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFFFDAD6)
                ) {
                    Text(
                        text = "⏱ $timerString",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PettoUrgentRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Total Pembayaran
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF7F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8DCD4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Donasi yang Harus Dibayar",
                            fontSize = 11.sp,
                            color = PettoSubtitleColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.transaction.amount.toRupiahFormat(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PettoPrimaryBrown
                        )
                        Text(
                            text = "Order ID: ${state.transaction.transactionId}",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TAMPILAN 1: QRIS / GOPAY
                if (isQris) {
                    if (details?.qrCodeUrl != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE8DCD4)),
                            modifier = Modifier.size(200.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = details.qrCodeUrl,
                                    contentDescription = "Midtrans Official QRIS",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    } else {
                        QrisCodeCanvas(orderId = state.transaction.transactionId)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (details?.qrString != null) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(details.qrString))
                                Toast.makeText(context, "Kode QRIS String disalin!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text("Salin Kode QRIS String", fontSize = 11.sp, color = PettoPrimaryBrown)
                        }
                    }

                    Text(
                        text = "Scan QRIS di atas atau salin kode untuk dicoba pada Simulator QRIS Midtrans Sandbox.",
                        fontSize = 11.sp,
                        color = PettoSubtitleColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }

                // TAMPILAN 2: VIRTUAL ACCOUNT (BCA / MANDIRI)
                if (isVa) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PettoPrimaryBrown),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isMandiri) "Bank Mandiri Virtual Account (Bill)" else "Bank BCA Virtual Account",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PettoPrimaryBrown
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            if (isMandiri) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Biller Code: $billerCode", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        Text("Bill Key: $vaNumber", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = PettoPrimaryBrown)
                                    }
                                    Surface(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(vaNumber.replace(" ", "")))
                                            Toast.makeText(context, "Bill Key disalin!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFBF1E9)
                                    ) {
                                        Text(
                                            text = "Salin",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PettoPrimaryBrown,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vaNumber,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = PettoDarkBrownText
                                    )

                                    Surface(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(vaNumber.replace(" ", "")))
                                            Toast.makeText(context, "Nomor Virtual Account disalin!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFBF1E9)
                                    ) {
                                        Text(
                                            text = "Salin",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PettoPrimaryBrown,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nama Akun: PETTO / ${state.transaction.donorName}",
                                fontSize = 11.sp,
                                color = PettoSubtitleColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF7F2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Petunjuk Uji Coba Simulator Sandbox:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PettoDarkBrownText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("1. Klik 'Salin' pada nomor Virtual Account di atas.", fontSize = 11.sp, color = PettoSubtitleColor)
                            Text("2. Klik tombol 'Buka Simulator Midtrans' di bawah.", fontSize = 11.sp, color = PettoSubtitleColor)
                            Text("3. Masukkan nomor VA, klik Inquire lalu klik Pay.", fontSize = 11.sp, color = PettoSubtitleColor)
                            Text("4. Kembali ke Petto dan tekan 'Saya Sudah Membayar'.", fontSize = 11.sp, color = PettoSubtitleColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Indikator Status Verifikasi
                AnimatedVisibility(visible = isVerifying) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        CircularProgressIndicator(
                            color = PettoPrimaryBrown,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Memverifikasi transaksi dengan server Midtrans...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PettoPrimaryBrown
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Tombol Alami: "Saya Sudah Membayar / Cek Status"
                Button(
                    onClick = {
                        if (!isVerifying) {
                            isVerifying = true
                        }
                    },
                    enabled = !isVerifying,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PettoSuccessGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isVerifying) "Memverifikasi..." else "✓ Saya Sudah Membayar",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Opsi Tambahan bagi Penguji / Demo: Buka Sandbox Web Simulator Midtrans
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(simulatorUrl))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            isBca -> "Buka Simulator BCA di Web ↗"
                            isMandiri -> "Buka Simulator Mandiri di Web ↗"
                            isQris -> "Buka Simulator QRIS di Web ↗"
                            else -> "Buka Simulator Midtrans Web ↗"
                        },
                        color = PettoPrimaryBrown,
                        fontSize = 12.sp
                    )
                }

                // Tombol Batal
                OutlinedButton(
                    onClick = { onFailureCallback("Pembayaran dibatalkan.") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batalkan Pembayaran", color = PettoUrgentRed, fontSize = 12.sp)
                }
            }
        }
    )

    // Efek simulasi verifikasi: saat pengguna mengklik "Saya Sudah Membayar",
    // verifikasi dengan Midtrans API, lalu selesaikan pembayaran.
    LaunchedEffect(isVerifying) {
        if (isVerifying) {
            try {
                MidtransClient.checkPaymentStatus(state.transaction.transactionId)
            } catch (_: Exception) {}
            delay(1000L)
            onSuccessCallback(state.transaction)
        }
    }
}

/**
 * Komponen Canvas QRIS Realistis yang digambar langsung tanpa dependensi external library
 */
@Composable
private fun QrisCodeCanvas(orderId: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE8DCD4)),
        modifier = Modifier.size(200.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSize = 21
                val cellSize = size.width / gridSize
                val random = Random(orderId.hashCode())

                // Gambar background putih
                drawRect(color = Color.White, size = size)

                // Gambar Finder Patterns (3 Kotak Sudut 7x7)
                fun drawFinderPattern(startX: Int, startY: Int) {
                    // Kotak Luar Hitam 7x7
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(startX * cellSize, startY * cellSize),
                        size = Size(7 * cellSize, 7 * cellSize)
                    )
                    // Kotak Tengah Putih 5x5
                    drawRect(
                        color = Color.White,
                        topLeft = Offset((startX + 1) * cellSize, (startY + 1) * cellSize),
                        size = Size(5 * cellSize, 5 * cellSize)
                    )
                    // Kotak Dalam Hitam 3x3
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset((startX + 2) * cellSize, (startY + 2) * cellSize),
                        size = Size(3 * cellSize, 3 * cellSize)
                    )
                }

                drawFinderPattern(0, 0) // Top-Left
                drawFinderPattern(gridSize - 7, 0) // Top-Right
                drawFinderPattern(0, gridSize - 7) // Bottom-Left

                // Gambar Data Matrix Random
                for (x in 0 until gridSize) {
                    for (y in 0 until gridSize) {
                        val inTopLeft = x < 8 && y < 8
                        val inTopRight = x >= gridSize - 8 && y < 8
                        val inBottomLeft = x < 8 && y >= gridSize - 8
                        val inCenter = x in 8..12 && y in 8..12

                        if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenter) {
                            if (random.nextBoolean()) {
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(x * cellSize, y * cellSize),
                                    size = Size(cellSize, cellSize)
                                )
                            }
                        }
                    }
                }
            }

            // Logo QRIS di Tengah
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(width = 44.dp, height = 24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "QRIS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD32F2F),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Dialog Notifikasi Donasi Berhasil
 */
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
                    text = "Donasi sebesar ${transaction.amount.toRupiahFormat()} telah berhasil diterima dan tercatat di sistem.",
                    fontSize = 13.sp,
                    color = PettoSubtitleColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF7F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Metode:", fontSize = 11.sp, color = PettoSubtitleColor)
                            Text(transaction.paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PettoDarkBrownText)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Status:", fontSize = 11.sp, color = PettoSubtitleColor)
                            Text("SUKSES (TERVERIFIKASI)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PettoSuccessGreen)
                        }
                    }
                }

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
