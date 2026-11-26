package com.papb.petto.ui.donation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.data.local.DatabaseProvider
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.repository.DefaultDonationRepository
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.theme.PettoDarkBrownText
import com.papb.petto.ui.theme.PettoPrimaryBrown

@Composable
fun DonationFlowScreen(
    onNavigateBackToHome: () -> Unit,
    currentTab: BottomTab = BottomTab.DONATION,
    onTabSelected: (BottomTab) -> Unit = {}
) {
    val context = LocalContext.current

    val repository = remember {
        val db = DatabaseProvider.getDatabase(context)
        DefaultDonationRepository(
            campaignDao = db.donationCampaignDao(),
            transactionDao = db.donationTransactionDao()
        )
    }

    val viewModel: DonationViewModel = viewModel(
        factory = DonationViewModel.factory(repository)
    )

    val uiState by viewModel.uiState.collectAsState()

    var activeDonationCampaign by remember { mutableStateOf<DonationCampaign?>(null) }
    var initialAmount by remember { mutableDoubleStateOf(50_000.0) }
    var showInputSheet by remember { mutableStateOf(false) }

    if (uiState.selectedCampaign != null) {
        CampaignDetailScreen(
            campaign = uiState.selectedCampaign!!,
            recentDonors = uiState.recentDonors,
            onBack = { viewModel.clearSelectedCampaign() },
            onDonateNowClick = { campaign ->
                activeDonationCampaign = campaign
                initialAmount = 50_000.0
                showInputSheet = true
            }
        )
    } else {
        DonationScreen(
            uiState = uiState,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onSelectCategory = viewModel::onSelectCategory,
            onCampaignClick = { campaignId -> viewModel.selectCampaign(campaignId) },
            onDonateNowClick = { campaign ->
                activeDonationCampaign = campaign
                initialAmount = 50_000.0
                showInputSheet = true
            },
            onQuickDonate = { campaign, amount ->
                activeDonationCampaign = campaign
                initialAmount = amount
                showInputSheet = true
            },
            onBack = onNavigateBackToHome,
            currentTab = currentTab,
            onTabSelected = onTabSelected
        )
    }

    if (showInputSheet && activeDonationCampaign != null) {
        DonationInputBottomSheet(
            campaign = activeDonationCampaign!!,
            initialAmount = initialAmount,
            onDismissRequest = { showInputSheet = false },
            onSubmitDonation = { name, amount, method, message ->
                showInputSheet = false
                viewModel.initiateDonation(
                    campaign = activeDonationCampaign!!,
                    donorName = name,
                    amount = amount,
                    paymentMethod = method,
                    message = message
                )
            }
        )
    }

    when (val checkout = uiState.checkoutState) {
        is CheckoutState.Ready -> {
            MidtransSandboxDialog(
                state = checkout,
                onSuccessCallback = { trx -> viewModel.onPaymentSuccess(trx) },
                onFailureCallback = { reason -> viewModel.onPaymentFailed(reason) },
                onDismissRequest = { viewModel.dismissCheckout() }
            )
        }
        is CheckoutState.Success -> {
            DonationSuccessDialog(
                transaction = checkout.transaction,
                onDismiss = { viewModel.dismissCheckout() }
            )
        }
        is CheckoutState.Failed -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissCheckout() },
                title = { Text("Status Pembayaran", color = PettoDarkBrownText) },
                text = { Text(checkout.message) },
                confirmButton = {
                    Button(
                        onClick = { viewModel.dismissCheckout() },
                        colors = ButtonDefaults.buttonColors(containerColor = PettoPrimaryBrown)
                    ) {
                        Text("Tutup", color = Color.White)
                    }
                }
            )
        }
        else -> Unit
    }
}
