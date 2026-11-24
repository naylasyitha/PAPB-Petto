package com.papb.petto.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import com.papb.petto.data.remote.midtrans.MidtransClient
import com.papb.petto.data.repository.DonationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class DonationViewModel(
    private val repository: DonationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationUiState(isLoading = true))
    val uiState: StateFlow<DonationUiState> = _uiState.asStateFlow()

    private var recentDonorsJob: Job? = null

    init {
        observeCampaigns()
        syncData()
    }

    private fun observeCampaigns() {
        viewModelScope.launch {
            repository.observeCampaigns().collect { list ->
                _uiState.update { currentState ->
                    val filtered = applyFilter(list, currentState.searchQuery, currentState.selectedCategory)
                    currentState.copy(
                        campaigns = list,
                        filteredCampaigns = filtered,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            try {
                repository.syncCampaigns()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Jangan timpa data Room lokal jika jaringan bermasalah
            } finally {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            val filtered = applyFilter(currentState.campaigns, query, currentState.selectedCategory)
            currentState.copy(searchQuery = query, filteredCampaigns = filtered)
        }
    }

    fun onSelectCategory(category: String) {
        _uiState.update { currentState ->
            val filtered = applyFilter(currentState.campaigns, currentState.searchQuery, category)
            currentState.copy(selectedCategory = category, filteredCampaigns = filtered)
        }
    }

    fun selectCampaign(campaignId: String) {
        val campaign = _uiState.value.campaigns.find { it.id == campaignId }
        _uiState.update { it.copy(selectedCampaign = campaign) }

        recentDonorsJob?.cancel()
        recentDonorsJob = viewModelScope.launch {
            repository.observeRecentDonors(campaignId, limit = 10).collect { donors ->
                _uiState.update { it.copy(recentDonors = donors) }
            }
        }
    }

    fun clearSelectedCampaign() {
        recentDonorsJob?.cancel()
        _uiState.update { it.copy(selectedCampaign = null, recentDonors = emptyList()) }
    }

    private fun applyFilter(
        campaigns: List<DonationCampaign>,
        query: String,
        category: String
    ): List<DonationCampaign> {
        val trimmedQuery = query.trim().lowercase()

        return campaigns.filter { campaign ->
            val matchesQuery = if (trimmedQuery.isBlank()) {
                true
            } else {
                campaign.title.lowercase().contains(trimmedQuery) ||
                        campaign.shelterName.lowercase().contains(trimmedQuery) ||
                        campaign.location.lowercase().contains(trimmedQuery)
            }

            val matchesCategory = when (category) {
                "All", "Urgent First" -> true
                else -> campaign.category.equals(category, ignoreCase = true)
            }

            matchesQuery && matchesCategory
        }
    }

    fun initiateDonation(
        campaign: DonationCampaign,
        donorName: String,
        amount: Double,
        paymentMethod: String,
        message: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(checkoutState = CheckoutState.Loading) }

            val orderId = "PETTO-${System.currentTimeMillis()}-${(100..999).random()}"
            val result = MidtransClient.requestSnapToken(
                orderId = orderId,
                amount = amount,
                donorName = donorName.ifBlank { "Hamba Allah" },
                campaignId = campaign.id,
                campaignTitle = campaign.title
            )

            result.fold(
                onSuccess = { snapResponse ->
                    val pendingTransaction = DonationTransaction(
                        transactionId = orderId,
                        campaignId = campaign.id,
                        donorName = donorName.ifBlank { "Hamba Allah" },
                        amount = amount,
                        paymentMethod = paymentMethod,
                        status = "PENDING",
                        message = message,
                        isSyncedToCloud = false,
                        timestamp = System.currentTimeMillis()
                    )
                    _uiState.update {
                        it.copy(
                            checkoutState = CheckoutState.Ready(
                                snapToken = snapResponse.token,
                                redirectUrl = snapResponse.redirectUrl,
                                transaction = pendingTransaction
                            )
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(checkoutState = CheckoutState.Failed(err.message ?: "Gagal memproses pembayaran"))
                    }
                }
            )
        }
    }

    fun onPaymentSuccess(transaction: DonationTransaction) {
        viewModelScope.launch {
            val successTransaction = transaction.copy(status = "SUCCESS")
            try {
                repository.recordDonationTransaction(successTransaction)
                _uiState.update { it.copy(checkoutState = CheckoutState.Success(successTransaction)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(checkoutState = CheckoutState.Failed("Gagal mencatat transaksi: ${e.message}"))
                }
            }
        }
    }

    fun onPaymentFailed(reason: String = "Pembayaran dibatalkan atau gagal.") {
        _uiState.update { it.copy(checkoutState = CheckoutState.Failed(reason)) }
    }

    fun dismissCheckout() {
        _uiState.update { it.copy(checkoutState = CheckoutState.Idle) }
    }

    companion object {
        fun factory(repository: DonationRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DonationViewModel(repository) as T
                }
            }
    }
}
