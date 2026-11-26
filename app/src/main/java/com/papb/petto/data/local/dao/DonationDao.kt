package com.papb.petto.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.papb.petto.data.local.entity.DonationCampaignEntity
import com.papb.petto.data.local.entity.DonationTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationCampaignDao {
    @Query("SELECT * FROM donation_campaigns")
    fun observeAllCampaigns(): Flow<List<DonationCampaignEntity>>

    @Query("SELECT * FROM donation_campaigns WHERE id = :id")
    fun observeCampaignById(id: String): Flow<DonationCampaignEntity?>

    @Query("SELECT * FROM donation_campaigns WHERE id = :id LIMIT 1")
    suspend fun getCampaignById(id: String): DonationCampaignEntity?

    @Query("SELECT * FROM donation_campaigns")
    suspend fun getAllCampaigns(): List<DonationCampaignEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCampaigns(campaigns: List<DonationCampaignEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCampaign(campaign: DonationCampaignEntity)

    @Query("UPDATE donation_campaigns SET status = :status WHERE id = :id")
    suspend fun updateCampaignStatus(id: String, status: String)

    @Query("UPDATE donation_campaigns SET collectedAmount = :newCollectedAmount WHERE id = :id")
    suspend fun updateCollectedAmount(id: String, newCollectedAmount: Double)

    @Query("SELECT COUNT(*) FROM donation_campaigns")
    suspend fun getCampaignCount(): Int
}

@Dao
interface DonationTransactionDao {
    @Query("SELECT * FROM donation_transactions WHERE campaignId = :campaignId ORDER BY timestamp DESC")
    fun observeTransactionsForCampaign(campaignId: String): Flow<List<DonationTransactionEntity>>

    @Query("SELECT * FROM donation_transactions WHERE campaignId = :campaignId AND status = 'SUCCESS' ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentSuccessfulDonors(campaignId: String, limit: Int = 10): Flow<List<DonationTransactionEntity>>

    @Query("SELECT * FROM donation_transactions WHERE campaignId = :campaignId AND status = 'SUCCESS' ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSuccessfulTransactions(campaignId: String, limit: Int = 10): List<DonationTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: DonationTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransactions(transactions: List<DonationTransactionEntity>)

    @Query("SELECT * FROM donation_transactions WHERE isSyncedToCloud = 0")
    suspend fun getUnsyncedTransactions(): List<DonationTransactionEntity>

    @Query("UPDATE donation_transactions SET isSyncedToCloud = 1 WHERE transactionId = :transactionId")
    suspend fun markAsSynced(transactionId: String)

    @Query("SELECT SUM(amount) FROM donation_transactions WHERE campaignId = :campaignId AND status = 'SUCCESS'")
    suspend fun getTotalSuccessfulDonationsForCampaign(campaignId: String): Double?
}
