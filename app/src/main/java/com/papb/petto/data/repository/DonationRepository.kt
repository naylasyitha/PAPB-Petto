package com.papb.petto.data.repository

import com.papb.petto.data.model.DonationCampaign
import com.papb.petto.data.model.DonationTransaction
import kotlinx.coroutines.flow.Flow

interface DonationRepository {

    fun observeCampaigns(): Flow<List<DonationCampaign>>

    fun observeCampaignById(id: String): Flow<DonationCampaign?>

    fun observeRecentDonors(campaignId: String, limit: Int = 10): Flow<List<DonationTransaction>>

    suspend fun getCampaignById(id: String): DonationCampaign?

    suspend fun syncCampaigns(): Result<Unit>

    suspend fun evaluateCampaign(campaignId: String): DonationCampaign?

    suspend fun recordDonationTransaction(transaction: DonationTransaction): Result<Unit>

    suspend fun syncPendingTransactions(): Result<Unit>

    fun calculateUrgencyScore(campaign: DonationCampaign, currentTimeMillis: Long = System.currentTimeMillis()): Double
}
