package com.papb.petto.ui.donation

import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.remote.midtrans.MidtransPaymentDetails

sealed interface CheckoutState {
    data object Idle : CheckoutState
    data object Loading : CheckoutState
    data class Ready(
        val snapToken: String = "",
        val redirectUrl: String = "",
        val transaction: DonationTransaction,
        val paymentDetails: MidtransPaymentDetails? = null
    ) : CheckoutState
    data class Success(val transaction: DonationTransaction) : CheckoutState
    data class Failed(val message: String) : CheckoutState
}

data class DonationUiState(
    val campaigns: List<DonationCampaign> = emptyList(),
    val filteredCampaigns: List<DonationCampaign> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All", // "All", "Urgent First", "Emergency Medical", "Food & Medical", "Shelter Renovation"
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val selectedCampaign: DonationCampaign? = null,
    val recentDonors: List<DonationTransaction> = emptyList(),
    val checkoutState: CheckoutState = CheckoutState.Idle
)
